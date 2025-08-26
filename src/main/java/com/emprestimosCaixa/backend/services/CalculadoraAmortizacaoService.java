package com.emprestimosCaixa.backend.services;

import com.emprestimosCaixa.backend.shared.dto.output.AmortizacaoDTO;
import com.emprestimosCaixa.backend.shared.dto.output.ParcelaDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculadoraAmortizacaoService {

    private final AmortizacaoService sacService;
    private final AmortizacaoService priceService;

    public CalculadoraAmortizacaoService(
            @Qualifier("sacService") AmortizacaoService sacService,
            @Qualifier("priceService") AmortizacaoService priceService) {
        this.sacService = sacService;
        this.priceService = priceService;
    }

    public List<AmortizacaoDTO> calcularAmortizacoes(BigDecimal valor, int prazo, BigDecimal taxaJuros) {
        List<ParcelaDTO> parcelasSAC = sacService.calcularParcelas(valor, prazo, taxaJuros);
        List<ParcelaDTO> parcelasPRICE = priceService.calcularParcelas(valor, prazo, taxaJuros);

        List<AmortizacaoDTO> resultados = new ArrayList<>();
        resultados.add(new AmortizacaoDTO("SAC", parcelasSAC));
        resultados.add(new AmortizacaoDTO("PRICE", parcelasPRICE));

        return resultados;
    }
}