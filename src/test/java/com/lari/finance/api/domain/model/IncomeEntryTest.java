package com.lari.finance.api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IncomeEntryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.of(2026, 7, 7);

    @Test
    void netAmountEqualsAmount_whenChangeNotGiven() {
        IncomeEntry entry = IncomeEntry.create(
            USER_ID, DATE, "Clienta", new BigDecimal("50.00"), PaymentMethod.EFECTIVO,
            null, false, null, null
        );

        assertThat(entry.amount()).isEqualByComparingTo("50.00");
        assertThat(entry.netAmount()).isEqualByComparingTo("50.00");
        assertThat(entry.breakdown().vatAmount()).isEqualByComparingTo("10.50");
    }

    @Test
    void nettingAmount_whenChangeGivenSameMethod() {
        IncomeEntry entry = IncomeEntry.create(
            USER_ID, DATE, "Clienta", new BigDecimal("50.00"), PaymentMethod.EFECTIVO,
            null, true, PaymentMethod.EFECTIVO, new BigDecimal("5.00")
        );

        assertThat(entry.amount()).isEqualByComparingTo("50.00");
        assertThat(entry.netAmount()).isEqualByComparingTo("45.00");
        assertThat(entry.breakdown().vatAmount()).isEqualByComparingTo("9.45");
    }

    @Test
    void nettingAmount_whenChangeGivenCrossMethod() {
        IncomeEntry entry = IncomeEntry.create(
            USER_ID, DATE, "Clienta", new BigDecimal("50.00"), PaymentMethod.BIZUM,
            null, true, PaymentMethod.EFECTIVO, new BigDecimal("5.00")
        );

        assertThat(entry.amount()).isEqualByComparingTo("50.00");
        assertThat(entry.netAmount()).isEqualByComparingTo("45.00");
        assertThat(entry.breakdown().vatAmount()).isEqualByComparingTo("9.45");
    }

    @Test
    void rejectsChangeAmountGreaterThanOrEqualToAmount() {
        assertThatThrownBy(() -> IncomeEntry.create(
            USER_ID, DATE, "Clienta", new BigDecimal("50.00"), PaymentMethod.EFECTIVO,
            null, true, PaymentMethod.EFECTIVO, new BigDecimal("50.00")
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> IncomeEntry.create(
            USER_ID, DATE, "Clienta", new BigDecimal("50.00"), PaymentMethod.EFECTIVO,
            null, true, PaymentMethod.EFECTIVO, new BigDecimal("60.00")
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsChangeAmountLessThanOrEqualToZero() {
        assertThatThrownBy(() -> IncomeEntry.create(
            USER_ID, DATE, "Clienta", new BigDecimal("50.00"), PaymentMethod.EFECTIVO,
            null, true, PaymentMethod.EFECTIVO, BigDecimal.ZERO
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsInvalidChangeMethod() {
        assertThatThrownBy(() -> IncomeEntry.create(
            USER_ID, DATE, "Clienta", new BigDecimal("50.00"), PaymentMethod.EFECTIVO,
            null, true, PaymentMethod.TARJETA, new BigDecimal("5.00")
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateRecalculatesNetAmount() {
        IncomeEntry entry = IncomeEntry.create(
            USER_ID, DATE, "Clienta", new BigDecimal("50.00"), PaymentMethod.EFECTIVO,
            null, false, null, null
        );

        entry.update(
            DATE, "Clienta", new BigDecimal("50.00"), PaymentMethod.BIZUM,
            null, true, PaymentMethod.EFECTIVO, new BigDecimal("5.00")
        );

        assertThat(entry.netAmount()).isEqualByComparingTo("45.00");
        assertThat(entry.breakdown().vatAmount()).isEqualByComparingTo("9.45");
    }
}
