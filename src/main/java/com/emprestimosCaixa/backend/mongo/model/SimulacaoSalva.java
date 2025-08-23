package com.emprestimosCaixa.backend.mongo.model;

import com.emprestimosCaixa.backend.dto.response.SimulacaoResponse;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "simulacoes") // Nome da "tabela" (collection) no MongoDB
public class SimulacaoSalva {

    @Id
    private String id; // O ID no MongoDB é uma String

    // Dados da requisição original
    private BigDecimal valorDesejado;
    private int prazo;

    // Resposta completa da simulação
    private SimulacaoResponse resultado;

    // Data da simulação
    private LocalDateTime dataSimulacao;

    public SimulacaoSalva(BigDecimal valorDesejado, int prazo, SimulacaoResponse resultado) {
        this.valorDesejado = valorDesejado;
        this.prazo = prazo;
        this.resultado = resultado;
        this.dataSimulacao = LocalDateTime.now();
    }
}