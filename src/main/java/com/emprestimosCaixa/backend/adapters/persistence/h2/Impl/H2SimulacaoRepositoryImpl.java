package com.emprestimosCaixa.backend.adapters.persistence.h2.Impl;

import com.emprestimosCaixa.backend.adapters.persistence.h2.SimulacaoEntity;
import com.emprestimosCaixa.backend.adapters.persistence.h2.SimulacaoH2Repository;
import com.emprestimosCaixa.backend.dto.output.SimulacaoCompletaDTO;
import com.emprestimosCaixa.backend.repository.SimulacaoLeituraRepository;
import com.emprestimosCaixa.backend.repository.SimulacaoPersistenciaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class H2SimulacaoRepositoryImpl implements SimulacaoLeituraRepository, SimulacaoPersistenciaRepository {

    @Autowired
    private SimulacaoH2Repository jpaRepository; // Injeta o repositório JPA

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void persistir(SimulacaoCompletaDTO novaSimulacao) {
        try {
            String resultadoJson = objectMapper.writeValueAsString(novaSimulacao.getResultado());
            SimulacaoEntity entity = new SimulacaoEntity(
                    novaSimulacao.getValorDesejado(),
                    novaSimulacao.getPrazo(),
                    resultadoJson
            );
            jpaRepository.save(entity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SimulacaoCompletaDTO> lerTodas() {
        return jpaRepository.findAll().stream().map(entity -> {
            try {
                com.emprestimosCaixa.backend.dto.response.SimulacaoResponse resultado = objectMapper.readValue(entity.getResultadoJson(), com.emprestimosCaixa.backend.dto.response.SimulacaoResponse.class);
                return new SimulacaoCompletaDTO(
                        entity.getValorDesejado(),
                        entity.getPrazo(),
                        resultado
                );
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }
}