package com.lari.finance.api.application.service;

import com.lari.finance.api.application.exception.BusinessException;

import java.time.LocalDate;

public record DateRange(LocalDate from, LocalDate to) {
    public static DateRange of(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDate start = from == null ? today.withDayOfMonth(1) : from;
        LocalDate end = to == null ? today : to;
        if (end.isBefore(start)) {
            throw new BusinessException("La fecha final no puede ser anterior a la fecha inicial.");
        }
        return new DateRange(start, end);
    }
}
