package com.emprestimosCaixa.backend.sqlite.model; // Pacote atualizado

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SIMULACAO") // A tabela será criada no ficheiro SQLite
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSimulacao; // Usar Integer para compatibilidade

    @Column(nullable = false)
    private BigDecimal valorDesejado;

    @Column(nullable = false)
    private int prazo;

    @Lob
    @Column(nullable = false)
    private String resultadoJson;

    public Simulacao(BigDecimal valorDesejado, int prazo, String resultadoJson) {
        this.valorDesejado = valorDesejado;
        this.prazo = prazo;
        this.resultadoJson = resultadoJson;
    }
}