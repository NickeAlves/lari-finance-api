package com.lari.finance.api.infrastructure.web.dto;

import com.lari.finance.api.application.dto.IncomeEntryCommand;
import com.lari.finance.api.domain.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeEntryRequest(
    @NotNull LocalDate date,
    @NotBlank @Size(max = 160) String clientName,
    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal amount,
    @NotNull PaymentMethod paymentMethod,
    @Size(max = 500) String notes,
    boolean changeGiven,
    PaymentMethod changeMethod
) {
    public IncomeEntryCommand toCommand() {
        return new IncomeEntryCommand(date, clientName, amount, paymentMethod, notes, changeGiven, changeMethod);
    }
}
