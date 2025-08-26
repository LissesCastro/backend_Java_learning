package com.emprestimosCaixa.backend.domain.repository;

import com.emprestimosCaixa.backend.shared.dto.output.SimulacaoResumoDTO;
import com.emprestimosCaixa.backend.shared.dto.output.VolumeSimuladoDiaDTO;
import com.emprestimosCaixa.backend.shared.dto.response.SimulacaoResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SimulacaoRepository {
    void salvar(BigDecimal valorDesejado, int prazo, SimulacaoResponse response);
    boolean existePorId(int idSimulacao);
    List<SimulacaoResumoDTO> buscarResumos();
    VolumeSimuladoDiaDTO buscarVolumePorDia(LocalDate data, Optional<Integer> codigoProduto);
}