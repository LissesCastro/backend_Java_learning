package com.emprestimosCaixa.backend.dto.output;

import com.emprestimosCaixa.backend.dto.response.SimulacaoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Combina os dados da requisição original com a resposta completa.
 * Utilizado para criar os dados que serão persistidos em JSON.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacaoCompletaDTO {

    // Dados da requisição original
    private BigDecimal valorDesejado;
    private int prazo;

    // Resposta completa da simulação
    private SimulacaoResponse resultado;
}
