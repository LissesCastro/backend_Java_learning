package com.emprestimosCaixa.backend.repository;

import com.emprestimosCaixa.backend.dto.output.SimulacaoCompletaDTO;
import java.util.List;

public interface SimulacaoLeituraRepository {
    List<SimulacaoCompletaDTO> lerTodas();
}