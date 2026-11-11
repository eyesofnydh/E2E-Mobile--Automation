package com.scoot.automation.tests.driver;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.driver.DriverHomePage;
import com.scoot.automation.pages.driver.DriverMenuPage;
import com.scoot.automation.pages.driver.DriverTripRequestsPage;
import com.scoot.automation.utils.StepLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DRIVER app — navigation drawer and the Trip Requests screen.
 * Requires the staging driver account to be signed in.
 * Run: mvn test -DsuiteXmlFile=testng-driver.xml -DtargetApp=driver
 */
public class DriverMenuTest extends BaseTest {

    @Test(description = "Driver drawer shows all core menu items")
    public void driverDrawerShowsItems() {
        new SplashPage().waitUntilLoaded();
        DriverHomePage home = new DriverHomePage();
        Assert.assertTrue(home.isLoaded(), "Driver home did not load");

        StepLogger.step("Open the driver navigation drawer");
        DriverMenuPage menu = home.openMenu();
        Assert.assertTrue(menu.isOpen(), "Driver drawer did not open");

        StepLogger.step("Verify core items (Your Jobs, Trip Requests, Earnings, Ratings, Settings, Support)");
        Assert.assertTrue(menu.hasAllCoreItems(), "One or more driver menu items are missing");
    }

    @Test(description = "Trip Requests screen loads (lists requests or empty state)")
    public void tripRequestsScreenLoads() {
        new SplashPage().waitUntilLoaded();
        DriverHomePage home = new DriverHomePage();
        Assert.assertTrue(home.isLoaded(), "Driver home did not load");

        StepLogger.step("Open Trip Requests from the drawer");
        DriverTripRequestsPage requests = home.openMenu().openTripRequests();
        Assert.assertTrue(requests.isLoaded(), "Trip Requests screen did not load");

        StepLogger.step("Title: " + requests.getTitle()
                + " | requests: " + requests.requestCount() + " | empty: " + requests.isEmpty());
        // Either there are pending requests or the empty state is shown — both are valid.
        Assert.assertTrue(requests.hasRequests() || requests.isEmpty(),
                "Trip Requests screen neither listed requests nor showed an empty state");
    }
}
