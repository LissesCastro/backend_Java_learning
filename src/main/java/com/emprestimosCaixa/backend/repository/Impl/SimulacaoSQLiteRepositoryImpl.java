package com.emprestimosCaixa.backend.repository.Impl;

import com.emprestimosCaixa.backend.dto.output.SimulacaoCompletaDTO;
import com.emprestimosCaixa.backend.dto.response.SimulacaoResponse;
import com.emprestimosCaixa.backend.repository.secondary.SimulacaoLeituraRepository;
import com.emprestimosCaixa.backend.repository.secondary.SimulacaoPersistenciaRepository;
import com.emprestimosCaixa.backend.sqlite.model.Simulacao;
// --- IMPORT CORRIGIDO ---
import com.emprestimosCaixa.backend.sqlite.repository.SimulacaoSQLiteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class SimulacaoSQLiteRepositoryImpl implements SimulacaoLeituraRepository, SimulacaoPersistenciaRepository {

    @Autowired
    @Lazy
    private SimulacaoSQLiteRepository jpaRepository; // Agora esta injeção vai funcionar

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void persistir(SimulacaoCompletaDTO novaSimulacao) {
        try {
            String resultadoJson = objectMapper.writeValueAsString(novaSimulacao.getResultado());
            Simulacao entity = new Simulacao(
                    novaSimulacao.getValorDesejado(),
                    novaSimulacao.getPrazo(),
                    resultadoJson
            );
            jpaRepository.save(entity);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter a simulação para JSON antes de salvar no SQLite.");
            e.printStackTrace();
        }
    }

    @Override
    public List<SimulacaoCompletaDTO> lerTodas() {
        return jpaRepository.findAll().stream()
                .map(entity -> {
                    try {
                        SimulacaoResponse resultado = objectMapper.readValue(entity.getResultadoJson(), SimulacaoResponse.class);
                        return new SimulacaoCompletaDTO(
                                entity.getValorDesejado(),
                                entity.getPrazo(),
                                resultado
                        );
                    } catch (JsonProcessingException e) {
                        System.err.println("Erro ao ler JSON do banco para a simulação ID: " + entity.getIdSimulacao());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}