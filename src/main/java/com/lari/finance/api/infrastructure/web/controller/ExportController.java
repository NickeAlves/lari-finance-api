package com.lari.finance.api.infrastructure.web.controller;

import com.lari.finance.api.infrastructure.export.IncomeEntryExportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
public class ExportController {
    private static final MediaType XLSX = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final IncomeEntryExportService exportService;

    public ExportController(IncomeEntryExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/api/exports/income-entries.xlsx")
    public ResponseEntity<byte[]> excel(
        Authentication authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) throws IOException {
        return ResponseEntity.ok()
            .contentType(XLSX)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("registro-entradas.xlsx").build().toString())
            .body(exportService.excel(authentication.getName(), from, to));
    }

    @GetMapping("/api/exports/income-entries.pdf")
    public ResponseEntity<byte[]> pdf(
        Authentication authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("registro-entradas.pdf").build().toString())
            .body(exportService.pdf(authentication.getName(), from, to));
    }
}
