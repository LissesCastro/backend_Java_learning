package com.emprestimosCaixa.backend.services.impl;

import com.emprestimosCaixa.backend.services.AmortizacaoService;
import com.emprestimosCaixa.backend.shared.dto.output.ParcelaDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service("sacService")
public class SACServiceImpl implements AmortizacaoService {

    @Override
    public List<ParcelaDTO> calcularParcelas(BigDecimal valor, int prazo, BigDecimal taxaJuros) {
        List<ParcelaDTO> parcelas = new ArrayList<>();

        // Amortização constante
        BigDecimal amortizacao = valor.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= prazo; i++) {
            // Saldo devedor no início da parcela
            BigDecimal saldoDevedor = valor.subtract(amortizacao.multiply(BigDecimal.valueOf(i - 1)));

            // Juros da parcela = saldo devedor * taxa de juros
            BigDecimal juros = saldoDevedor.multiply(taxaJuros).setScale(2, RoundingMode.HALF_UP);

            // Prestação = amortização + juros
            BigDecimal prestacao = amortizacao.add(juros).setScale(2, RoundingMode.HALF_UP);

            // Criar a parcela
            ParcelaDTO parcela = ParcelaDTO.builder()
                    .numero(i)
                    .valorAmortizacao(amortizacao)
                    .valorJuros(juros)
                    .valorPrestacao(prestacao)
                    .build();

            parcelas.add(parcela);
        }

        return parcelas;
    }
}