package com.emprestimosCaixa.backend.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "telemetria")
public class Telemetria {

    @Id
    private String id;

    private String nomeApi;
    private String endpoint;
    private String metodoHttp;
    private LocalDateTime dataHoraRequisicao;
    private long tempoResposta;
    private int statusHttp;
    private boolean sucesso;
    private String errorMessage;

    public Telemetria(String nomeApi, String endpoint, String metodoHttp,
                      long tempoResposta, int statusHttp, String errorMessage) {
        this.nomeApi = nomeApi;
        this.endpoint = endpoint;
        this.metodoHttp = metodoHttp;
        this.dataHoraRequisicao = LocalDateTime.now();
        this.tempoResposta = tempoResposta;
        this.statusHttp = statusHttp;
        this.sucesso = statusHttp >= 200 && statusHttp < 300;
        this.errorMessage = errorMessage;
    }
}