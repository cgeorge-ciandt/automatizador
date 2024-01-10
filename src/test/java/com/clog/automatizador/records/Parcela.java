package com.clog.automatizador.records;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Parcela(
        Integer parc,
        LocalDate vencimento,
        Integer dias,
        BigDecimal prestacao,
        BigDecimal juros,
        BigDecimal valorPrincipal,
        BigDecimal percPrincipal,
        BigDecimal saldoDevedor,
        Integer diasIos,
        BigDecimal percPrincipalIof,
        BigDecimal valorIof,
        BigDecimal valorIofAdicional
){}