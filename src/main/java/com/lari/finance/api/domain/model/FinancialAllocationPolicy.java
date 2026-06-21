package com.lari.finance.api.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class FinancialAllocationPolicy {
    public static final BigDecimal VAT_RATE = new BigDecimal("0.21");
    public static final BigDecimal FIXED_EXPENSES_RATE = new BigDecimal("0.20");
    public static final BigDecimal PRODUCTS_RATE = new BigDecimal("0.08");
    public static final BigDecimal SALARY_RATE = new BigDecimal("0.41");
    public static final BigDecimal ANNUAL_TAX_RESERVE_RATE = new BigDecimal("0.10");

    private FinancialAllocationPolicy() {
    }

    public static MoneyBreakdown calculate(BigDecimal amount) {
        BigDecimal normalized = normalize(amount);
        return new MoneyBreakdown(
            percentage(normalized, VAT_RATE),
            percentage(normalized, FIXED_EXPENSES_RATE),
            percentage(normalized, PRODUCTS_RATE),
            percentage(normalized, SALARY_RATE),
            percentage(normalized, ANNUAL_TAX_RESERVE_RATE)
        );
    }

    public static BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("El importe es obligatorio.");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal percentage(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
