package com.lari.finance.api.infrastructure.persistence.repository;

import com.lari.finance.api.domain.model.PaymentMethod;
import com.lari.finance.api.domain.model.UserRole;
import com.lari.finance.api.infrastructure.persistence.entity.IncomeEntryEntity;
import com.lari.finance.api.infrastructure.persistence.entity.UserAccountEntity;
import com.lari.finance.api.infrastructure.persistence.projection.DailyTotalProjection;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaIncomeEntryRepositoryTest {
    private static final LocalDate DATE = LocalDate.of(2026, 7, 7);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JpaIncomeEntryRepository repository;

    @Test
    void sumDailyTotals_nettsChangeAmountForTheDay() {
        UUID userId = UUID.randomUUID();
        entityManager.persist(new UserAccountEntity(userId, "Owner", "owner-" + userId + "@example.com", "hash", UserRole.OWNER, Instant.now()));

        persistEntry(userId, new BigDecimal("50.00"), PaymentMethod.EFECTIVO, true, PaymentMethod.EFECTIVO, new BigDecimal("5.00"));
        persistEntry(userId, new BigDecimal("30.00"), PaymentMethod.TARJETA, false, null, null);
        entityManager.flush();

        List<DailyTotalProjection> totals = repository.sumDailyTotals(userId, DATE, DATE);

        assertThat(totals).hasSize(1);
        assertThat(totals.get(0).getTotal()).isEqualByComparingTo("75.00");
    }

    private void persistEntry(UUID userId, BigDecimal amount, PaymentMethod paymentMethod, boolean changeGiven, PaymentMethod changeMethod, BigDecimal changeAmount) {
        Instant now = Instant.now();
        entityManager.persist(new IncomeEntryEntity(
            UUID.randomUUID(),
            userId,
            DATE,
            "Clienta",
            amount,
            paymentMethod,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            null,
            changeGiven,
            changeMethod,
            changeAmount,
            now,
            now
        ));
    }
}
