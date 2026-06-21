package com.lari.finance.api.domain.model;

import java.math.BigDecimal;

public record MoneyBreakdown(
    BigDecimal vatAmount,
    BigDecimal fixedExpensesAmount,
    BigDecimal productsAmount,
    BigDecimal salaryAmount,
    BigDecimal annualTaxReserveAmount
) {
}
