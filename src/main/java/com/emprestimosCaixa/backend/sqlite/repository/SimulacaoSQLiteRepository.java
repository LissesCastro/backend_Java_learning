package com.emprestimosCaixa.backend.sqlite.repository;

import com.emprestimosCaixa.backend.sqlite.model.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulacaoSQLiteRepository extends JpaRepository<Simulacao, Integer> {
    // A interface JpaRepository já nos dá os métodos save(), findAll(), etc.
}