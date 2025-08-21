package com.emprestimosCaixa.backend.services.impl;

import com.emprestimosCaixa.backend.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.dto.output.*;
import com.emprestimosCaixa.backend.model.Produto;
import com.emprestimosCaixa.backend.repository.ProdutoRepository;
import com.emprestimosCaixa.backend.repository.SimulacaoRepository; // Importação atualizada
import com.emprestimosCaixa.backend.services.PRICEService;
import com.emprestimosCaixa.backend.services.SACService;
import com.emprestimosCaixa.backend.services.SimulacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SimulacaoServiceImpl implements SimulacaoService {

    private final AtomicInteger idSimulacaoGenerator = new AtomicInteger(1);

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private SACService sacService;

    @Autowired
    private PRICEService priceService;

    @Autowired
    private SimulacaoRepository simulacaoRepository; // Injeção de dependência atualizada

    @Override
    public SimulacaoResponse simular(SimulacaoRequest request) {
        Produto produto = produtoRepository.findProdutoByValorAndPrazo(request.getValorDesejado(), request.getPrazo())
                .orElseThrow(() -> new RuntimeException("Nenhum produto de crédito encontrado para as condições solicitadas."));

        List<ParcelaDTO> parcelasSAC = sacService.calcularParcelas(request.getValorDesejado(), request.getPrazo(), produto.getPcTaxaJuros());
        List<ParcelaDTO> parcelasPRICE = priceService.calcularParcelas(request.getValorDesejado(), request.getPrazo(), produto.getPcTaxaJuros());

        List<AmortizacaoDTO> resultadoSimulacao = new ArrayList<>();
        resultadoSimulacao.add(new AmortizacaoDTO("SAC", parcelasSAC));
        resultadoSimulacao.add(new AmortizacaoDTO("PRICE", parcelasPRICE));

        int novoId = idSimulacaoGenerator.getAndIncrement();

        SimulacaoResponse response = new SimulacaoResponse(
                novoId,
                produto.getCoProduto(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                resultadoSimulacao
        );

        SimulacaoCompletaDTO dadosParaPersistir = new SimulacaoCompletaDTO(
                request.getValorDesejado(),
                request.getPrazo(),
                response
        );

        simulacaoRepository.persistir(dadosParaPersistir); // Usando o novo repositório

        return response;
    }

    @Override
    public List<SimulacaoResumoDTO> listarTodas() {
        List<SimulacaoCompletaDTO> listaCompleta = simulacaoRepository.lerTodas(); // Usando o novo repositório

        return listaCompleta.stream()
                .map(dadosCompletos -> {
                    BigDecimal valorTotalParcelas = dadosCompletos.getResultado().getResultadoSimulacao()
                            .stream()
                            .filter(amortizacao -> "SAC".equalsIgnoreCase(amortizacao.getTipo()))
                            .findFirst()
                            .map(amortizacao -> amortizacao.getParcelas().stream()
                                    .map(ParcelaDTO::getValorPrestacao)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                            .orElse(BigDecimal.ZERO);

                    return new SimulacaoResumoDTO(
                            dadosCompletos.getResultado().getIdSimulacao(),
                            dadosCompletos.getValorDesejado(),
                            dadosCompletos.getPrazo(),
                            valorTotalParcelas
                    );
                })
                .collect(Collectors.toList());
    }
}