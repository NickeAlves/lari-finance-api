package com.lari.finance.api.infrastructure.web.controller;

import com.lari.finance.api.application.dto.IncomeEntryPage;
import com.lari.finance.api.application.service.IncomeEntryService;
import com.lari.finance.api.domain.model.PaymentMethod;
import com.lari.finance.api.infrastructure.web.dto.IncomeEntryRequest;
import com.lari.finance.api.infrastructure.web.dto.IncomeEntryResponse;
import com.lari.finance.api.infrastructure.web.dto.PagedResponse;
import com.lari.finance.api.infrastructure.web.dto.PaymentMethodResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/entries")
public class IncomeEntryController {
    private final IncomeEntryService incomeEntryService;

    public IncomeEntryController(IncomeEntryService incomeEntryService) {
        this.incomeEntryService = incomeEntryService;
    }

    @GetMapping
    public PagedResponse<IncomeEntryResponse> list(
        Authentication authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "date") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        IncomeEntryPage result = incomeEntryService.list(authentication.getName(), from, to, page, size, sortBy, sortDir);
        List<IncomeEntryResponse> content = result.content().stream().map(IncomeEntryResponse::from).toList();
        return PagedResponse.of(content, result.totalElements(), result.totalPages(), result.page(), result.size());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncomeEntryResponse create(Authentication authentication, @Valid @RequestBody IncomeEntryRequest request) {
        return IncomeEntryResponse.from(incomeEntryService.create(authentication.getName(), request.toCommand()));
    }

    @PutMapping("/{id}")
    public IncomeEntryResponse update(
        Authentication authentication,
        @PathVariable UUID id,
        @Valid @RequestBody IncomeEntryRequest request
    ) {
        return IncomeEntryResponse.from(incomeEntryService.update(authentication.getName(), id, request.toCommand()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable UUID id) {
        incomeEntryService.delete(authentication.getName(), id);
    }

    @GetMapping("/payment-methods")
    public List<PaymentMethodResponse> paymentMethods() {
        return Arrays.stream(PaymentMethod.values()).map(PaymentMethodResponse::from).toList();
    }
}
