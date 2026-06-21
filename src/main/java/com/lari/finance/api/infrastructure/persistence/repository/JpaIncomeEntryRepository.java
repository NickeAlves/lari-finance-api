package com.lari.finance.api.infrastructure.persistence.repository;

import com.lari.finance.api.infrastructure.persistence.entity.IncomeEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaIncomeEntryRepository extends JpaRepository<IncomeEntryEntity, UUID> {
    Optional<IncomeEntryEntity> findByIdAndUserId(UUID id, UUID userId);

    List<IncomeEntryEntity> findByUserIdAndDateBetweenOrderByDateAscCreatedAtAsc(UUID userId, LocalDate from, LocalDate to);
}
