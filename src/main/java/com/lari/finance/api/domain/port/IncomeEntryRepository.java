package com.lari.finance.api.domain.port;

import com.lari.finance.api.domain.model.IncomeEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncomeEntryRepository {
    IncomeEntry save(IncomeEntry entry);

    Optional<IncomeEntry> findByIdAndUserId(UUID id, UUID userId);

    List<IncomeEntry> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to);

    void delete(IncomeEntry entry);
}
