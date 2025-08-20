package com.exemplo.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTest implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Testando conexão com o banco...");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dbo.PRODUTO", Integer.class
        );

        System.out.println("Número de produtos na tabela: " + count);
    }
}

