package com.lari.finance.api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReportSummary(
    LocalDate from,
    LocalDate to,
    long servicesCount,
    BigDecimal totalAmount,
    BigDecimal vatAmount,
    BigDecimal fixedExpensesAmount,
    BigDecimal productsAmount,
    BigDecimal salaryAmount,
    BigDecimal annualTaxReserveAmount,
    List<PaymentMethodSummary> paymentMethods,
    List<DailySummary> days
) {
    public record PaymentMethodSummary(String method, String label, long servicesCount, BigDecimal totalAmount) {
    }

    public record DailySummary(LocalDate date, long servicesCount, BigDecimal totalAmount) {
    }
}
