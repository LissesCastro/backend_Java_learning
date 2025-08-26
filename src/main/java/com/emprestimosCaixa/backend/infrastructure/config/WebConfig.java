package com.emprestimosCaixa.backend.infrastructure.config;

import com.emprestimosCaixa.backend.infrastructure.web.interceptor.TelemetriaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TelemetriaInterceptor telemetriaInterceptor;

    public WebConfig(TelemetriaInterceptor telemetriaInterceptor) {
        this.telemetriaInterceptor = telemetriaInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(telemetriaInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/actuator/**",
                        "/health/**",
                        "/error/**"
                );
    }
}