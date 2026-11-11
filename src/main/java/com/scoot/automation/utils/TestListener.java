package com.scoot.automation.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.scoot.automation.base.DriverManager;
import com.scoot.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Single TestNG listener that wires reporting + screenshots + bug sheet.
 * Registered in testng.xml. (Allure's own listener auto-registers separately
 * via the allure-testng jar, so this class focuses on Extent + artifacts.)
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.getInstance();
        log.info("===== Test run started: {} =====", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentReportManager.createTest(testName(result));
        CURRENT.set(test);
        StepLogger.bindExtent(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        CURRENT.get().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable error = result.getThrowable();
        log.error("Test FAILED: {}", testName(result), error);

        String screenshotPath = captureScreenshot(testName(result));

        ExtentTest test = CURRENT.get();
        if (test != null) {
            test.log(Status.FAIL, error);
            if (screenshotPath != null) {
                try {
                    test.addScreenCaptureFromPath(toRelative(screenshotPath));
                } catch (Exception ignored) {
                }
            }
        }

        writeBug(result, error, screenshotPath);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (CURRENT.get() != null) {
            CURRENT.get().log(Status.SKIP, "Test skipped");
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flush();
        StepLogger.clear();
        log.info("===== Test run finished. Extent report: {} =====",
                ExtentReportManager.getReportPath());
    }

    // ---- helpers ---------------------------------------------------------

    private String captureScreenshot(String name) {
        AndroidDriver driver = DriverManager.getDriver();
        if (driver == null) {
            return null;
        }
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            File dir = new File("target/screenshots");
            dir.mkdirs();
            File dest = new File(dir, name + "-" + stamp + ".png");
            Files.write(dest.toPath(), png);

            // Also attach to the Allure report.
            Allure.addAttachment(name, new ByteArrayInputStream(png));
            log.info("Saved failure screenshot: {}", dest.getPath());
            return dest.getPath();
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
            return null;
        }
    }

    private void writeBug(ITestResult result, Throwable error, String screenshotPath) {
        BugSheetLogger.Bug bug = new BugSheetLogger.Bug();
        bug.app = ConfigReader.targetApp();
        bug.module = result.getTestClass().getRealClass().getSimpleName();
        bug.scenario = result.getMethod().getMethodName();
        bug.severity = "Medium"; // tune per test; could be driven by a custom annotation later
        bug.steps = StepLogger.formattedSteps();
        bug.expected = "Test assertion should pass (see scenario).";
        bug.actual = error == null ? "Unknown failure" : String.valueOf(error.getMessage());
        bug.status = "Needs triage";
        bug.screenshotPath = screenshotPath;
        BugSheetLogger.logBug(bug);
    }

    private String testName(ITestResult result) {
        return result.getMethod().getMethodName();
    }

    /** Extent HTML lives in target/extent-report/; screenshots in target/screenshots/. */
    private String toRelative(String screenshotPath) {
        return "../screenshots/" + new File(screenshotPath).getName();
    }
}
