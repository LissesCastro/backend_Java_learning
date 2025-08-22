package com.emprestimosCaixa.backend.services.impl;

import com.emprestimosCaixa.backend.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.dto.output.*;
import com.emprestimosCaixa.backend.dto.response.SimulacaoResponse;
import com.emprestimosCaixa.backend.model.Produto;
import com.emprestimosCaixa.backend.repository.primary.ProdutoRepository;
// --- IMPORTAÇÕES CORRIGIDAS ---
import com.emprestimosCaixa.backend.repository.secondary.SimulacaoLeituraRepository;
import com.emprestimosCaixa.backend.repository.secondary.SimulacaoPersistenciaRepository;
import com.emprestimosCaixa.backend.services.AmortizacaoService;
import com.emprestimosCaixa.backend.services.SimulacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    // --- INJEÇÕES DE DEPENDÊNCIA CORRIGIDAS ---
    @Autowired
    private SimulacaoLeituraRepository leituraRepository;

    @Autowired
    private SimulacaoPersistenciaRepository persistenciaRepository;

    @Autowired
    @Qualifier("sacService")
    private AmortizacaoService sacService;

    @Autowired
    @Qualifier("priceService")
    private AmortizacaoService priceService;

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

        // --- USO DO REPOSITÓRIO CORRIGIDO ---
        persistenciaRepository.persistir(dadosParaPersistir);

        return response;
    }

    @Override
    public List<SimulacaoResumoDTO> listarTodas() {
        // --- USO DO REPOSITÓRIO CORRIGIDO ---
        List<SimulacaoCompletaDTO> listaCompleta = leituraRepository.lerTodas();

        return listaCompleta.stream()
                .map(dadosCompletos -> {
                    // Lógica para calcular o valor total das parcelas (exemplo usando SAC)
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