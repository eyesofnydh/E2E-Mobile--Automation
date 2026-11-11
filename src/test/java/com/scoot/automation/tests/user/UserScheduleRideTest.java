package com.scoot.automation.tests.user;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.user.HomePage;
import com.scoot.automation.pages.user.ScheduleRidePage;
import com.scoot.automation.utils.StepLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * USER app — "Schedule a Ride" bottom sheet (date/time picker).
 *
 * Verifies the scheduling UI and that a pickup time can be confirmed. It does
 * NOT complete a scheduled booking (that needs destination + Go + REQUEST).
 * Requires the staging account to be signed in.
 */
public class UserScheduleRideTest extends BaseTest {

    @Test(description = "Schedule-a-Ride sheet shows the date/time picker")
    public void scheduleSheetShowsDateTimePicker() {
        new SplashPage().waitUntilLoaded();
        HomePage home = new HomePage();
        Assert.assertTrue(home.isLoaded(), "Home screen did not load");

        StepLogger.step("Open the 'Schedule a Ride' sheet");
        ScheduleRidePage schedule = home.openSchedule();
        Assert.assertTrue(schedule.isLoaded(), "Schedule sheet did not open");

        StepLogger.step("Verify day/hour/minute/AM-PM pickers are present");
        Assert.assertTrue(schedule.hasDateTimePickers(), "Date/time pickers not shown");

        StepLogger.step("Default selected pickup time: " + schedule.getSelectedDateTime());
        Assert.assertFalse(schedule.getSelectedDateTime().isBlank(), "No default time shown");
    }

    @Test(description = "Confirming a pickup time returns to the home screen in scheduled mode")
    public void confirmPickupTimeReturnsHome() {
        new SplashPage().waitUntilLoaded();
        HomePage home = new HomePage();
        Assert.assertTrue(home.isLoaded(), "Home screen did not load");

        StepLogger.step("Open schedule sheet and confirm the default pickup time");
        HomePage afterSchedule = home.openSchedule().confirmPickupTime();

        StepLogger.step("Verify we are back on the home screen (no booking created)");
        Assert.assertTrue(afterSchedule.isLoaded(), "Did not return to home after setting pickup time");
    }
}
