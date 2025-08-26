package com.emprestimosCaixa.backend.infrastructure.persistence.jpa;

import com.emprestimosCaixa.backend.domain.model.Produto;
import com.emprestimosCaixa.backend.domain.repository.ProdutoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

interface ProdutoJpaRepository extends JpaRepository<Produto, Integer> {
    @Query("SELECT p FROM Produto p WHERE " +
            ":valorDesejado >= p.vrMinimo AND (:valorDesejado <= p.vrMaximo OR p.vrMaximo IS NULL) AND " +
            ":prazo >= p.nuMinimoMeses AND (:prazo <= p.nuMaximoMeses OR p.nuMaximoMeses IS NULL)")
    Optional<Produto> findProdutoByValorAndPrazo(
            @Param("valorDesejado") BigDecimal valorDesejado,
            @Param("prazo") Integer prazo
    );
}

@Repository
public class ProdutoRepositoryAdapter implements ProdutoRepository {

    private final ProdutoJpaRepository jpaRepository;

    public ProdutoRepositoryAdapter(ProdutoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Produto> findProdutoByValorAndPrazo(BigDecimal valorDesejado, Integer prazo) {
        return jpaRepository.findProdutoByValorAndPrazo(valorDesejado, prazo);
    }
}