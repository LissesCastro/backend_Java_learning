package com.emprestimosCaixa.backend.shared.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoProdutosDTO {
    private int codigoProduto;
    private String descricaoProduto;
    private BigDecimal taxaMediaJuros;
    private BigDecimal valorMedioPrestacao;
    private BigDecimal valorTotalDesejado;
    private BigDecimal valorTotalCredito;
}