package com.emprestimosCaixa.backend.mongo.repository;

import com.emprestimosCaixa.backend.mongo.model.SimulacaoSalva;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulacaoMongoRepository extends MongoRepository<SimulacaoSalva, String> {
    boolean existsByResultado_IdSimulacao(int idSimulacao);
}