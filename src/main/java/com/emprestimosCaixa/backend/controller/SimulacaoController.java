package com.emprestimosCaixa.backend.controller;

import com.emprestimosCaixa.backend.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.dto.output.SimulacaoResponse;
import com.emprestimosCaixa.backend.dto.output.SimulacaoResumoDTO;
import com.emprestimosCaixa.backend.services.SimulacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.emprestimosCaixa.backend.dto.output.SimulacaoResumoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/simulacoes")
public class SimulacaoController {

        @Autowired
        private SimulacaoService simulacaoService;

        @PostMapping
        public ResponseEntity<SimulacaoResponse> simular(@Valid @RequestBody SimulacaoRequest request) {
            SimulacaoResponse response = simulacaoService.simular(request);
            return ResponseEntity.ok(response);
        }

        @GetMapping
        public ResponseEntity<List<SimulacaoResumoDTO>> listar() {
            return ResponseEntity.ok(simulacaoService.listarTodas());
        }

}