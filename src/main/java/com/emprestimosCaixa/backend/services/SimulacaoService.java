package com.emprestimosCaixa.backend.services;

import com.emprestimosCaixa.backend.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.dto.response.SimulacaoResponse;
import com.emprestimosCaixa.backend.dto.output.SimulacaoResumoDTO;

import java.util.List;

public interface SimulacaoService {
    SimulacaoResponse simular(SimulacaoRequest request);
    List<SimulacaoResumoDTO> listarTodas();
}