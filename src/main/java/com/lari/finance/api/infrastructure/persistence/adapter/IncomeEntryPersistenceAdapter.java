package com.lari.finance.api.infrastructure.persistence.adapter;

import com.lari.finance.api.application.dto.IncomeEntryPage;
import com.lari.finance.api.application.dto.IncomeEntryWithDailyTotal;
import com.lari.finance.api.domain.model.IncomeEntry;
import com.lari.finance.api.domain.port.IncomeEntryRepository;
import com.lari.finance.api.infrastructure.persistence.mapper.IncomeEntryMapper;
import com.lari.finance.api.infrastructure.persistence.projection.DailyTotalProjection;
import com.lari.finance.api.infrastructure.persistence.repository.JpaIncomeEntryRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("!memory")
public class IncomeEntryPersistenceAdapter implements IncomeEntryRepository {
    private final JpaIncomeEntryRepository repository;

    public IncomeEntryPersistenceAdapter(JpaIncomeEntryRepository repository) {
        this.repository = repository;
    }

    @Override
    public IncomeEntry save(IncomeEntry entry) {
        return IncomeEntryMapper.toDomain(repository.save(IncomeEntryMapper.toEntity(entry)));
    }

    @Override
    public Optional<IncomeEntry> findByIdAndUserId(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId).map(IncomeEntryMapper::toDomain);
    }

    @Override
    public List<IncomeEntry> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to) {
        return repository.findByUserIdAndDateBetweenOrderByDateAscCreatedAtAsc(userId, from, to)
            .stream()
            .map(IncomeEntryMapper::toDomain)
            .toList();
    }

    @Override
    public IncomeEntryPage findPage(UUID userId, LocalDate from, LocalDate to, int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<IncomeEntry> entries = repository.findByUserIdAndDateBetween(userId, from, to, pageable)
            .map(IncomeEntryMapper::toDomain);

        Map<LocalDate, BigDecimal> dailyTotals = repository.sumDailyTotals(userId, from, to)
            .stream()
            .collect(Collectors.toMap(DailyTotalProjection::getDate, DailyTotalProjection::getTotal));

        List<IncomeEntryWithDailyTotal> content = entries.getContent()
            .stream()
            .map(entry -> new IncomeEntryWithDailyTotal(entry, dailyTotals.getOrDefault(entry.date(), BigDecimal.ZERO)))
            .toList();

        return new IncomeEntryPage(content, entries.getTotalElements(), entries.getTotalPages(), page, size);
    }

    @Override
    public void delete(IncomeEntry entry) {
        repository.delete(IncomeEntryMapper.toEntity(entry));
    }
}
