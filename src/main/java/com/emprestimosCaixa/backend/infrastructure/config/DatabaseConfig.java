package com.emprestimosCaixa.backend.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.emprestimosCaixa.backend.infrastructure.persistence.jpa")
@EnableMongoRepositories(basePackages = "com.emprestimosCaixa.backend.infrastructure.persistence.mongodb")
public class DatabaseConfig {
}