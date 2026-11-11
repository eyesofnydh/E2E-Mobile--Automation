package com.scoot.automation.tests.user;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.user.FareEstimatePage;
import com.scoot.automation.pages.user.HomePage;
import com.scoot.automation.pages.user.SetLocationPage;
import com.scoot.automation.utils.StepLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * USER app — selects a destination and views the fare / vehicle options.
 *
 * SAFETY: this stops at the fare-estimate screen and verifies the vehicle list.
 * It does NOT tap "REQUEST" / book a ride. (A dedicated end-to-end booking test
 * that books and has the driver accept is handled separately.)
 *
 * Requires the staging account to be signed in. Run: mvn test -DsuiteXmlFile=testng-home.xml
 */
public class UserFareEstimateTest extends BaseTest {

    @Test(description = "Selecting a destination and tapping Go shows vehicle options with fares")
    public void fareEstimateShowsVehicleOptions() {
        new SplashPage().waitUntilLoaded();

        HomePage home = new HomePage();
        Assert.assertTrue(home.isLoaded(), "Home screen did not load");

        StepLogger.step("Open destination search and search for 'Infopark'");
        SetLocationPage loc = home.tapWhereTo();
        loc.searchDestination("Infopark");

        StepLogger.step("Select the first place suggestion");
        home = loc.selectFirstSuggestion();

        StepLogger.step("Verify the destination is set on the home screen");
        Assert.assertTrue(home.getDestinationText().toLowerCase().contains("infopark"),
                "Destination not set correctly: " + home.getDestinationText());

        StepLogger.step("Tap 'Go' to request fare estimates");
        FareEstimatePage fare = home.tapGo();

        StepLogger.step("Verify the fare/vehicle screen loaded with at least one option");
        Assert.assertTrue(fare.isLoaded(), "Fare estimate screen did not load");
        Assert.assertTrue(fare.availableRideCount() >= 1, "No vehicle options were listed");

        StepLogger.step("Vehicles: " + fare.getVehicleNames() + " | Fares: " + fare.getVehicleFares());
        Assert.assertFalse(fare.getVehicleFares().isEmpty(), "No fares were displayed");

        StepLogger.step("Verify a payment method is shown: " + fare.getPaymentType());
        Assert.assertFalse(fare.getPaymentType().isBlank(), "No payment method shown");

        // Deliberately STOP here — no ride is requested/booked.
        StepLogger.step("Stopping before REQUEST — no booking made. Request button: '"
                + fare.getRequestButtonText() + "'");
    }
}
