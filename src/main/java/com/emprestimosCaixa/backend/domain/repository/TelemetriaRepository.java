package com.emprestimosCaixa.backend.domain.repository;

import com.emprestimosCaixa.backend.shared.dto.output.EndpointTelemetriaDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TelemetriaRepository {
    void registrar(String nomeApi, String endpoint, String metodoHttp,
                   long tempoResposta, int statusHttp, String errorMessage);
    List<EndpointTelemetriaDTO> buscarMetricasPorDia(LocalDate data, Optional<String> nomeApi);
    long contarTotal();
}
