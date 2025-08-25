package com.emprestimosCaixa.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TelemetriaInterceptor telemetriaInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(telemetriaInterceptor)
                .addPathPatterns("/**")  // Interceptar todas as rotas
                .excludePathPatterns(
                        "/actuator/**",  // Excluir endpoints do actuator
                        "/health/**",    // Excluir health checks
                        "/error/**"      // Excluir páginas de erro
                );
    }
}