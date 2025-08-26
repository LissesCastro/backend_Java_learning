package com.emprestimosCaixa.backend.shared.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmortizacaoDTO {
    private String tipo; // SAC ou PRICE
    private List<ParcelaDTO> parcelas;
}