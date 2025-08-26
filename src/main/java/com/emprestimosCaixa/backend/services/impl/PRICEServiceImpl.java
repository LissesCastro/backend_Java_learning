package com.emprestimosCaixa.backend.services.impl;

import com.emprestimosCaixa.backend.services.AmortizacaoService;
import com.emprestimosCaixa.backend.shared.dto.output.ParcelaDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service("priceService")
public class PRICEServiceImpl implements AmortizacaoService {

    @Override
    public List<ParcelaDTO> calcularParcelas(BigDecimal valor, int prazo, BigDecimal taxaJuros) {
        List<ParcelaDTO> parcelas = new ArrayList<>();

        // Fórmula da prestação fixa (Price):
        // PMT = [ P * i ] / [ 1 - (1 + i)^(-n) ]
        BigDecimal i = taxaJuros; // taxa de juros ao mês
        BigDecimal umMaisI = BigDecimal.ONE.add(i);

        BigDecimal numerador = valor.multiply(i);
        BigDecimal denominador = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(umMaisI.pow(prazo), 10, RoundingMode.HALF_UP)
        );

        BigDecimal prestacaoFixa = numerador.divide(denominador, 2, RoundingMode.HALF_UP);

        BigDecimal saldoDevedor = valor;

        for (int k = 1; k <= prazo; k++) {
            // Juros do mês = saldo devedor * taxa de juros
            BigDecimal juros = saldoDevedor.multiply(i).setScale(2, RoundingMode.HALF_UP);

            // Amortização = prestação fixa - juros
            BigDecimal amortizacao = prestacaoFixa.subtract(juros).setScale(2, RoundingMode.HALF_UP);

            // Atualiza saldo devedor
            saldoDevedor = saldoDevedor.subtract(amortizacao);

            // Cria a parcela
            ParcelaDTO parcela = ParcelaDTO.builder()
                    .numero(k)
                    .valorAmortizacao(amortizacao)
                    .valorJuros(juros)
                    .valorPrestacao(prestacaoFixa)
                    .build();

            parcelas.add(parcela);
        }

        return parcelas;
    }
}