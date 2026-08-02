package com.lari.finance.api.application.service;

import com.lari.finance.api.application.exception.BusinessException;

import java.time.LocalDate;
import java.time.ZoneId;

public record DateRange(LocalDate from, LocalDate to) {
    public static final ZoneId ZONE = ZoneId.of("Europe/Madrid");

    /**
     * Límites abiertos para las consultas sin filtro de fecha.
     * No usamos {@link LocalDate#MIN}/{@link LocalDate#MAX} porque sus años
     * (±999999999) quedan fuera del rango del tipo {@code date} de PostgreSQL.
     */
    private static final LocalDate OPEN_START = LocalDate.of(1, 1, 1);
    private static final LocalDate OPEN_END = LocalDate.of(9999, 12, 31);

    public static DateRange of(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now(ZONE);
        return validated(from == null ? today.withDayOfMonth(1) : from, to == null ? today : to);
    }

    public static DateRange unbounded(LocalDate from, LocalDate to) {
        return validated(from == null ? OPEN_START : from, to == null ? OPEN_END : to);
    }

    private static DateRange validated(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new BusinessException("La fecha final no puede ser anterior a la fecha inicial.");
        }
        return new DateRange(start, end);
    }
}
