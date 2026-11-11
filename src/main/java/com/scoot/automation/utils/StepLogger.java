package com.scoot.automation.utils;

import com.aventstack.extentreports.ExtentTest;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One call records a human-readable test step everywhere at once:
 *   - Log4j2 (console + file)
 *   - the Extent report (current test node)
 *   - the Allure report (as a step)
 *   - an in-memory list, so the bug sheet can print full repro steps on failure.
 *
 * Usage inside a test:  StepLogger.step("Open the booking screen");
 */
public final class StepLogger {

    private static final Logger log = LogManager.getLogger(StepLogger.class);
    private static final ThreadLocal<List<String>> STEPS = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<ExtentTest> EXTENT = new ThreadLocal<>();

    private StepLogger() {
    }

    /** Called by the listener at the start of each test. */
    public static void reset() {
        STEPS.set(new ArrayList<>());
    }

    /** Bind the current Extent test node so steps show up in the HTML report. */
    public static void bindExtent(ExtentTest test) {
        EXTENT.set(test);
    }

    public static void step(String message) {
        STEPS.get().add(message);
        log.info("STEP: {}", message);
        if (EXTENT.get() != null) {
            EXTENT.get().info(message);
        }
        // Allure records this as a passed step in the report timeline.
        Allure.step(message);
    }

    /** Full repro steps for the current test, numbered, as a single string. */
    public static String formattedSteps() {
        List<String> steps = STEPS.get();
        if (steps.isEmpty()) {
            return "(no explicit steps recorded)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            sb.append(i + 1).append(". ").append(steps.get(i));
            if (i < steps.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static List<String> steps() {
        return Collections.unmodifiableList(STEPS.get());
    }

    public static void clear() {
        STEPS.remove();
        EXTENT.remove();
    }
}
