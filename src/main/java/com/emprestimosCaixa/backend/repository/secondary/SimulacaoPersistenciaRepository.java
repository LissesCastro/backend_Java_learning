package com.emprestimosCaixa.backend.repository.secondary;

import com.emprestimosCaixa.backend.dto.output.SimulacaoCompletaDTO;

public interface SimulacaoPersistenciaRepository {
    void persistir(SimulacaoCompletaDTO novaSimulacao);
}