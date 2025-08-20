package com.emprestimosCaixa.backend.repository;

import com.emprestimosCaixa.backend.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    @Query("SELECT p FROM Produto p WHERE " +
            ":valorDesejado >= p.vrMinimo AND (:valorDesejado <= p.vrMaximo OR p.vrMaximo IS NULL) AND " +
            ":prazo >= p.nuMinimoMeses AND (:prazo <= p.nuMaximoMeses OR p.nuMaximoMeses IS NULL)")
    Optional<Produto> findProdutoByValorAndPrazo(
            @Param("valorDesejado") BigDecimal valorDesejado,
            @Param("prazo") Integer prazo
    );
}

