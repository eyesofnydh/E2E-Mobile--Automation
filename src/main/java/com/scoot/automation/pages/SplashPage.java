package com.scoot.automation.pages;

import com.scoot.automation.config.ConfigReader;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * Sample page object for the first screen (SplashActivity) of whichever app
 * is under test. It intentionally avoids app-specific UI locators for now,
 * because the goal of the smoke test is only "did the app launch and show
 * its first screen?".
 *
 * Once we have the real splash/login UI (via Appium Inspector), add concrete
 * locators here (logo, "Get Started" button, language selector, etc.)
 * following the accessibility-id > resource-id > UiAutomator > XPath order.
 */
public class SplashPage extends BasePage {

    private final String expectedPackage = ConfigReader.appPackage();

    /**
     * Waits until the app-under-test is the foreground package, i.e. the
     * splash screen actually rendered rather than the launcher or a crash dialog.
     */
    public SplashPage waitUntilLoaded() {
        log.info("Waiting for '{}' to be in the foreground...", expectedPackage);
        ExpectedCondition<Boolean> appInForeground =
                d -> expectedPackage.equals(driver.getCurrentPackage());
        wait.until(appInForeground);
        log.info("App is loaded. Current activity: {}", currentActivity());
        return this;
    }

    public boolean isAppInForeground() {
        return expectedPackage.equals(currentPackage());
    }

    public String expectedPackage() {
        return expectedPackage;
    }
}
