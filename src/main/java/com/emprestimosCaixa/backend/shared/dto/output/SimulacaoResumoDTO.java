package com.emprestimosCaixa.backend.shared.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa o formato de resumo para a listagem de simulações,
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacaoResumoDTO {
    private int idSimulacao;
    private BigDecimal valorDesejado;
    private int prazo;
    private BigDecimal valorTotalParcelas;
}