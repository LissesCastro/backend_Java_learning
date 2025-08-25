package com.emprestimosCaixa.backend.services;

import com.emprestimosCaixa.backend.dto.response.TelemetriaResponse;

import java.time.LocalDate;
import java.util.Optional;

public interface TelemetriaService {
    TelemetriaResponse obterTelemetriaPorDia(LocalDate data, Optional<String> nomeApi);
}