package com.lari.finance.api.application.service;

import com.lari.finance.api.application.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateRangeTest {
    @Test
    void unboundedTreatsNullBoundsAsNoDateFilter() {
        DateRange range = DateRange.unbounded(null, null);

        assertThat(range.from()).isEqualTo(LocalDate.of(1, 1, 1));
        assertThat(range.to()).isEqualTo(LocalDate.of(9999, 12, 31));
    }

    @Test
    void unboundedBoundsStayInsidePostgresDateRange() {
        DateRange range = DateRange.unbounded(null, null);

        // PostgreSQL soporta date entre 4713 a.C. y 5874897 d.C.;
        // LocalDate.MIN/MAX se saldrían de ese rango y romperían la consulta.
        assertThat(range.from()).isAfter(LocalDate.of(-4713, 1, 1));
        assertThat(range.to()).isBefore(LocalDate.of(5874897, 1, 1));
    }

    @Test
    void unboundedKeepsExplicitBounds() {
        DateRange range = DateRange.unbounded(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 31));

        assertThat(range.from()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(range.to()).isEqualTo(LocalDate.of(2026, 7, 31));
    }

    @Test
    void ofDefaultsToCurrentMonthToDateInMadridZone() {
        LocalDate today = LocalDate.now(DateRange.ZONE);

        DateRange range = DateRange.of(null, null);

        assertThat(range.from()).isEqualTo(today.withDayOfMonth(1));
        assertThat(range.to()).isEqualTo(today);
    }

    @Test
    void rejectsEndBeforeStart() {
        LocalDate from = LocalDate.of(2026, 7, 31);
        LocalDate to = LocalDate.of(2026, 7, 1);

        assertThatThrownBy(() -> DateRange.of(from, to))
            .isInstanceOf(BusinessException.class)
            .hasMessage("La fecha final no puede ser anterior a la fecha inicial.");

        assertThatThrownBy(() -> DateRange.unbounded(from, to))
            .isInstanceOf(BusinessException.class)
            .hasMessage("La fecha final no puede ser anterior a la fecha inicial.");
    }
}
