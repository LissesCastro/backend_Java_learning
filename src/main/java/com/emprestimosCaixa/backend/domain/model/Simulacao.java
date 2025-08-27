package com.emprestimosCaixa.backend.domain.model;

import com.emprestimosCaixa.backend.shared.dto.response.SimulacaoResponse;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "simulacoes")
public class Simulacao {

    @Id
    private String id;

    private BigDecimal valorDesejado;
    private int prazo;
    private SimulacaoResponse resultado;
    private LocalDateTime dataSimulacao;

    public Simulacao(BigDecimal valorDesejado, int prazo, SimulacaoResponse resultado) {
        this.valorDesejado = valorDesejado;
        this.prazo = prazo;
        this.resultado = resultado;
        this.dataSimulacao = LocalDateTime.now();
    }
}