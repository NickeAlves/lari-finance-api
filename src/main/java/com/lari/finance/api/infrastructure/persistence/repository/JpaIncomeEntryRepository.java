package com.lari.finance.api.infrastructure.persistence.repository;

import com.lari.finance.api.infrastructure.persistence.entity.IncomeEntryEntity;
import com.lari.finance.api.infrastructure.persistence.projection.DailyTotalProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaIncomeEntryRepository extends JpaRepository<IncomeEntryEntity, UUID> {
    Optional<IncomeEntryEntity> findByIdAndUserId(UUID id, UUID userId);

    List<IncomeEntryEntity> findByUserIdAndDateBetweenOrderByDateAscCreatedAtAsc(UUID userId, LocalDate from, LocalDate to);

    Page<IncomeEntryEntity> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to, Pageable pageable);

    @Query("SELECT e.date as date, "
        + "SUM(e.amount - CASE WHEN e.changeGiven = true THEN COALESCE(e.changeAmount, 0) ELSE 0 END) as total "
        + "FROM IncomeEntryEntity e WHERE e.userId = :userId AND e.date BETWEEN :from AND :to GROUP BY e.date")
    List<DailyTotalProjection> sumDailyTotals(@Param("userId") UUID userId, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
