package com.emprestimosCaixa.backend.infrastructure.persistence.mongodb;

import com.emprestimosCaixa.backend.domain.model.Telemetria;
import com.emprestimosCaixa.backend.domain.repository.TelemetriaRepository;
import com.emprestimosCaixa.backend.shared.dto.output.EndpointTelemetriaDTO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

interface TelemetriaMongoRepository extends MongoRepository<Telemetria, String> {
}

@Repository
public class TelemetriaRepositoryAdapter implements TelemetriaRepository {

    private final TelemetriaMongoRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    public TelemetriaRepositoryAdapter(TelemetriaMongoRepository mongoRepository,
                                       MongoTemplate mongoTemplate) {
        this.mongoRepository = mongoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void registrar(String nomeApi, String endpoint, String metodoHttp,
                          long tempoResposta, int statusHttp, String errorMessage) {
        try {
            Telemetria telemetria = new Telemetria(
                    nomeApi, endpoint, metodoHttp, tempoResposta, statusHttp, errorMessage
            );
            mongoRepository.save(telemetria);
        } catch (Exception e) {
            System.err.println("Erro ao salvar telemetria: " + e.getMessage());
        }
    }

    @Override
    public List<EndpointTelemetriaDTO> buscarMetricasPorDia(LocalDate data, Optional<String> nomeApi) {
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

        return results.getMappedResults();
    }

    @Override
    public long contarTotal() {
        return mongoRepository.count();
    }
}