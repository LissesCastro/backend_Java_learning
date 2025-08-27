package com.emprestimosCaixa.backend.infrastructure.web.controller;

import com.emprestimosCaixa.backend.services.SimulacaoService;
import com.emprestimosCaixa.backend.shared.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.shared.dto.output.VolumeSimuladoDiaDTO;
import com.emprestimosCaixa.backend.shared.dto.response.SimulacaoResponse;
import com.emprestimosCaixa.backend.shared.dto.output.SimulacaoResumoDTO;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/simulacoes")
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @PostMapping
    public ResponseEntity<SimulacaoResponse> simular(@Valid @RequestBody SimulacaoRequest request) {
        SimulacaoResponse response = simulacaoService.simular(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SimulacaoResumoDTO>> listar() {
        return ResponseEntity.ok(simulacaoService.listarTodas());
    }

    @GetMapping("/volume-diario")
    public ResponseEntity<VolumeSimuladoDiaDTO> getVolumeDiario(
            @RequestParam("data") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate data,
            @RequestParam(value = "produto", required = false) Optional<Integer> codigoProduto) {

        VolumeSimuladoDiaDTO volume = simulacaoService.getVolumeSimuladoPorDia(data, codigoProduto);
        return ResponseEntity.ok(volume);
    }
}
