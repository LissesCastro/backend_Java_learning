package com.emprestimosCaixa.backend.repository;

import com.emprestimosCaixa.backend.dto.output.SimulacaoCompletaDTO;

public interface SimulacaoPersistenciaRepository {
    void persistir(SimulacaoCompletaDTO novaSimulacao);
}