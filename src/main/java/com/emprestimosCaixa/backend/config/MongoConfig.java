package com.emprestimosCaixa.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.emprestimosCaixa.backend.mongo.repository")
public class MongoConfig {
}