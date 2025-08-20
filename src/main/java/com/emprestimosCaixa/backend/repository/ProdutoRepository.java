package com.emprestimosCaixa.backend.repository;

import com.emprestimosCaixa.backend.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    // Pensar se adiciono métodos personalizados
}

