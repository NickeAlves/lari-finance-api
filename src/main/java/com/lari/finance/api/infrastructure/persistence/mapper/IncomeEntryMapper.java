package com.lari.finance.api.infrastructure.persistence.mapper;

import com.lari.finance.api.domain.model.IncomeEntry;
import com.lari.finance.api.domain.model.MoneyBreakdown;
import com.lari.finance.api.infrastructure.persistence.entity.IncomeEntryEntity;

public final class IncomeEntryMapper {
    private IncomeEntryMapper() {
    }

    public static IncomeEntry toDomain(IncomeEntryEntity entity) {
        return new IncomeEntry(
            entity.getId(),
            entity.getUserId(),
            entity.getDate(),
            entity.getClientName(),
            entity.getAmount(),
            entity.getPaymentMethod(),
            new MoneyBreakdown(
                entity.getVatAmount(),
                entity.getFixedExpensesAmount(),
                entity.getProductsAmount(),
                entity.getSalaryAmount(),
                entity.getAnnualTaxReserveAmount()
            ),
            entity.getNotes(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static IncomeEntryEntity toEntity(IncomeEntry entry) {
        return new IncomeEntryEntity(
            entry.id(),
            entry.userId(),
            entry.date(),
            entry.clientName(),
            entry.amount(),
            entry.paymentMethod(),
            entry.breakdown().vatAmount(),
            entry.breakdown().fixedExpensesAmount(),
            entry.breakdown().productsAmount(),
            entry.breakdown().salaryAmount(),
            entry.breakdown().annualTaxReserveAmount(),
            entry.notes(),
            entry.createdAt(),
            entry.updatedAt()
        );
    }
}
