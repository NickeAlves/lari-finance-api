package com.lari.finance.api.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class IncomeEntry {
    private final UUID id;
    private final UUID userId;
    private LocalDate date;
    private String clientName;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private MoneyBreakdown breakdown;
    private String notes;
    private final Instant createdAt;
    private Instant updatedAt;

    public IncomeEntry(
        UUID id,
        UUID userId,
        LocalDate date,
        String clientName,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        MoneyBreakdown breakdown,
        String notes,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        apply(date, clientName, amount, paymentMethod, notes);
        this.breakdown = Objects.requireNonNull(breakdown, "breakdown");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static IncomeEntry create(
        UUID userId,
        LocalDate date,
        String clientName,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String notes
    ) {
        Instant now = Instant.now();
        BigDecimal normalized = validateAmount(amount);
        return new IncomeEntry(
            UUID.randomUUID(),
            userId,
            validateDate(date),
            validateClientName(clientName),
            normalized,
            Objects.requireNonNull(paymentMethod, "paymentMethod"),
            FinancialAllocationPolicy.calculate(normalized),
            normalizeNotes(notes),
            now,
            now
        );
    }

    public void update(LocalDate date, String clientName, BigDecimal amount, PaymentMethod paymentMethod, String notes) {
        apply(date, clientName, amount, paymentMethod, notes);
        this.updatedAt = Instant.now();
    }

    private void apply(LocalDate date, String clientName, BigDecimal amount, PaymentMethod paymentMethod, String notes) {
        BigDecimal normalized = validateAmount(amount);
        this.date = validateDate(date);
        this.clientName = validateClientName(clientName);
        this.amount = normalized;
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "paymentMethod");
        this.breakdown = FinancialAllocationPolicy.calculate(normalized);
        this.notes = normalizeNotes(notes);
    }

    private static LocalDate validateDate(LocalDate date) {
        return Objects.requireNonNull(date, "date");
    }

    private static String validateClientName(String clientName) {
        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la clienta es obligatorio.");
        }
        return clientName.trim();
    }

    private static BigDecimal validateAmount(BigDecimal amount) {
        BigDecimal normalized = FinancialAllocationPolicy.normalize(amount);
        if (normalized.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor que cero.");
        }
        return normalized;
    }

    private static String normalizeNotes(String notes) {
        return notes == null || notes.isBlank() ? null : notes.trim();
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public LocalDate date() {
        return date;
    }

    public String clientName() {
        return clientName;
    }

    public BigDecimal amount() {
        return amount;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    public MoneyBreakdown breakdown() {
        return breakdown;
    }

    public String notes() {
        return notes;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
