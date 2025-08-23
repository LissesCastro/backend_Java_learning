package com.emprestimosCaixa.backend.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeSimuladoDiaDTO {
    private LocalDate dataReferencia;
    private List<ResumoProdutosDTO> simulacoes; // <-- TIPO DA LISTA ATUALIZADO
}