package com.emprestimosCaixa.backend.mongo.repository;

import com.emprestimosCaixa.backend.mongo.model.TelemetriaRequisicao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TelemetriaRepository extends MongoRepository<TelemetriaRequisicao, String> {

    @Query("{'dataHoraRequisicao': {$gte: ?0, $lt: ?1}}")
    List<TelemetriaRequisicao> findByDataHoraRequisicaoBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("{'dataHoraRequisicao': {$gte: ?0, $lt: ?1}, 'nomeApi': ?2}")
    List<TelemetriaRequisicao> findByDataHoraRequisicaoBetweenAndNomeApi(
            LocalDateTime inicio, LocalDateTime fim, String nomeApi);
}