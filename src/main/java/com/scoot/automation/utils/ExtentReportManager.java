package com.scoot.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.scoot.automation.config.ConfigReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Single ExtentReports instance for the whole run; one timestamped HTML file
 * under target/extent-report/. The TestListener creates a test node per method.
 */
public final class ExtentReportManager {

    private static ExtentReports extent;
    private static String reportPath;

    private ExtentReportManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            reportPath = "target/extent-report/Scoot-ExtentReport-" + stamp + ".html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("Scoot Automation Report");
            spark.config().setReportName("Scoot - " + ConfigReader.targetApp() + " app");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Target App", ConfigReader.targetApp());
            extent.setSystemInfo("Package", ConfigReader.appPackage());
            extent.setSystemInfo("Device", ConfigReader.get("deviceName", "n/a"));
            extent.setSystemInfo("Platform", "Android " + ConfigReader.get("platformVersion", "?"));
            extent.setSystemInfo("Environment", ConfigReader.get("env", "n/a"));
        }
        return extent;
    }

    public static ExtentTest createTest(String name) {
        return getInstance().createTest(name);
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static String getReportPath() {
        return reportPath;
    }
}
