package com.lari.finance.api.infrastructure.web.controller;

import com.lari.finance.api.application.dto.ReportSummary;
import com.lari.finance.api.application.service.ReportService;
import com.lari.finance.api.infrastructure.web.dto.CalendarDayResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/api/reports/summary")
    public ReportSummary summary(
        Authentication authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return reportService.summarize(authentication.getName(), from, to);
    }

    @GetMapping("/api/calendar")
    public List<CalendarDayResponse> calendar(
        Authentication authentication,
        @RequestParam int year,
        @RequestParam int month
    ) {
        return reportService.calendar(authentication.getName(), year, month)
            .stream()
            .map(CalendarDayResponse::from)
            .toList();
    }
}
