package com.lari.finance.api.application.dto;

import com.lari.finance.api.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeEntryCommand(
    LocalDate date,
    String clientName,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    String notes
) {
}
