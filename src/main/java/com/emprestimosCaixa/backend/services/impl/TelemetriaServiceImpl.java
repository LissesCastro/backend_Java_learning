package com.emprestimosCaixa.backend.services.impl;

import com.emprestimosCaixa.backend.dto.output.EndpointTelemetriaDTO;
import com.emprestimosCaixa.backend.dto.response.TelemetriaResponse;
import com.emprestimosCaixa.backend.mongo.repository.TelemetriaRepository;
import com.emprestimosCaixa.backend.services.TelemetriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators; // Importe
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators; // Importe
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TelemetriaServiceImpl implements TelemetriaService {

    @Autowired
    private TelemetriaRepository telemetriaRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public TelemetriaResponse obterTelemetriaPorDia(LocalDate data, Optional<String> nomeApi) {
        Criteria criteria = Criteria.where("dataHoraRequisicao")
                .gte(data.atStartOfDay())
                .lt(data.plusDays(1).atStartOfDay());

        if (nomeApi.isPresent()) {
            criteria.and("nomeApi").is(nomeApi.get());
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),

                Aggregation.group("nomeApi")
                        .count().as("qtdRequisicoes")
                        .avg("tempoResposta").as("tempoMedio")
                        .min("tempoResposta").as("tempoMinimo")
                        .max("tempoResposta").as("tempoMaximo")
                        .sum(ConditionalOperators.when(ComparisonOperators.Eq.valueOf("sucesso").equalToValue(true))
                                .then(1)
                                .otherwise(0)
                        ).as("qtdSucesso"),

                Aggregation.project()
                        .and("_id").as("nomeApi")
                        .and("qtdRequisicoes").as("qtdRequisicoes")
                        .and("tempoMedio").as("tempoMedio")
                        .and("tempoMinimo").as("tempoMinimo")
                        .and("tempoMaximo").as("tempoMaximo")
                        .and("qtdSucesso").as("qtdSucesso")
                        .andExpression("qtdSucesso / qtdRequisicoes").as("percentualSucesso")
                        .andExclude("_id")
        );

        AggregationResults<EndpointTelemetriaDTO> results = mongoTemplate.aggregate(
                aggregation, "telemetria", EndpointTelemetriaDTO.class
        );

        List<EndpointTelemetriaDTO> listaEndpoints = results.getMappedResults();

        if (listaEndpoints.isEmpty() && nomeApi.isPresent()) {
            long count = telemetriaRepository.findByDataHoraRequisicaoBetweenAndNomeApi(
                    data.atStartOfDay(), data.plusDays(1).atStartOfDay(), nomeApi.get()
            ).size();

            if (count == 0) {
                listaEndpoints.add(new EndpointTelemetriaDTO(nomeApi.get(), 0, 0.0, 0, 0, 0.0));
            }
        }

        return new TelemetriaResponse(data, listaEndpoints);
    }
}