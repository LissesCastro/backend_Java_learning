package com.emprestimosCaixa.backend.infrastructure.web.interceptor;

import com.emprestimosCaixa.backend.services.TelemetriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TelemetriaInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";
    private final TelemetriaService telemetriaService;

    public TelemetriaInterceptor(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        try {
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            if (startTime == null) return;

            telemetriaService.registrarRequisicao(
                    request.getRequestURI(),
                    request.getMethod(),
                    System.currentTimeMillis() - startTime,
                    response.getStatus(),
                    ex
            );

        } catch (Exception e) {
            System.err.println("Erro ao capturar telemetria: " + e.getMessage());
        }
    }
}