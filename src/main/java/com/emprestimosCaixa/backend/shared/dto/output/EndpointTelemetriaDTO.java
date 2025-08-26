package com.emprestimosCaixa.backend.shared.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointTelemetriaDTO {
    private String nomeApi;
    private long qtdRequisicoes;
    private double tempoMedio; // em milissegundos
    private long tempoMinimo;
    private long tempoMaximo;
    private double percentualSucesso; // qtd de retorno 200 com relação ao total
}
