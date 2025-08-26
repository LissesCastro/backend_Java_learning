package com.emprestimosCaixa.backend.shared.dto.response;

import com.emprestimosCaixa.backend.shared.dto.output.EndpointTelemetriaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetriaResponse {
    private LocalDate dataReferencia;
    private List<EndpointTelemetriaDTO> listaEndpoints;
}