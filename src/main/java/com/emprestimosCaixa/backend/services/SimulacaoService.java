package com.emprestimosCaixa.backend.services;

import com.emprestimosCaixa.backend.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.dto.output.VolumeSimuladoDiaDTO;
import com.emprestimosCaixa.backend.dto.response.SimulacaoResponse;
import com.emprestimosCaixa.backend.dto.output.SimulacaoResumoDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SimulacaoService {
    SimulacaoResponse simular(SimulacaoRequest request);
    List<SimulacaoResumoDTO> listarTodas();
    VolumeSimuladoDiaDTO getVolumeSimuladoPorDia(LocalDate data, Optional<Integer> codigoProduto);
}