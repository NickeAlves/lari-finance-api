package com.lari.finance.api.application.dto;

import java.util.List;

public record IncomeEntryPage(
    List<IncomeEntryWithDailyTotal> content,
    long totalElements,
    int totalPages,
    int page,
    int size
) {}
