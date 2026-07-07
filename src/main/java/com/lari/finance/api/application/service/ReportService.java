package com.lari.finance.api.application.service;

import com.lari.finance.api.application.dto.CalendarDay;
import com.lari.finance.api.application.dto.ReportSummary;
import com.lari.finance.api.domain.model.IncomeEntry;
import com.lari.finance.api.domain.model.PaymentMethod;
import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.domain.port.IncomeEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final IncomeEntryRepository incomeEntryRepository;
    private final CurrentUserService currentUserService;

    public ReportService(IncomeEntryRepository incomeEntryRepository, CurrentUserService currentUserService) {
        this.incomeEntryRepository = incomeEntryRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public ReportSummary summarize(String userEmail, LocalDate from, LocalDate to) {
        UserAccount user = currentUserService.getByEmail(userEmail);
        DateRange range = DateRange.of(from, to);
        List<IncomeEntry> entries = incomeEntryRepository.findByUserIdAndDateBetween(user.id(), range.from(), range.to());

        BigDecimal totalAmount = sum(entries, IncomeEntry::netAmount);
        BigDecimal vatAmount = sum(entries, entry -> entry.breakdown().vatAmount());
        BigDecimal fixedExpensesAmount = sum(entries, entry -> entry.breakdown().fixedExpensesAmount());
        BigDecimal productsAmount = sum(entries, entry -> entry.breakdown().productsAmount());
        BigDecimal salaryAmount = sum(entries, entry -> entry.breakdown().salaryAmount());
        BigDecimal annualTaxReserveAmount = sum(entries, entry -> entry.breakdown().annualTaxReserveAmount());

        Map<PaymentMethod, List<IncomeEntry>> byMethod = entries.stream()
            .collect(Collectors.groupingBy(IncomeEntry::paymentMethod, () -> new EnumMap<>(PaymentMethod.class), Collectors.toList()));
        List<ReportSummary.PaymentMethodSummary> paymentMethods = Arrays.stream(PaymentMethod.values())
            .map(method -> {
                List<IncomeEntry> methodEntries = byMethod.getOrDefault(method, List.of());
                return new ReportSummary.PaymentMethodSummary(
                    method.name(),
                    method.label(),
                    methodEntries.size(),
                    methodBucket(entries, methodEntries, method)
                );
            })
            .toList();

        List<ReportSummary.DailySummary> days = entries.stream()
            .collect(Collectors.groupingBy(IncomeEntry::date))
            .entrySet()
            .stream()
            .map(entry -> new ReportSummary.DailySummary(
                entry.getKey(),
                entry.getValue().size(),
                sum(entry.getValue(), IncomeEntry::netAmount)
            ))
            .sorted(Comparator.comparing(ReportSummary.DailySummary::date))
            .toList();

        return new ReportSummary(
            range.from(),
            range.to(),
            entries.size(),
            totalAmount,
            vatAmount,
            fixedExpensesAmount,
            productsAmount,
            salaryAmount,
            annualTaxReserveAmount,
            paymentMethods,
            days
        );
    }

    @Transactional(readOnly = true)
    public List<CalendarDay> calendar(String userEmail, int year, int month) {
        UserAccount user = currentUserService.getByEmail(userEmail);
        YearMonth yearMonth = YearMonth.of(year, month);
        List<IncomeEntry> entries = incomeEntryRepository.findByUserIdAndDateBetween(
            user.id(),
            yearMonth.atDay(1),
            yearMonth.atEndOfMonth()
        );
        Map<LocalDate, List<IncomeEntry>> byDay = entries.stream().collect(Collectors.groupingBy(IncomeEntry::date));
        return yearMonth.atDay(1).datesUntil(yearMonth.atEndOfMonth().plusDays(1))
            .map(date -> {
                List<IncomeEntry> dayEntries = byDay.getOrDefault(date, List.of());
                return new CalendarDay(date, dayEntries.size(), sum(dayEntries, IncomeEntry::netAmount));
            })
            .toList();
    }

    private static BigDecimal methodBucket(List<IncomeEntry> allEntries, List<IncomeEntry> methodEntries, PaymentMethod method) {
        BigDecimal inflow = sum(methodEntries, IncomeEntry::amount);
        BigDecimal outflow = allEntries.stream()
            .filter(entry -> entry.changeGiven() && entry.changeMethod() == method)
            .map(IncomeEntry::changeAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return inflow.subtract(outflow);
    }

    public static BigDecimal sum(List<IncomeEntry> entries, MoneyExtractor extractor) {
        return entries.stream().map(extractor::value).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @FunctionalInterface
    public interface MoneyExtractor {
        BigDecimal value(IncomeEntry entry);
    }
}
