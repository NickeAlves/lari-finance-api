package com.lari.finance.api.application.dto;

import com.lari.finance.api.domain.model.IncomeEntry;

import java.math.BigDecimal;

public record IncomeEntryWithDailyTotal(IncomeEntry entry, BigDecimal dailyTotal) {
}
