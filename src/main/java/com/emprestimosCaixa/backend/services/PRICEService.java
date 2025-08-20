package com.emprestimosCaixa.backend.services;

import com.emprestimosCaixa.backend.dto.output.ParcelaDTO;
import java.math.BigDecimal;
import java.util.List;

public interface PRICEService {
    List<ParcelaDTO> calcularParcelas(BigDecimal valor, int prazo, BigDecimal taxaJuros);
}
