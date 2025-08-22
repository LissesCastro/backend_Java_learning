package com.emprestimosCaixa.backend.dto.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class SimulacaoRequest {

    @NotNull(message = "Por favor coloque o valor desejado do empréstimo")
    @Min(value = 1, message = "O valor desejado deve ser maior que zero")
    private BigDecimal valorDesejado;

    @NotNull(message = "Por favor, indique o prazo para o empréstimo, em meses")
    @Min(value = 1, message = "O prazo deve ser maior que zero")
    private Integer prazo;
}
