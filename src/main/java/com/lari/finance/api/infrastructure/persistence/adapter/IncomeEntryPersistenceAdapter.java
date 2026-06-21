package com.lari.finance.api.infrastructure.persistence.adapter;

import com.lari.finance.api.domain.model.IncomeEntry;
import com.lari.finance.api.domain.port.IncomeEntryRepository;
import com.lari.finance.api.infrastructure.persistence.mapper.IncomeEntryMapper;
import com.lari.finance.api.infrastructure.persistence.repository.JpaIncomeEntryRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public void delete(IncomeEntry entry) {
        repository.delete(IncomeEntryMapper.toEntity(entry));
    }
}
