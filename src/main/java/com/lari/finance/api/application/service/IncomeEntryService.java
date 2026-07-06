package com.lari.finance.api.application.service;

import com.lari.finance.api.application.dto.IncomeEntryCommand;
import com.lari.finance.api.application.dto.IncomeEntryPage;
import com.lari.finance.api.application.dto.IncomeEntryWithDailyTotal;
import com.lari.finance.api.application.exception.BusinessException;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IncomeEntryService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("date", "clientName", "amount", "createdAt");

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
            command.notes(),
            command.changeGiven(),
            command.changeMethod()
        );
        IncomeEntry saved = incomeEntryRepository.save(entry);
        return withDailyTotal(saved);
    }

    @Transactional
    public IncomeEntryWithDailyTotal update(String userEmail, UUID entryId, IncomeEntryCommand command) {
        UserAccount user = currentUserService.getByEmail(userEmail);
        IncomeEntry entry = incomeEntryRepository.findByIdAndUserId(entryId, user.id())
            .orElseThrow(() -> new NotFoundException("Entrada no encontrada."));
        entry.update(
            command.date(),
            command.clientName(),
            command.amount(),
            command.paymentMethod(),
            command.notes(),
            command.changeGiven(),
            command.changeMethod()
        );
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
    public IncomeEntryPage list(String userEmail, LocalDate from, LocalDate to, int page, int size, String sortBy, String sortDir) {
        if (page < 0) throw new BusinessException("O número da página não pode ser negativo.");
        if (size < 1 || size > 100) throw new BusinessException("O tamanho da página deve estar entre 1 e 100.");
        if (!ALLOWED_SORT_FIELDS.contains(sortBy))
            throw new BusinessException("Campo de ordenação inválido: " + sortBy + ". Valores aceitos: " + ALLOWED_SORT_FIELDS);
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc"))
            throw new BusinessException("Direção de ordenação inválida: " + sortDir + ". Use 'asc' ou 'desc'.");

        UserAccount user = currentUserService.getByEmail(userEmail);
        DateRange range = DateRange.of(from, to);
        return incomeEntryRepository.findPage(user.id(), range.from(), range.to(), page, size, sortBy, sortDir);
    }

    @Transactional(readOnly = true)
    public List<IncomeEntryWithDailyTotal> listAll(String userEmail, LocalDate from, LocalDate to) {
        UserAccount user = currentUserService.getByEmail(userEmail);
        DateRange range = DateRange.of(from, to);
        List<IncomeEntry> entries = incomeEntryRepository.findByUserIdAndDateBetween(user.id(), range.from(), range.to());
        Map<LocalDate, BigDecimal> totalsByDay = entries.stream()
            .collect(Collectors.groupingBy(IncomeEntry::date,
                Collectors.reducing(BigDecimal.ZERO, IncomeEntry::amount, BigDecimal::add)));
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
