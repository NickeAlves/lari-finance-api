package com.lari.finance.api.infrastructure.web.dto;

import com.lari.finance.api.application.dto.IncomeEntryWithDailyTotal;
import com.lari.finance.api.domain.model.IncomeEntry;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record IncomeEntryResponse(
    UUID id,
    LocalDate date,
    String clientName,
    BigDecimal amount,
    String paymentMethod,
    String paymentMethodLabel,
    BigDecimal vatAmount,
    BigDecimal fixedExpensesAmount,
    BigDecimal productsAmount,
    BigDecimal salaryAmount,
    BigDecimal annualTaxReserveAmount,
    BigDecimal dailyTotal,
    String notes,
    boolean changeGiven,
    String changeMethod,
    String changeMethodLabel,
    Instant createdAt,
    Instant updatedAt
) {
    public static IncomeEntryResponse from(IncomeEntryWithDailyTotal row) {
        IncomeEntry entry = row.entry();
        return new IncomeEntryResponse(
            entry.id(),
            entry.date(),
            entry.clientName(),
            entry.amount(),
            entry.paymentMethod().name(),
            entry.paymentMethod().label(),
            entry.breakdown().vatAmount(),
            entry.breakdown().fixedExpensesAmount(),
            entry.breakdown().productsAmount(),
            entry.breakdown().salaryAmount(),
            entry.breakdown().annualTaxReserveAmount(),
            row.dailyTotal(),
            entry.notes(),
            entry.changeGiven(),
            entry.changeMethod() == null ? null : entry.changeMethod().name(),
            entry.changeMethod() == null ? null : entry.changeMethod().label(),
            entry.createdAt(),
            entry.updatedAt()
        );
    }
}
