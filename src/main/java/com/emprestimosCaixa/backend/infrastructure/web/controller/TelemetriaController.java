package com.emprestimosCaixa.backend.infrastructure.web.controller;

import com.emprestimosCaixa.backend.services.TelemetriaService;
import com.emprestimosCaixa.backend.shared.dto.response.TelemetriaResponse;
import com.emprestimosCaixa.backend.domain.model.Telemetria;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/telemetria")
public class TelemetriaController {

    private final TelemetriaService telemetriaService;
    private final MongoTemplate mongoTemplate;

    public TelemetriaController(TelemetriaService telemetriaService, MongoTemplate mongoTemplate) {
        this.telemetriaService = telemetriaService;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping
    public ResponseEntity<TelemetriaResponse> obterTelemetria(
            @RequestParam("data") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate data,
            @RequestParam(value = "api", required = false) Optional<String> nomeApi) {

        TelemetriaResponse telemetria = telemetriaService.obterTelemetriaPorDia(data, nomeApi);
        return ResponseEntity.ok(telemetria);
    }

    @GetMapping("/debug/requisicoes")
    public ResponseEntity<Map<String, Object>> listarRequisicoes(
            @RequestParam("data") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate data,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {

        Query query = new Query(
                Criteria.where("dataHoraRequisicao")
                        .gte(data.atStartOfDay())
                        .lt(data.plusDays(1).atStartOfDay())
        ).limit(limit);

        List<Telemetria> requisicoes = mongoTemplate.find(query, Telemetria.class);

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("totalEncontrado", requisicoes.size());
        response.put("limite", limit);
        response.put("requisicoes", requisicoes);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/debug/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        long totalRequisicoes = telemetriaService.obterEstatisticasGerais();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequisicoesSalvas", totalRequisicoes);
        stats.put("message", totalRequisicoes > 0 ?
                "Telemetria funcionando corretamente!" :
                "Nenhuma requisição de telemetria encontrada ainda.");

        return ResponseEntity.ok(stats);
    }
}