package com.emprestimosCaixa.backend.config;

import com.emprestimosCaixa.backend.mongo.model.TelemetriaRequisicao;
import com.emprestimosCaixa.backend.mongo.repository.TelemetriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TelemetriaInterceptor implements HandlerInterceptor {

    @Autowired
    private TelemetriaRepository telemetriaRepository;

    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Capturar o tempo de início da requisição
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        try {
            // Calcular tempo de resposta
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            if (startTime == null) {
                return;
            }

            long tempoResposta = System.currentTimeMillis() - startTime;

            // Extrair informações da requisição
            String endpoint = request.getRequestURI();
            String metodoHttp = request.getMethod();
            int statusHttp = response.getStatus();
            String errorMessage = null;

            // Se houve exceção, capturar a mensagem
            if (ex != null) {
                errorMessage = ex.getMessage();
                if (statusHttp < 400) {
                    statusHttp = 500; // Definir como erro interno se não foi setado
                }
            }

            // Determinar o nome da API baseado no endpoint
            String nomeApi = determinarNomeApi(endpoint);

            // Salvar telemetria apenas para endpoints da API (não para actuator, etc.)
            if (nomeApi != null) {
                TelemetriaRequisicao telemetria = new TelemetriaRequisicao(
                        nomeApi, endpoint, metodoHttp, tempoResposta, statusHttp, errorMessage
                );

                // Salvar de forma assíncrona para não impactar a performance
                salvarTelemetriaAsync(telemetria);
            }

        } catch (Exception e) {
            // Log do erro, mas não falhar a requisição
            System.err.println("Erro ao capturar telemetria: " + e.getMessage());
        }
    }

    private String determinarNomeApi(String endpoint) {
        if (endpoint.startsWith("/simulacoes")) {
            return "Simulacao";
        } else if (endpoint.startsWith("/telemetria")) {
            return "Telemetria";
        }
        // Adicionar outros endpoints conforme necessário
        // Retornar null para endpoints que não queremos rastrear (actuator, health, etc.)
        return null;
    }

    private void salvarTelemetriaAsync(TelemetriaRequisicao telemetria) {
        // Em produção, considere usar @Async ou um ExecutorService
        try {
            telemetriaRepository.save(telemetria);
        } catch (Exception e) {
            System.err.println("Erro ao salvar telemetria: " + e.getMessage());
        }
    }
}