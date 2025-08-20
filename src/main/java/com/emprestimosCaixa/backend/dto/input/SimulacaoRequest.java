package com.emprestimosCaixa.backend.dto.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SimulacaoRequest {

    @NotNull(message = "Por favor coloque o valor desejado do empréstimo")
    @Min(value = 1, message = "O valor desejado deve ser maior que zero")
    private BigDecimal valorDesejado;

    @NotNull(message = "Por favor, indique o prazo para o empréstimo, em meses")
    @Min(value = 1, message = "O prazo deve ser maior que zero")
    private Integer prazo;

    // Getters e Setters
    // Pensar se utilizo Lombok aqui
    public BigDecimal getValorDesejado() {
        return valorDesejado;
    }

    public void setValorDesejado(BigDecimal valorDesejado) {
        this.valorDesejado = valorDesejado;
    }

    public Integer getPrazo() {
        return prazo;
    }

    public void setPrazo(Integer prazo) {
        this.prazo = prazo;
    }
}

