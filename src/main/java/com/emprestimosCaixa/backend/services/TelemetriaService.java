package com.emprestimosCaixa.backend.services;

import com.emprestimosCaixa.backend.domain.repository.TelemetriaRepository;
import com.emprestimosCaixa.backend.shared.dto.output.EndpointTelemetriaDTO;
import com.emprestimosCaixa.backend.shared.dto.response.TelemetriaResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;

    public TelemetriaService(TelemetriaRepository telemetriaRepository) {
        this.telemetriaRepository = telemetriaRepository;
    }

    public void registrarRequisicao(String endpoint, String metodoHttp, long tempoResposta,
                                    int statusHttp, Exception ex) {
        String nomeApi = determinarNomeApi(endpoint);
        if (nomeApi != null) {
            String errorMessage = (ex != null) ? ex.getMessage() : null;
            if (ex != null && statusHttp < 400) {
                statusHttp = 500;
            }

            telemetriaRepository.registrar(nomeApi, endpoint, metodoHttp,
                    tempoResposta, statusHttp, errorMessage);
        }
    }

    public TelemetriaResponse obterTelemetriaPorDia(LocalDate data, Optional<String> nomeApi) {
        List<EndpointTelemetriaDTO> metricas = telemetriaRepository.buscarMetricasPorDia(data, nomeApi);

        // Se não encontrou dados e foi especificada uma API, retornar dados zerados
        if (metricas.isEmpty() && nomeApi.isPresent()) {
            metricas.add(new EndpointTelemetriaDTO(nomeApi.get(), 0, 0.0, 0, 0, 0.0));
        }

        return new TelemetriaResponse(data, metricas);
    }

    public long obterEstatisticasGerais() {
        return telemetriaRepository.contarTotal();
    }

    private String determinarNomeApi(String endpoint) {
        if (endpoint.startsWith("/simulacoes")) {
            return "Simulacao";
        } else if (endpoint.startsWith("/telemetria")) {
            return "Telemetria";
        }
        return null;
    }
}
