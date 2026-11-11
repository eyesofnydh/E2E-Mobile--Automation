package com.scoot.automation.tests.user;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.user.HomePage;
import com.scoot.automation.pages.user.NavigationMenuPage;
import com.scoot.automation.pages.user.YourTripsPage;
import com.scoot.automation.utils.StepLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * USER app — navigation drawer and ride history (read-only; no logout,
 * no booking). Requires the staging account to be signed in.
 * Run: mvn test -DsuiteXmlFile=testng-account.xml
 */
public class UserMenuHistoryTest extends BaseTest {

    @Test(description = "Navigation drawer shows all core menu items")
    public void drawerMenuShowsCoreItems() {
        new SplashPage().waitUntilLoaded();
        HomePage home = new HomePage();
        Assert.assertTrue(home.isLoaded(), "Home screen did not load");

        StepLogger.step("Open the navigation drawer");
        NavigationMenuPage menu = home.openMenu();
        Assert.assertTrue(menu.isOpen(), "Navigation drawer did not open");

        StepLogger.step("Verify core menu items are present (Profile, Bookings, Wallet, Payment, Settings, Support)");
        Assert.assertTrue(menu.hasAllCoreItems(), "One or more core menu items are missing");
    }

    @Test(description = "Your Bookings shows ride history with Past/Upcoming tabs")
    public void yourBookingsShowsHistory() {
        new SplashPage().waitUntilLoaded();
        HomePage home = new HomePage();
        Assert.assertTrue(home.isLoaded(), "Home screen did not load");

        StepLogger.step("Open drawer and go to Your Bookings");
        YourTripsPage trips = home.openMenu().openYourBookings();

        StepLogger.step("Verify the ride history screen loaded");
        Assert.assertTrue(trips.isLoaded(), "Your Bookings screen did not load");
        Assert.assertEquals(trips.getTitle(), "Your Job", "Unexpected ride history title");

        StepLogger.step("Verify Past and Upcoming tabs are present");
        Assert.assertTrue(trips.hasPastAndUpcomingTabs(), "Past/Upcoming tabs not found");

        StepLogger.step("Past-trip cards visible: " + trips.visibleTripCount());
    }
}
