package com.emprestimosCaixa.backend.adapters.persistence.h2;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SIMULACAO")
public class SimulacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSimulacao;

    @Column(nullable = false)
    private BigDecimal valorDesejado;

    @Column(nullable = false)
    private int prazo;

    @Lob
    @Column(nullable = false)
    private String resultadoJson;

    public SimulacaoEntity(BigDecimal valorDesejado, int prazo, String resultadoJson) {
        this.valorDesejado = valorDesejado;
        this.prazo = prazo;
        this.resultadoJson = resultadoJson;
    }
}