package com.emprestimosCaixa.backend.services;

import com.emprestimosCaixa.backend.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.dto.output.SimulacaoResponse;

public interface SimulacaoService {
    SimulacaoResponse simular(SimulacaoRequest request);
}