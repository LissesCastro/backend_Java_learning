package com.emprestimosCaixa.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.emprestimosCaixa.backend.repository.primary")
public class JpaConfig {
}