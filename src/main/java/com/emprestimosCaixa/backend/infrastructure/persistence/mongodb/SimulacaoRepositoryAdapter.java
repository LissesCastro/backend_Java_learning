package com.emprestimosCaixa.backend.infrastructure.persistence.mongodb;

import com.emprestimosCaixa.backend.domain.model.Simulacao;
import com.emprestimosCaixa.backend.domain.repository.SimulacaoRepository;
import com.emprestimosCaixa.backend.shared.dto.output.ParcelaDTO;
import com.emprestimosCaixa.backend.shared.dto.output.ResumoProdutosDTO;
import com.emprestimosCaixa.backend.shared.dto.output.SimulacaoResumoDTO;
import com.emprestimosCaixa.backend.shared.dto.output.VolumeSimuladoDiaDTO;
import com.emprestimosCaixa.backend.shared.dto.response.SimulacaoResponse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

interface SimulacaoMongoRepository extends MongoRepository<Simulacao, String> {
    boolean existsByResultado_IdSimulacao(int idSimulacao);
}

@Repository
public class SimulacaoRepositoryAdapter implements SimulacaoRepository {

    private final SimulacaoMongoRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    public SimulacaoRepositoryAdapter(SimulacaoMongoRepository mongoRepository,
                                      MongoTemplate mongoTemplate) {
        this.mongoRepository = mongoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void salvar(BigDecimal valorDesejado, int prazo, SimulacaoResponse response) {
        Simulacao dadosParaPersistir = new Simulacao(valorDesejado, prazo, response);
        mongoRepository.save(dadosParaPersistir);
    }

    @Override
    public boolean existePorId(int idSimulacao) {
        return mongoRepository.existsByResultado_IdSimulacao(idSimulacao);
    }

    @Override
    public List<SimulacaoResumoDTO> buscarResumos() {
        List<Simulacao> listaCompleta = mongoRepository.findAll();

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
    public VolumeSimuladoDiaDTO buscarVolumePorDia(LocalDate data, Optional<Integer> codigoProduto) {
        Criteria criteria = Criteria.where("dataSimulacao")
                .gte(data.atStartOfDay())
                .lt(data.plusDays(1).atStartOfDay());

        if (codigoProduto.isPresent()) {
            criteria.and("resultado.codigoProduto").is(codigoProduto.get());
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.addFields().addField("valorDesejadoNumerico")
                        .withValue(ConvertOperators.ToDouble.toDouble("$valorDesejado")).build(),
                Aggregation.unwind("resultado.resultadoSimulacao"),
                Aggregation.unwind("resultado.resultadoSimulacao.parcelas"),
                Aggregation.addFields()
                        .addField("valorPrestacaoNumerico")
                        .withValue(ConvertOperators.ToDouble.toDouble("$resultado.resultadoSimulacao.parcelas.valorPrestacao"))
                        .addField("taxaJurosNumerica")
                        .withValue(ConvertOperators.ToDouble.toDouble("$resultado.taxaJuros"))
                        .build(),
                Aggregation.group("resultado.codigoProduto", "resultado.descricaoProduto")
                        .sum("valorDesejadoNumerico").as("valorTotalDesejado")
                        .avg("taxaJurosNumerica").as("taxaMediaJuros")
                        .sum("valorPrestacaoNumerico").as("valorTotalCredito")
                        .avg("valorPrestacaoNumerico").as("valorMedioPrestacao"),
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