package com.emprestimosCaixa.backend.dto.response;

import com.emprestimosCaixa.backend.dto.output.AmortizacaoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacaoResponse {
    private int idSimulacao;
    private int codigoProduto;
    private String descricaoProduto;
    private BigDecimal taxaJuros;
    private List<AmortizacaoDTO> resultadoSimulacao;
}
