package com.lari.finance.api.infrastructure.web.dto;

import com.lari.finance.api.application.dto.CalendarDay;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalendarDayResponse(LocalDate date, long servicesCount, BigDecimal totalAmount) {
    public static CalendarDayResponse from(CalendarDay day) {
        return new CalendarDayResponse(day.date(), day.servicesCount(), day.totalAmount());
    }
}
