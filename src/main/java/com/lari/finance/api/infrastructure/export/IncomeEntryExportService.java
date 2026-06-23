package com.lari.finance.api.infrastructure.export;

import com.lari.finance.api.application.dto.IncomeEntryWithDailyTotal;
import com.lari.finance.api.application.dto.ReportSummary;
import com.lari.finance.api.application.service.IncomeEntryService;
import com.lari.finance.api.application.service.ReportService;
import com.lari.finance.api.domain.model.IncomeEntry;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class IncomeEntryExportService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final IncomeEntryService incomeEntryService;
    private final ReportService reportService;

    public IncomeEntryExportService(IncomeEntryService incomeEntryService, ReportService reportService) {
        this.incomeEntryService = incomeEntryService;
        this.reportService = reportService;
    }

    public byte[] excel(String userEmail, LocalDate from, LocalDate to) throws IOException {
        List<IncomeEntryWithDailyTotal> rows = incomeEntryService.listAll(userEmail, from, to);
        ReportSummary summary = reportService.summarize(userEmail, from, to);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
            headerStyle.setFont(headerFont);

            Sheet entriesSheet = workbook.createSheet("Entradas");
            writeEntriesSheet(entriesSheet, headerStyle, rows);

            Sheet summarySheet = workbook.createSheet("Resumen");
            writeSummarySheet(summarySheet, headerStyle, summary);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] pdf(String userEmail, LocalDate from, LocalDate to) {
        List<IncomeEntryWithDailyTotal> rows = incomeEntryService.listAll(userEmail, from, to);
        ReportSummary summary = reportService.summarize(userEmail, from, to);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            document.add(new Paragraph("Registro de entradas", titleFont));
            document.add(new Paragraph("Periodo: " + format(summary.from()) + " - " + format(summary.to())));
            document.add(new Paragraph("Total: " + money(summary.totalAmount()) + " | Servicios: " + summary.servicesCount()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 1.2f, 2.2f, 1f, 1.4f, 1f, 1f, 1f, 1f, 1f, 1.1f });
            List.of(
                "Fecha",
                "Clienta",
                "Importe",
                "Forma",
                "IVA 21%",
                "Gastos 20%",
                "Productos 8%",
                "Salario 41%",
                "Impuestos 10%",
                "Total dia"
            ).forEach(header -> table.addCell(headerCell(header)));

            for (IncomeEntryWithDailyTotal row : rows) {
                IncomeEntry entry = row.entry();
                table.addCell(format(entry.date()));
                table.addCell(entry.clientName());
                table.addCell(money(entry.amount()));
                table.addCell(entry.paymentMethod().label());
                table.addCell(money(entry.breakdown().vatAmount()));
                table.addCell(money(entry.breakdown().fixedExpensesAmount()));
                table.addCell(money(entry.breakdown().productsAmount()));
                table.addCell(money(entry.breakdown().salaryAmount()));
                table.addCell(money(entry.breakdown().annualTaxReserveAmount()));
                table.addCell(money(row.dailyTotal()));
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | IOException exception) {
            throw new IllegalStateException("No se pudo generar el PDF.", exception);
        }
    }

    private void writeEntriesSheet(Sheet sheet, CellStyle headerStyle, List<IncomeEntryWithDailyTotal> rows) {
        List<String> headers = List.of(
            "Fecha",
            "Nombre de la clienta",
            "Importe",
            "Forma de pago",
            "IVA (21%)",
            "Gastos fijos (20%)",
            "Productos (8%)",
            "Salario (41%)",
            "Reserva impuesto anual (10%)",
            "Total del dia",
            "Notas"
        );
        writeHeader(sheet, headerStyle, headers);

        int rowIndex = 1;
        for (IncomeEntryWithDailyTotal tableRow : rows) {
            IncomeEntry entry = tableRow.entry();
            Row row = sheet.createRow(rowIndex++);
            write(row, 0, format(entry.date()));
            write(row, 1, entry.clientName());
            write(row, 2, entry.amount());
            write(row, 3, entry.paymentMethod().label());
            write(row, 4, entry.breakdown().vatAmount());
            write(row, 5, entry.breakdown().fixedExpensesAmount());
            write(row, 6, entry.breakdown().productsAmount());
            write(row, 7, entry.breakdown().salaryAmount());
            write(row, 8, entry.breakdown().annualTaxReserveAmount());
            write(row, 9, tableRow.dailyTotal());
            write(row, 10, entry.notes());
        }

        autosize(sheet, headers.size());
    }

    private void writeSummarySheet(Sheet sheet, CellStyle headerStyle, ReportSummary summary) {
        writeHeader(sheet, headerStyle, List.of("Concepto", "Valor"));
        write(sheet.createRow(1), 0, "Periodo");
        write(sheet.getRow(1), 1, format(summary.from()) + " - " + format(summary.to()));
        write(sheet.createRow(2), 0, "Servicios");
        write(sheet.getRow(2), 1, BigDecimal.valueOf(summary.servicesCount()));
        write(sheet.createRow(3), 0, "Total");
        write(sheet.getRow(3), 1, summary.totalAmount());
        write(sheet.createRow(4), 0, "IVA (21%)");
        write(sheet.getRow(4), 1, summary.vatAmount());
        write(sheet.createRow(5), 0, "Gastos fijos (20%)");
        write(sheet.getRow(5), 1, summary.fixedExpensesAmount());
        write(sheet.createRow(6), 0, "Productos (8%)");
        write(sheet.getRow(6), 1, summary.productsAmount());
        write(sheet.createRow(7), 0, "Salario (41%)");
        write(sheet.getRow(7), 1, summary.salaryAmount());
        write(sheet.createRow(8), 0, "Reserva impuesto anual (10%)");
        write(sheet.getRow(8), 1, summary.annualTaxReserveAmount());

        int rowIndex = 11;
        Row methodHeader = sheet.createRow(rowIndex++);
        methodHeader.createCell(0).setCellValue("Forma de pago");
        methodHeader.createCell(1).setCellValue("Servicios");
        methodHeader.createCell(2).setCellValue("Total");
        methodHeader.forEach(cell -> cell.setCellStyle(headerStyle));
        for (ReportSummary.PaymentMethodSummary method : summary.paymentMethods()) {
            Row row = sheet.createRow(rowIndex++);
            write(row, 0, method.label());
            write(row, 1, BigDecimal.valueOf(method.servicesCount()));
            write(row, 2, method.totalAmount());
        }

        autosize(sheet, 3);
    }

    private void writeHeader(Sheet sheet, CellStyle headerStyle, List<String> headers) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }
    }

    private void write(Row row, int index, String value) {
        row.createCell(index).setCellValue(value == null ? "" : value);
    }

    private void write(Row row, int index, BigDecimal value) {
        row.createCell(index).setCellValue(value == null ? 0 : value.doubleValue());
    }

    private void autosize(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private PdfPCell headerCell(String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 9, Font.BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private String format(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : value.toPlainString();
    }
}
