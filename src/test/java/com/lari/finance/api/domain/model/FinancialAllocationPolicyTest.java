package com.lari.finance.api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FinancialAllocationPolicyTest {
    @Test
    void calculatesSpreadsheetAllocationsFromIncomeAmount() {
        MoneyBreakdown breakdown = FinancialAllocationPolicy.calculate(new BigDecimal("100.00"));

        assertThat(breakdown.vatAmount()).isEqualByComparingTo("21.00");
        assertThat(breakdown.fixedExpensesAmount()).isEqualByComparingTo("20.00");
        assertThat(breakdown.productsAmount()).isEqualByComparingTo("8.00");
        assertThat(breakdown.salaryAmount()).isEqualByComparingTo("41.00");
        assertThat(breakdown.annualTaxReserveAmount()).isEqualByComparingTo("10.00");
    }
}
