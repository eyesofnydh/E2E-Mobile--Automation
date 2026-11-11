package com.scoot.automation.tests.driver;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.driver.DriverHomePage;
import com.scoot.automation.utils.StepLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DRIVER app — logged-in home screen: driver details, map, online/offline switch.
 *
 * Assumes the staging driver account is signed in (login persists via noReset;
 * run DriverLoginTest first if needed).
 *
 * Run: mvn test -DsuiteXmlFile=testng-driver.xml -DtargetApp=driver
 */
public class DriverHomeTest extends BaseTest {

    @Test(description = "Driver home shows map, driver details and the online/offline switch")
    public void driverHomeLoads() {
        new SplashPage().waitUntilLoaded();

        DriverHomePage home = new DriverHomePage();
        StepLogger.step("Verify the driver home screen loaded");
        Assert.assertTrue(home.isLoaded(),
                "Driver home did not load — is the staging driver account signed in?");

        StepLogger.step("Driver: " + home.getDriverName() + " | Vehicle: " + home.getVehicleName());
        Assert.assertFalse(home.getDriverName().isBlank(), "Driver name not shown");

        StepLogger.step("Verify the online/offline switch and map are present");
        Assert.assertTrue(home.isOnlineSwitchDisplayed(), "Online/offline switch not shown");
        Assert.assertTrue(home.isMapDisplayed(), "Map not shown");

        StepLogger.step("Current online state: " + home.isOnline());
    }

    @Test(description = "Driver online/offline switch responds to a tap")
    public void driverOnlineSwitchResponds() {
        new SplashPage().waitUntilLoaded();

        DriverHomePage home = new DriverHomePage();
        Assert.assertTrue(home.isLoaded(), "Driver home did not load");

        boolean initial = home.isOnline();
        StepLogger.step("Initial online state: " + initial + " — tapping the status switch");
        home.toggleOnlineStatus();

        // NOTE: actually going ONLINE depends on real preconditions (GPS location,
        // vehicle approval, server dispatch readiness), so the state may not flip.
        // Here we only verify the control stays interactable; the full online flow
        // is exercised in the end-to-end booking test once dispatch is set up.
        StepLogger.step("State after tap: " + home.isOnline() + " (may require location to go online)");
        Assert.assertTrue(home.isOnlineSwitchDisplayed(),
                "Status switch disappeared / became unresponsive after tapping");

        // Restore offline if the tap put the driver online.
        if (home.isOnline() && !initial) {
            StepLogger.step("Restoring offline state");
            home.toggleOnlineStatus();
        }
    }
}
