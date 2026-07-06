package com.lari.finance.api.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private boolean changeGiven;
    private PaymentMethod changeMethod;
    private BigDecimal changeAmount;
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
        boolean changeGiven,
        PaymentMethod changeMethod,
        BigDecimal changeAmount,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        apply(date, clientName, amount, paymentMethod, notes, changeGiven, changeMethod, changeAmount);
        this.breakdown = Objects.requireNonNull(breakdown, "breakdown");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static IncomeEntry create(
        UUID userId,
        LocalDate date,
        String clientName,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String notes,
        boolean changeGiven,
        PaymentMethod changeMethod,
        BigDecimal changeAmount
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
            changeGiven,
            changeMethod,
            changeAmount,
            now,
            now
        );
    }

    public void update(
        LocalDate date,
        String clientName,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String notes,
        boolean changeGiven,
        PaymentMethod changeMethod,
        BigDecimal changeAmount
    ) {
        apply(date, clientName, amount, paymentMethod, notes, changeGiven, changeMethod, changeAmount);
        this.updatedAt = Instant.now();
    }

    private void apply(
        LocalDate date,
        String clientName,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String notes,
        boolean changeGiven,
        PaymentMethod changeMethod,
        BigDecimal changeAmount
    ) {
        BigDecimal normalized = validateAmount(amount);
        this.date = validateDate(date);
        this.clientName = validateClientName(clientName);
        this.amount = normalized;
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "paymentMethod");
        this.breakdown = FinancialAllocationPolicy.calculate(normalized);
        this.notes = normalizeNotes(notes);
        this.changeGiven = changeGiven;
        this.changeMethod = validateChangeMethod(changeGiven, changeMethod);
        this.changeAmount = validateChangeAmount(changeGiven, changeAmount);
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

    private static PaymentMethod validateChangeMethod(boolean changeGiven, PaymentMethod changeMethod) {
        if (!changeGiven) {
            return null;
        }
        if (changeMethod != PaymentMethod.EFECTIVO && changeMethod != PaymentMethod.BIZUM) {
            throw new IllegalArgumentException("Selecciona cómo se dio el cambio (Efectivo o Bizum).");
        }
        return changeMethod;
    }

    private static BigDecimal validateChangeAmount(boolean changeGiven, BigDecimal changeAmount) {
        if (!changeGiven) {
            return null;
        }
        if (changeAmount == null || changeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Indica el importe del cambio dado.");
        }
        return changeAmount.setScale(2, RoundingMode.HALF_UP);
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

    public boolean changeGiven() {
        return changeGiven;
    }

    public PaymentMethod changeMethod() {
        return changeMethod;
    }

    public BigDecimal changeAmount() {
        return changeAmount;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
