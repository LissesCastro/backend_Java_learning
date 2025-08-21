package com.emprestimosCaixa.backend.repository;

import com.emprestimosCaixa.backend.dto.output.SimulacaoCompletaDTO;
import java.util.List;


public interface SimulacaoRepository {

    void persistir(SimulacaoCompletaDTO novaSimulacao);

    List<SimulacaoCompletaDTO> lerTodas();
}