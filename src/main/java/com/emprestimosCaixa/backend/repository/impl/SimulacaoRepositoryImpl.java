package com.emprestimosCaixa.backend.repository.impl;

import com.emprestimosCaixa.backend.dto.output.SimulacaoCompletaDTO;
import com.emprestimosCaixa.backend.repository.SimulacaoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Repository; // Importante: mudámos de @Service para @Repository
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository // A anotação mudou para @Repository, que é a correta para esta camada.
public class SimulacaoRepositoryImpl implements SimulacaoRepository {

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final String CAMINHO_ARQUIVO_SIMULACOES = System.getProperty("user.home") + "/simulacoes.json";

    @Override
    public synchronized void persistir(SimulacaoCompletaDTO novaSimulacao) {
        List<SimulacaoCompletaDTO> simulacoesExistentes = lerTodas();
        simulacoesExistentes.add(novaSimulacao);

        try {
            File arquivoDeSimulacoes = new File(CAMINHO_ARQUIVO_SIMULACOES);
            objectMapper.writeValue(arquivoDeSimulacoes, simulacoesExistentes);
            System.out.println("Simulação adicionada com sucesso em: " + arquivoDeSimulacoes.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo de simulação.");
            e.printStackTrace();
        }
    }

    @Override
    public List<SimulacaoCompletaDTO> lerTodas() {
        File arquivoDeSimulacoes = new File(CAMINHO_ARQUIVO_SIMULACOES);
        if (!arquivoDeSimulacoes.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(arquivoDeSimulacoes, new TypeReference<List<SimulacaoCompletaDTO>>() {});
        } catch (IOException e) {
            System.err.println("Erro ao ler o ficheiro de simulações.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}