package com.emprestimosCaixa.backend.controller;

import com.emprestimosCaixa.backend.dto.response.TelemetriaResponse;
import com.emprestimosCaixa.backend.mongo.model.TelemetriaRequisicao;
import com.emprestimosCaixa.backend.mongo.repository.TelemetriaRepository;
import com.emprestimosCaixa.backend.services.TelemetriaService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TelemetriaService telemetriaService;

    @Autowired
    private TelemetriaRepository telemetriaRepository;

    /**
     * Endpoint para obter dados de telemetria por dia
     *
     * Exemplos de uso:
     * GET /telemetria?data=30-07-2025
     * GET /telemetria?data=30-07-2025&api=Simulacao
     */
    @GetMapping
    public ResponseEntity<TelemetriaResponse> obterTelemetria(
            @RequestParam("data") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate data,
            @RequestParam(value = "api", required = false) Optional<String> nomeApi) {

        TelemetriaResponse telemetria = telemetriaService.obterTelemetriaPorDia(data, nomeApi);
        return ResponseEntity.ok(telemetria);
    }

    /**
     * Endpoint para visualizar todas as requisições de telemetria de um dia
     * Útil para debug e verificação dos dados coletados
     *
     * Exemplos de uso:
     * GET /telemetria/debug/requisicoes?data=30-07-2025
     * GET /telemetria/debug/requisicoes?data=30-07-2025&limit=100
     */
    @GetMapping("/debug/requisicoes")
    public ResponseEntity<Map<String, Object>> listarRequisicoes(
            @RequestParam("data") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate data,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {

        List<TelemetriaRequisicao> requisicoes = telemetriaRepository
                .findByDataHoraRequisicaoBetween(
                        data.atStartOfDay(),
                        data.plusDays(1).atStartOfDay()
                )
                .stream()
                .limit(limit)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("totalEncontrado", requisicoes.size());
        response.put("limite", limit);
        response.put("requisicoes", requisicoes);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obter estatísticas gerais da telemetria
     *
     * Exemplo de uso:
     * GET /telemetria/debug/estatisticas
     */
    @GetMapping("/debug/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        long totalRequisicoes = telemetriaRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequisicoesSalvas", totalRequisicoes);
        stats.put("message", totalRequisicoes > 0 ?
                "Telemetria funcionando corretamente!" :
                "Nenhuma requisição de telemetria encontrada ainda.");

        return ResponseEntity.ok(stats);
    }
}