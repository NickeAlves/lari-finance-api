package com.lari.finance.api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalendarDay(LocalDate date, long servicesCount, BigDecimal totalAmount) {
}
