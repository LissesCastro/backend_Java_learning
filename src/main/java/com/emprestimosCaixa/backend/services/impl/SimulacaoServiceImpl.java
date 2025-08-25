package com.emprestimosCaixa.backend.services.impl;

import com.emprestimosCaixa.backend.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.dto.output.*;
import com.emprestimosCaixa.backend.dto.response.SimulacaoResponse;
import com.emprestimosCaixa.backend.model.Produto;
import com.emprestimosCaixa.backend.mongo.model.SimulacaoSalva;
import com.emprestimosCaixa.backend.mongo.repository.SimulacaoMongoRepository;
import com.emprestimosCaixa.backend.repository.primary.ProdutoRepository;
import com.emprestimosCaixa.backend.services.AmortizacaoService;
import com.emprestimosCaixa.backend.services.EventHubService;
import com.emprestimosCaixa.backend.services.SimulacaoService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SimulacaoServiceImpl implements SimulacaoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private SimulacaoMongoRepository simulacaoMongoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    @Qualifier("sacService")
    private AmortizacaoService sacService;

    @Autowired
    @Qualifier("priceService")
    private AmortizacaoService priceService;

    @Autowired
    private EventHubService eventHubService;

    @Override
    public SimulacaoResponse simular(SimulacaoRequest request) {
        Produto produto = produtoRepository.findProdutoByValorAndPrazo(request.getValorDesejado(), request.getPrazo())
                .orElseThrow(() -> new RuntimeException("Nenhum produto de crédito encontrado para as condições solicitadas."));

        List<ParcelaDTO> parcelasSAC = sacService.calcularParcelas(request.getValorDesejado(), request.getPrazo(), produto.getPcTaxaJuros());
        List<ParcelaDTO> parcelasPRICE = priceService.calcularParcelas(request.getValorDesejado(), request.getPrazo(), produto.getPcTaxaJuros());

        List<AmortizacaoDTO> resultadoSimulacao = new ArrayList<>();
        resultadoSimulacao.add(new AmortizacaoDTO("SAC", parcelasSAC));
        resultadoSimulacao.add(new AmortizacaoDTO("PRICE", parcelasPRICE));

        int novoId;
        Random random = new Random();
        do {
            novoId = 10000000 + random.nextInt(90000000);
        } while (simulacaoMongoRepository.existsByResultado_IdSimulacao(novoId));

        SimulacaoResponse response = new SimulacaoResponse(
                novoId,
                produto.getCoProduto(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                resultadoSimulacao
        );

        SimulacaoSalva dadosParaPersistir = new SimulacaoSalva(
                request.getValorDesejado(),
                request.getPrazo(),
                response
        );
        simulacaoMongoRepository.save(dadosParaPersistir);
        eventHubService.sendEvent(response);

        return response;
    }

    @Override
    public List<SimulacaoResumoDTO> listarTodas() {
        List<SimulacaoSalva> listaCompleta = simulacaoMongoRepository.findAll();

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

    @Override
    public VolumeSimuladoDiaDTO getVolumeSimuladoPorDia(LocalDate data, Optional<Integer> codigoProduto) {
        Criteria criteria = Criteria.where("dataSimulacao")
                .gte(data.atStartOfDay())
                .lt(data.plusDays(1).atStartOfDay());

        if (codigoProduto.isPresent()) {
            criteria.and("resultado.codigoProduto").is(codigoProduto.get());
        }

        Aggregation aggregation = Aggregation.newAggregation(
                // 1. Filtrar os documentos pela data e produto (opcional)
                Aggregation.match(criteria),

                // 2. Adicionar um campo para o valor desejado convertido para double
                Aggregation.addFields().addField("valorDesejadoNumerico")
                        .withValue(ConvertOperators.ToDouble.toDouble("$valorDesejado")).build(),

                // 3. Desdobrar o array de sistemas de amortização (SAC, PRICE)
                Aggregation.unwind("resultado.resultadoSimulacao"),

                // 4. Desdobrar o array de parcelas de cada sistema
                Aggregation.unwind("resultado.resultadoSimulacao.parcelas"),

                // 5. Adicionar campos para os valores das parcelas convertidos para double
                Aggregation.addFields()
                        .addField("valorPrestacaoNumerico")
                        .withValue(ConvertOperators.ToDouble.toDouble("$resultado.resultadoSimulacao.parcelas.valorPrestacao"))
                        .addField("taxaJurosNumerica")
                        .withValue(ConvertOperators.ToDouble.toDouble("$resultado.taxaJuros"))
                        .build(),

                // 6. Agrupar por produto e calcular as métricas
                Aggregation.group("resultado.codigoProduto", "resultado.descricaoProduto")
                        .sum("valorDesejadoNumerico").as("valorTotalDesejado")
                        .avg("taxaJurosNumerica").as("taxaMediaJuros")
                        .sum("valorPrestacaoNumerico").as("valorTotalCredito")
                        .avg("valorPrestacaoNumerico").as("valorMedioPrestacao"),

                // 7. Remodelar a saída para corresponder ao DTO
                Aggregation.project("valorTotalDesejado", "taxaMediaJuros", "valorTotalCredito", "valorMedioPrestacao")
                        .and("_id.codigoProduto").as("codigoProduto")
                        .and("_id.descricaoProduto").as("descricaoProduto")
                        .andExclude("_id")
        );

        AggregationResults<ResumoProdutosDTO> results = mongoTemplate.aggregate(
                aggregation, "simulacoes", ResumoProdutosDTO.class
        );

        return new VolumeSimuladoDiaDTO(data, results.getMappedResults());
    }
}