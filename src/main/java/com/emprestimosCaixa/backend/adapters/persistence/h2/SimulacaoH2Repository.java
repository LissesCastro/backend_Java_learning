package com.emprestimosCaixa.backend.adapters.persistence.h2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulacaoH2Repository extends JpaRepository<SimulacaoEntity, Integer> {
}