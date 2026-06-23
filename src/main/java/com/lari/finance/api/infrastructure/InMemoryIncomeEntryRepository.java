package com.lari.finance.api.infrastructure;

import com.lari.finance.api.application.dto.IncomeEntryPage;
import com.lari.finance.api.application.dto.IncomeEntryWithDailyTotal;
import com.lari.finance.api.domain.model.IncomeEntry;
import com.lari.finance.api.domain.port.IncomeEntryRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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
	public IncomeEntryPage findPage(UUID userId, LocalDate from, LocalDate to, int page, int size, String sortBy, String sortDir) {
		List<IncomeEntry> all = entries.values().stream()
				.filter(entry -> entry.userId().equals(userId))
				.filter(entry -> !entry.date().isBefore(from) && !entry.date().isAfter(to))
				.sorted(buildComparator(sortBy, sortDir))
				.toList();

		Map<LocalDate, BigDecimal> dailyTotals = all.stream()
				.collect(Collectors.groupingBy(IncomeEntry::date,
						Collectors.reducing(BigDecimal.ZERO, IncomeEntry::amount, BigDecimal::add)));

		long totalElements = all.size();
		int totalPages = (int) Math.ceil((double) totalElements / size);
		int fromIndex = page * size;
		int toIndex = Math.min(fromIndex + size, (int) totalElements);

		List<IncomeEntryWithDailyTotal> content = (fromIndex >= totalElements)
				? List.of()
				: all.subList(fromIndex, toIndex).stream()
						.map(entry -> new IncomeEntryWithDailyTotal(entry, dailyTotals.getOrDefault(entry.date(), BigDecimal.ZERO)))
						.toList();

		return new IncomeEntryPage(content, totalElements, totalPages, page, size);
	}

	@Override
	public void delete(IncomeEntry entry) {
		entries.remove(entry.id());
	}

	private Comparator<IncomeEntry> buildComparator(String sortBy, String sortDir) {
		Comparator<IncomeEntry> comparator = switch (sortBy) {
			case "clientName" -> Comparator.comparing(IncomeEntry::clientName);
			case "amount" -> Comparator.comparing(IncomeEntry::amount);
			case "createdAt" -> Comparator.comparing(IncomeEntry::createdAt);
			default -> Comparator.comparing(IncomeEntry::date).thenComparing(IncomeEntry::createdAt);
		};
		return sortDir.equalsIgnoreCase("desc") ? comparator.reversed() : comparator;
	}
}
