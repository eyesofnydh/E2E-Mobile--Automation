package com.scoot.automation.tests;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.utils.StepLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Smoke test: launch the selected Scoot app and verify its first screen loads.
 *
 * Works for both the user and driver app because it targets whatever
 * `targetApp` resolves to. It makes NO bookings and touches no payment flow.
 *
 * Run against the driver app with:  mvn test -DtargetApp=driver
 */
public class SmokeTest extends BaseTest {

    @Test(description = "App launches and the first screen (SplashActivity) loads")
    public void appLaunchesAndFirstScreenLoads() {
        SplashPage splash = new SplashPage();

        StepLogger.step("Launch the Scoot app and wait for the splash screen");
        splash.waitUntilLoaded();

        StepLogger.step("Verify the app-under-test is the foreground application");
        Assert.assertTrue(splash.isAppInForeground(),
                "Expected '" + splash.expectedPackage() + "' to be in the foreground, but it was not.");

        StepLogger.step("Verify the launched package matches the expected package");
        Assert.assertEquals(splash.currentPackage(), splash.expectedPackage(),
                "Launched package did not match the configured target app package.");
    }
}
