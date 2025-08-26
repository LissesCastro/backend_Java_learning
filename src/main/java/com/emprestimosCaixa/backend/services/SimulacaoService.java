package com.emprestimosCaixa.backend.services;

import com.emprestimosCaixa.backend.domain.model.Produto;
import com.emprestimosCaixa.backend.domain.repository.ProdutoRepository;
import com.emprestimosCaixa.backend.domain.repository.SimulacaoRepository;
import com.emprestimosCaixa.backend.shared.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.shared.dto.output.AmortizacaoDTO;
import com.emprestimosCaixa.backend.shared.dto.output.SimulacaoResumoDTO;
import com.emprestimosCaixa.backend.shared.dto.output.VolumeSimuladoDiaDTO;
import com.emprestimosCaixa.backend.shared.dto.response.SimulacaoResponse;
import com.emprestimosCaixa.backend.infrastructure.messaging.EventHubService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class SimulacaoService {

    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;
    private final CalculadoraAmortizacaoService calculadoraService;
    private final EventHubService eventHubService;

    public SimulacaoService(
            ProdutoRepository produtoRepository,
            SimulacaoRepository simulacaoRepository,
            CalculadoraAmortizacaoService calculadoraService,
            EventHubService eventHubService) {
        this.produtoRepository = produtoRepository;
        this.simulacaoRepository = simulacaoRepository;
        this.calculadoraService = calculadoraService;
        this.eventHubService = eventHubService;
    }

    public SimulacaoResponse simular(SimulacaoRequest request) {
        // 1. Encontrar produto adequado
        Produto produto = produtoRepository.findProdutoByValorAndPrazo(
                request.getValorDesejado(),
                request.getPrazo()
        ).orElseThrow(() -> new RuntimeException(
                "Nenhum produto encontrado para as condições solicitadas."
        ));

        // 2. Calcular amortizações
        List<AmortizacaoDTO> amortizacoes = calculadoraService.calcularAmortizacoes(
                request.getValorDesejado(),
                request.getPrazo(),
                produto.getPcTaxaJuros()
        );

        // 3. Gerar ID único
        int novoId = gerarIdUnico();

        // 4. Criar resposta
        SimulacaoResponse response = new SimulacaoResponse(
                novoId,
                produto.getCoProduto(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                amortizacoes
        );

        // 5. Persistir
        simulacaoRepository.salvar(request.getValorDesejado(), request.getPrazo(), response);

        // 6. Enviar evento
        eventHubService.sendEvent(response);

        return response;
    }

    public List<SimulacaoResumoDTO> listarTodas() {
        return simulacaoRepository.buscarResumos();
    }

    public VolumeSimuladoDiaDTO getVolumeSimuladoPorDia(LocalDate data, Optional<Integer> codigoProduto) {
        return simulacaoRepository.buscarVolumePorDia(data, codigoProduto);
    }

    private int gerarIdUnico() {
        Random random = new Random();
        int novoId;
        do {
            novoId = 10000000 + random.nextInt(90000000);
        } while (simulacaoRepository.existePorId(novoId));
        return novoId;
    }
}
