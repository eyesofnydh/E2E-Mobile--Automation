package com.scoot.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Appends one row to an Excel (.xlsx) bug sheet every time a test fails.
 *
 * Column layout mirrors the client's existing "Issues" tracker tab
 * (Date / Issue description / App / Department / Task / Hr needed / Status)
 * and adds proper QA fields: Severity, Steps to Reproduce, Expected, Actual,
 * and a link to the failure screenshot.
 *
 * The workbook is created with a header row on first failure and reused after,
 * so a single BugReport.xlsx accumulates across runs.
 */
public final class BugSheetLogger {

    private static final Logger log = LogManager.getLogger(BugSheetLogger.class);
    private static final String FILE_PATH = "target/bug-sheet/Scoot-BugSheet.xlsx";
    private static final String SHEET_NAME = "Issues";

    private static final String[] HEADERS = {
            "Bug ID", "Date", "App", "Functionality / Module", "Test / Scenario",
            "Severity", "Steps to Reproduce", "Expected Result", "Actual Result",
            "Status", "Screenshot", "Hr needed for fix"
    };

    private BugSheetLogger() {
    }

    /** One POJO per failed test. */
    public static class Bug {
        public String app;            // user | driver
        public String module;         // functionality under test
        public String scenario;       // test/method name or description
        public String severity;       // Critical / High / Medium / Low
        public String steps;          // numbered repro steps
        public String expected;
        public String actual;         // exception message
        public String status = "Needs triage";
        public String screenshotPath;
    }

    public static synchronized void logBug(Bug bug) {
        try {
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();

            Workbook workbook;
            Sheet sheet;
            if (file.exists()) {
                try (FileInputStream in = new FileInputStream(file)) {
                    workbook = WorkbookFactory.create(in);
                }
                sheet = workbook.getSheet(SHEET_NAME);
                if (sheet == null) {
                    sheet = workbook.createSheet(SHEET_NAME);
                    writeHeader(workbook, sheet);
                }
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet(SHEET_NAME);
                writeHeader(workbook, sheet);
            }

            int rowNum = sheet.getLastRowNum() + 1;
            // Bug ID = sequential based on existing data rows
            String bugId = "SCOOT-" + String.format("%04d", rowNum);

            Row row = sheet.createRow(rowNum);
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            set(row, 0, bugId);
            set(row, 1, today);
            set(row, 2, nvl(bug.app));
            set(row, 3, nvl(bug.module));
            set(row, 4, nvl(bug.scenario));
            set(row, 5, nvl(bug.severity));
            set(row, 6, nvl(bug.steps));
            set(row, 7, nvl(bug.expected));
            set(row, 8, nvl(bug.actual));
            set(row, 9, nvl(bug.status));
            set(row, 10, nvl(bug.screenshotPath));
            set(row, 11, "");

            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }
            workbook.close();
            log.info("Logged bug {} to {}", bugId, FILE_PATH);
        } catch (Exception e) {
            log.error("Could not write to bug sheet: {}", e.getMessage(), e);
        }
    }

    private static void writeHeader(Workbook workbook, Sheet sheet) {
        Row header = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(style);
            sheet.setColumnWidth(i, 22 * 256);
        }
    }

    private static void set(Row row, int col, String value) {
        row.createCell(col).setCellValue(value);
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    public static String filePath() {
        return FILE_PATH;
    }
}
