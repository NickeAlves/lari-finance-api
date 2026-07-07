package com.lari.finance.api.application.service;

import com.lari.finance.api.application.dto.IncomeEntryPage;
import com.lari.finance.api.application.dto.ReportSummary;
import com.lari.finance.api.domain.model.IncomeEntry;
import com.lari.finance.api.domain.model.PaymentMethod;
import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.domain.port.IncomeEntryRepository;
import com.lari.finance.api.domain.port.UserAccountRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReportServiceTest {
    private static final LocalDate DATE = LocalDate.of(2026, 7, 7);
    private static final String EMAIL = "owner@example.com";

    private final List<IncomeEntry> entries = new ArrayList<>();
    private final UserAccount user = UserAccount.createOwner("Owner", EMAIL, "hash");
    private final ReportService reportService = new ReportService(new FakeIncomeEntryRepository(), new CurrentUserService(new FakeUserAccountRepository()));

    private void addEntry(PaymentMethod paymentMethod, BigDecimal amount, boolean changeGiven, PaymentMethod changeMethod, BigDecimal changeAmount) {
        entries.add(IncomeEntry.create(user.id(), DATE, "Clienta", amount, paymentMethod, null, changeGiven, changeMethod, changeAmount));
    }

    @Test
    void summarize_nettsTotalRevenue_whenChangeGiven() {
        addEntry(PaymentMethod.EFECTIVO, new BigDecimal("50.00"), true, PaymentMethod.EFECTIVO, new BigDecimal("5.00"));
        addEntry(PaymentMethod.TARJETA, new BigDecimal("30.00"), false, null, null);

        ReportSummary summary = reportService.summarize(EMAIL, DATE, DATE);

        assertThat(summary.totalAmount()).isEqualByComparingTo("75.00");
    }

    @Test
    void summarize_paymentMethodBucket_sameMethodChange() {
        addEntry(PaymentMethod.EFECTIVO, new BigDecimal("50.00"), true, PaymentMethod.EFECTIVO, new BigDecimal("5.00"));

        ReportSummary summary = reportService.summarize(EMAIL, DATE, DATE);

        assertThat(bucket(summary, PaymentMethod.EFECTIVO)).isEqualByComparingTo("45.00");
    }

    @Test
    void summarize_paymentMethodBucket_crossMethodChange() {
        addEntry(PaymentMethod.BIZUM, new BigDecimal("50.00"), true, PaymentMethod.EFECTIVO, new BigDecimal("5.00"));

        ReportSummary summary = reportService.summarize(EMAIL, DATE, DATE);

        assertThat(bucket(summary, PaymentMethod.BIZUM)).isEqualByComparingTo("50.00");
        assertThat(bucket(summary, PaymentMethod.EFECTIVO)).isEqualByComparingTo("-5.00");
    }

    @Test
    void summarize_bucketsSumToTotalRevenue() {
        addEntry(PaymentMethod.BIZUM, new BigDecimal("50.00"), true, PaymentMethod.EFECTIVO, new BigDecimal("5.00"));
        addEntry(PaymentMethod.EFECTIVO, new BigDecimal("20.00"), true, PaymentMethod.EFECTIVO, new BigDecimal("2.00"));
        addEntry(PaymentMethod.TARJETA, new BigDecimal("15.00"), false, null, null);

        ReportSummary summary = reportService.summarize(EMAIL, DATE, DATE);

        BigDecimal bucketSum = summary.paymentMethods().stream()
            .map(ReportSummary.PaymentMethodSummary::totalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(bucketSum).isEqualByComparingTo(summary.totalAmount());
    }

    @Test
    void calendar_nettsPerDayTotals() {
        addEntry(PaymentMethod.EFECTIVO, new BigDecimal("50.00"), true, PaymentMethod.EFECTIVO, new BigDecimal("5.00"));

        var calendarDays = reportService.calendar(EMAIL, DATE.getYear(), DATE.getMonthValue());

        BigDecimal dayTotal = calendarDays.stream()
            .filter(day -> day.date().equals(DATE))
            .findFirst()
            .orElseThrow()
            .totalAmount();

        assertThat(dayTotal).isEqualByComparingTo("45.00");
    }

    private static BigDecimal bucket(ReportSummary summary, PaymentMethod method) {
        return summary.paymentMethods().stream()
            .filter(m -> m.method().equals(method.name()))
            .findFirst()
            .orElseThrow()
            .totalAmount();
    }

    private class FakeIncomeEntryRepository implements IncomeEntryRepository {
        @Override
        public IncomeEntry save(IncomeEntry entry) {
            entries.add(entry);
            return entry;
        }

        @Override
        public Optional<IncomeEntry> findByIdAndUserId(UUID id, UUID userId) {
            return entries.stream().filter(e -> e.id().equals(id) && e.userId().equals(userId)).findFirst();
        }

        @Override
        public List<IncomeEntry> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to) {
            return entries.stream()
                .filter(e -> e.userId().equals(userId) && !e.date().isBefore(from) && !e.date().isAfter(to))
                .toList();
        }

        @Override
        public IncomeEntryPage findPage(UUID userId, LocalDate from, LocalDate to, int page, int size, String sortBy, String sortDir) {
            throw new UnsupportedOperationException("not needed for these tests");
        }

        @Override
        public void delete(IncomeEntry entry) {
            entries.remove(entry);
        }
    }

    private class FakeUserAccountRepository implements UserAccountRepository {
        @Override
        public UserAccount save(UserAccount account) {
            return account;
        }

        @Override
        public Optional<UserAccount> findByEmail(String email) {
            return email.equals(EMAIL) ? Optional.of(user) : Optional.empty();
        }

        @Override
        public Optional<UserAccount> findById(UUID id) {
            return id.equals(user.id()) ? Optional.of(user) : Optional.empty();
        }

        @Override
        public boolean existsByEmail(String email) {
            return email.equals(EMAIL);
        }
    }
}
