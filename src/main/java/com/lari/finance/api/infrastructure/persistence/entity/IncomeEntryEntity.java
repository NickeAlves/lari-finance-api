package com.lari.finance.api.infrastructure.persistence.entity;

import com.lari.finance.api.domain.model.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "income_entries")
public class IncomeEntryEntity {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "entry_date", nullable = false)
    private LocalDate date;

    @Column(name = "client_name", nullable = false, length = 160)
    private String clientName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 40)
    private PaymentMethod paymentMethod;

    @Column(name = "vat_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal vatAmount;

    @Column(name = "fixed_expenses_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal fixedExpensesAmount;

    @Column(name = "products_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal productsAmount;

    @Column(name = "salary_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal salaryAmount;

    @Column(name = "annual_tax_reserve_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal annualTaxReserveAmount;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected IncomeEntryEntity() {
    }

    public IncomeEntryEntity(
        UUID id,
        UUID userId,
        LocalDate date,
        String clientName,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        BigDecimal vatAmount,
        BigDecimal fixedExpensesAmount,
        BigDecimal productsAmount,
        BigDecimal salaryAmount,
        BigDecimal annualTaxReserveAmount,
        String notes,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.clientName = clientName;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.vatAmount = vatAmount;
        this.fixedExpensesAmount = fixedExpensesAmount;
        this.productsAmount = productsAmount;
        this.salaryAmount = salaryAmount;
        this.annualTaxReserveAmount = annualTaxReserveAmount;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getClientName() {
        return clientName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public BigDecimal getFixedExpensesAmount() {
        return fixedExpensesAmount;
    }

    public BigDecimal getProductsAmount() {
        return productsAmount;
    }

    public BigDecimal getSalaryAmount() {
        return salaryAmount;
    }

    public BigDecimal getAnnualTaxReserveAmount() {
        return annualTaxReserveAmount;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
