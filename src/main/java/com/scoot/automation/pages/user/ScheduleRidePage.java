package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * USER app — "Schedule a Ride" bottom sheet with a day/hour/minute/AM-PM
 * wheel picker and a "SET YOUR PICKUP TIME" button.
 *
 * Opened from HomePage.openSchedule(). Setting a precise time means swiping the
 * individual wheel pickers; {@link #confirmPickupTime()} accepts the currently
 * selected time (the default is a near-future slot).
 */
public class ScheduleRidePage extends BasePage {

    private final By sheetTitle   = resourceId("scheduleride_text");  // "Schedule a Ride"
    private final By selectedTime = resourceId("time_date");          // "Tue, Nov 03 at 05:20 PM"
    private final By daysPicker   = resourceId("daysPicker");
    private final By hoursPicker  = resourceId("hoursPicker");
    private final By minutesPicker = resourceId("minutesPicker");
    private final By amPmPicker   = resourceId("amPmPicker");
    private final By setButton    = resourceId("set_pickup_window");  // "SET YOUR PICKUP TIME"

    public boolean isLoaded() {
        return isDisplayed(sheetTitle) && isDisplayed(setButton);
    }

    public boolean hasDateTimePickers() {
        return isDisplayed(daysPicker) && isDisplayed(hoursPicker)
                && isDisplayed(minutesPicker) && isDisplayed(amPmPicker);
    }

    public String getSelectedDateTime() {
        return getText(selectedTime);
    }

    /** Nudges the hours wheel up one step (advanced: precise time-setting). */
    public ScheduleRidePage bumpHour() {
        swipeWheelUp(hoursPicker);
        return this;
    }

    /** Confirms the currently selected pickup time and returns to the home screen. */
    public HomePage confirmPickupTime() {
        tap(setButton);
        return new HomePage();
    }

    /** Swipes a wheel picker up by ~one row using its own bounds (no fixed coordinates). */
    private void swipeWheelUp(By picker) {
        var el = waitForVisible(picker);
        var r = el.getRect();
        int x = r.getX() + r.getWidth() / 2;
        int yStart = r.getY() + (int) (r.getHeight() * 0.65);
        int yEnd = r.getY() + (int) (r.getHeight() * 0.35);
        swipeBetween(x, yStart, x, yEnd);
    }
}
