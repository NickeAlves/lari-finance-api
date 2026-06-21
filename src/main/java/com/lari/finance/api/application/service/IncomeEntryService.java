package com.lari.finance.api.application.service;

import com.lari.finance.api.application.dto.IncomeEntryCommand;
import com.lari.finance.api.application.dto.IncomeEntryWithDailyTotal;
import com.lari.finance.api.application.exception.NotFoundException;
import com.lari.finance.api.domain.model.IncomeEntry;
import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.domain.port.IncomeEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IncomeEntryService {
    private final IncomeEntryRepository incomeEntryRepository;
    private final CurrentUserService currentUserService;

    public IncomeEntryService(IncomeEntryRepository incomeEntryRepository, CurrentUserService currentUserService) {
        this.incomeEntryRepository = incomeEntryRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public IncomeEntryWithDailyTotal create(String userEmail, IncomeEntryCommand command) {
        UserAccount user = currentUserService.getByEmail(userEmail);
        IncomeEntry entry = IncomeEntry.create(
            user.id(),
            command.date(),
            command.clientName(),
            command.amount(),
            command.paymentMethod(),
            command.notes()
        );
        IncomeEntry saved = incomeEntryRepository.save(entry);
        return withDailyTotal(saved);
    }

    @Transactional
    public IncomeEntryWithDailyTotal update(String userEmail, UUID entryId, IncomeEntryCommand command) {
        UserAccount user = currentUserService.getByEmail(userEmail);
        IncomeEntry entry = incomeEntryRepository.findByIdAndUserId(entryId, user.id())
            .orElseThrow(() -> new NotFoundException("Entrada no encontrada."));
        entry.update(command.date(), command.clientName(), command.amount(), command.paymentMethod(), command.notes());
        IncomeEntry saved = incomeEntryRepository.save(entry);
        return withDailyTotal(saved);
    }

    @Transactional
    public void delete(String userEmail, UUID entryId) {
        UserAccount user = currentUserService.getByEmail(userEmail);
        IncomeEntry entry = incomeEntryRepository.findByIdAndUserId(entryId, user.id())
            .orElseThrow(() -> new NotFoundException("Entrada no encontrada."));
        incomeEntryRepository.delete(entry);
    }

    @Transactional(readOnly = true)
    public List<IncomeEntryWithDailyTotal> list(String userEmail, LocalDate from, LocalDate to) {
        UserAccount user = currentUserService.getByEmail(userEmail);
        DateRange range = DateRange.of(from, to);
        List<IncomeEntry> entries = incomeEntryRepository.findByUserIdAndDateBetween(user.id(), range.from(), range.to());
        Map<LocalDate, BigDecimal> totalsByDay = entries.stream()
            .collect(Collectors.groupingBy(IncomeEntry::date, Collectors.reducing(BigDecimal.ZERO, IncomeEntry::amount, BigDecimal::add)));
        return entries.stream()
            .map(entry -> new IncomeEntryWithDailyTotal(entry, totalsByDay.getOrDefault(entry.date(), BigDecimal.ZERO)))
            .toList();
    }

    private IncomeEntryWithDailyTotal withDailyTotal(IncomeEntry entry) {
        List<IncomeEntry> dayEntries = incomeEntryRepository.findByUserIdAndDateBetween(entry.userId(), entry.date(), entry.date());
        BigDecimal total = dayEntries.stream().map(IncomeEntry::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new IncomeEntryWithDailyTotal(entry, total);
    }
}
