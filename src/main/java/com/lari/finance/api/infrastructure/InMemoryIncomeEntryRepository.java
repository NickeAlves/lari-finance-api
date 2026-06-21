package com.lari.finance.api.infrastructure;

import com.lari.finance.api.domain.model.IncomeEntry;
import com.lari.finance.api.domain.port.IncomeEntryRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Profile("memory")
public class InMemoryIncomeEntryRepository implements IncomeEntryRepository {
	private final ConcurrentMap<UUID, IncomeEntry> entries = new ConcurrentHashMap<>();

	@Override
	public IncomeEntry save(IncomeEntry entry) {
		entries.put(entry.id(), entry);
		return entry;
	}

	@Override
	public Optional<IncomeEntry> findByIdAndUserId(UUID id, UUID userId) {
		return Optional.ofNullable(entries.get(id))
				.filter(entry -> entry.userId().equals(userId));
	}

	@Override
	public List<IncomeEntry> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to) {
		return entries.values().stream()
				.filter(entry -> entry.userId().equals(userId))
				.filter(entry -> !entry.date().isBefore(from) && !entry.date().isAfter(to))
				.sorted(Comparator.comparing(IncomeEntry::date).thenComparing(IncomeEntry::clientName))
				.toList();
	}

	@Override
	public void delete(IncomeEntry entry) {
		entries.remove(entry.id());
	}
}
