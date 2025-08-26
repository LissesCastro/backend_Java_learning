package com.emprestimosCaixa.backend.domain.repository;

import com.emprestimosCaixa.backend.domain.model.Produto;
import java.math.BigDecimal;
import java.util.Optional;

public interface ProdutoRepository {
    Optional<Produto> findProdutoByValorAndPrazo(BigDecimal valorDesejado, Integer prazo);
}

