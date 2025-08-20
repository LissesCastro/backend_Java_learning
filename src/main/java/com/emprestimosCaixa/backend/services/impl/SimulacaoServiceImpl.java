package com.emprestimosCaixa.backend.services.impl;

import com.emprestimosCaixa.backend.dto.input.SimulacaoRequest;
import com.emprestimosCaixa.backend.dto.output.*;
import com.emprestimosCaixa.backend.model.Produto;
import com.emprestimosCaixa.backend.repository.ProdutoRepository;
import com.emprestimosCaixa.backend.services.PRICEService;
import com.emprestimosCaixa.backend.services.SACService;
import com.emprestimosCaixa.backend.services.SimulacaoService;
import com.fasterxml.jackson.core.type.TypeReference; // Importado
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SimulacaoServiceImpl implements SimulacaoService {

    private final AtomicInteger idSimulacaoGenerator = new AtomicInteger(1);

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private SACService sacService;

    @Autowired
    private PRICEService priceService;

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // --- MUDANÇA AQUI: O caminho agora aponta para um único FICHEIRO ---
    private final String CAMINHO_ARQUIVO_SIMULACOES = System.getProperty("user.home") + "/simulacoes.json";

    @Override
    public SimulacaoResponse simular(SimulacaoRequest request) {
        // ... (o código deste método permanece exatamente o mesmo)
        Produto produto = produtoRepository.findProdutoByValorAndPrazo(request.getValorDesejado(), request.getPrazo())
                .orElseThrow(() -> new RuntimeException("Nenhum produto de crédito encontrado para as condições solicitadas."));

        List<ParcelaDTO> parcelasSAC = sacService.calcularParcelas(request.getValorDesejado(), request.getPrazo(), produto.getPcTaxaJuros());
        List<ParcelaDTO> parcelasPRICE = priceService.calcularParcelas(request.getValorDesejado(), request.getPrazo(), produto.getPcTaxaJuros());

        List<AmortizacaoDTO> resultadoSimulacao = new ArrayList<>();
        resultadoSimulacao.add(new AmortizacaoDTO("SAC", parcelasSAC));
        resultadoSimulacao.add(new AmortizacaoDTO("PRICE", parcelasPRICE));

        int novoId = idSimulacaoGenerator.getAndIncrement();

        SimulacaoResponse response = new SimulacaoResponse(
                novoId,
                produto.getCoProduto(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                resultadoSimulacao
        );

        SimulacaoCompletaDTO dadosParaPersistir = new SimulacaoCompletaDTO(
                request.getValorDesejado(),
                request.getPrazo(),
                response
        );
        persistirSimulacaoEmArquivo(dadosParaPersistir);

        return response;
    }

    // --- MUDANÇA SIGNIFICATIVA AQUI ---
    private synchronized void persistirSimulacaoEmArquivo(SimulacaoCompletaDTO novaSimulacao) {
        File arquivoDeSimulacoes = new File(CAMINHO_ARQUIVO_SIMULACOES);
        List<SimulacaoCompletaDTO> simulacoesExistentes = new ArrayList<>();

        // Se o ficheiro já existe, lê a lista de simulações que está nele
        if (arquivoDeSimulacoes.exists()) {
            try {
                simulacoesExistentes = objectMapper.readValue(arquivoDeSimulacoes, new TypeReference<List<SimulacaoCompletaDTO>>() {});
            } catch (IOException e) {
                System.err.println("Erro ao ler o ficheiro de simulações existente.");
                e.printStackTrace();
                return; // Aborta se não conseguir ler o ficheiro
            }
        }

        // Adiciona a nova simulação à lista
        simulacoesExistentes.add(novaSimulacao);

        // Escreve a lista completa de volta para o ficheiro, substituindo o conteúdo antigo
        try {
            objectMapper.writeValue(arquivoDeSimulacoes, simulacoesExistentes);
            System.out.println("Simulação adicionada com sucesso em: " + arquivoDeSimulacoes.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo de simulação.");
            e.printStackTrace();
        }
    }

    // --- MUDANÇA SIGNIFICATIVA AQUI ---
    @Override
    public List<SimulacaoResumoDTO> listarTodas() {
        File arquivoDeSimulacoes = new File(CAMINHO_ARQUIVO_SIMULACOES);
        if (!arquivoDeSimulacoes.exists()) {
            return new ArrayList<>(); // Retorna lista vazia se o ficheiro não existe
        }

        try {
            // Lê o ficheiro e converte o JSON numa lista de objetos completos
            List<SimulacaoCompletaDTO> listaCompleta = objectMapper.readValue(arquivoDeSimulacoes, new TypeReference<List<SimulacaoCompletaDTO>>() {});

            // Transforma a lista de objetos completos na lista de resumos
            return listaCompleta.stream()
                    .map(dadosCompletos -> {
                        BigDecimal valorTotalParcelas = dadosCompletos.getResultado().getResultadoSimulacao()
                                .stream()
                                .filter(amortizacao -> "SAC".equalsIgnoreCase(amortizacao.getTipo()))
                                .findFirst()
                                .map(amortizacao -> amortizacao.getParcelas().stream()
                                        .map(ParcelaDTO::getValorPrestacao)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                                .orElse(BigDecimal.ZERO);

                        return new SimulacaoResumoDTO(
                                dadosCompletos.getResultado().getIdSimulacao(),
                                dadosCompletos.getValorDesejado(),
                                dadosCompletos.getPrazo(),
                                valorTotalParcelas
                        );
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println("Erro ao ler o ficheiro de simulações.");
            e.printStackTrace();
            return new ArrayList<>(); // Retorna lista vazia em caso de erro
        }
    }
}