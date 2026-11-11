package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * USER app — logged-in home / ride-booking screen
 * (activity: com.trioangle.goferhandy.gofer.views.GoferMainActivity).
 *
 * This is the map + "Where to?" booking entry point. SAFETY: methods here stop
 * at opening the destination screen; they never confirm a ride or payment.
 */
public class HomePage extends BasePage {

    private final By menuButton    = resourceId("iv_menu");            // hamburger -> nav drawer
    private final By welcomeText   = resourceId("tv_welcome_text");    // "Hey <name>"
    private final By mapView       = resourceId("mapview");
    private final By scheduleToggle = resourceId("tv_schedule");       // "Ride Now"
    private final By pickupAddress = resourceId("pickupaddress");
    private final By whereTo       = resourceId("destadddress");       // "Where to?"
    private final By goButton      = resourceId("go_button");
    private final By addStops      = resourceId("btn_add_stops");

    public boolean isLoaded() {
        return isDisplayed(whereTo) && isDisplayed(mapView);
    }

    public String getWelcomeText() {
        return getText(welcomeText);
    }

    public String getPickupAddress() {
        return getText(pickupAddress);
    }

    public String getScheduleMode() {
        return getText(scheduleToggle);
    }

    /** Opens the "Schedule a Ride" bottom sheet (date/time picker). */
    public ScheduleRidePage openSchedule() {
        tap(scheduleToggle);
        return new ScheduleRidePage();
    }

    public NavigationMenuPage openMenu() {
        tap(menuButton);
        return new NavigationMenuPage();
    }

    /** Opens the destination picker. SAFE: this only navigates to the search screen. */
    public SetLocationPage tapWhereTo() {
        tap(whereTo);
        return new SetLocationPage();
    }

    public String getDestinationText() {
        return getText(whereTo);
    }

    public boolean isGoButtonDisplayed() {
        return isDisplayed(goButton);
    }

    /**
     * Taps "Go" to request fare estimates for the chosen pickup/destination.
     * This opens the fare/vehicle screen; it does NOT book a ride yet.
     */
    public FareEstimatePage tapGo() {
        tap(goButton);
        return new FareEstimatePage();
    }

    public void tapAddStops() {
        tap(addStops);
    }

    public boolean isMapDisplayed() {
        return isDisplayed(mapView);
    }
}
