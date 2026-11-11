package com.scoot.automation.tests.user;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.user.HomePage;
import com.scoot.automation.pages.user.SetLocationPage;
import com.scoot.automation.utils.StepLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * USER app — logged-in home screen and the start of the booking flow.
 *
 * Assumes the staging account is already signed in (login persists via noReset;
 * run testng-login.xml first if the session was cleared).
 *
 * SAFETY: this stops at verifying destination suggestions. It never selects a
 * destination, taps "Go", or confirms a ride — no real booking is made.
 *
 * Run: mvn test -DsuiteXmlFile=testng-home.xml
 */
public class UserHomeBookingEntryTest extends BaseTest {

    @Test(description = "Logged-in home screen shows the map and booking entry point")
    public void homeScreenLoadsForLoggedInUser() {
        new SplashPage().waitUntilLoaded();

        HomePage home = new HomePage();
        StepLogger.step("Verify the home screen (map + 'Where to?') is loaded");
        Assert.assertTrue(home.isLoaded(),
                "Home screen did not load — is the staging account still signed in? (run testng-login.xml)");

        StepLogger.step("Verify the welcome greeting is shown");
        Assert.assertTrue(home.getWelcomeText().startsWith("Hey"),
                "Unexpected welcome text: " + home.getWelcomeText());

        StepLogger.step("Verify the map is displayed");
        Assert.assertTrue(home.isMapDisplayed(), "Map not displayed on home screen");
    }

    @Test(description = "'Where to?' opens destination search and returns place suggestions")
    public void whereToReturnsDestinationSuggestions() {
        new SplashPage().waitUntilLoaded();

        HomePage home = new HomePage();
        Assert.assertTrue(home.isLoaded(), "Home screen did not load");

        StepLogger.step("Tap 'Where to?' to open the destination search");
        SetLocationPage loc = home.tapWhereTo();
        Assert.assertTrue(loc.isLoaded(), "Destination search screen did not load");
        Assert.assertEquals(loc.getTitle(), "Set Location", "Unexpected destination screen title");

        StepLogger.step("Search for a destination and verify suggestions appear");
        loc.searchDestination("Infopark");
        Assert.assertTrue(loc.hasSuggestions(),
                "No place suggestions returned for the search query");

        // Deliberately stop here — selecting a destination would start a booking.
        StepLogger.step("Return to home without selecting a destination (no booking made)");
        loc.tapBack();
    }
}
