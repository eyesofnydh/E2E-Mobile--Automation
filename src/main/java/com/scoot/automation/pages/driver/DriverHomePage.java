package com.scoot.automation.pages.driver;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * DRIVER app — logged-in home (activity: goferhandyprovider.common.ui.HandyMainActivity).
 *
 * Has the online/offline status switch (controls whether the driver receives
 * ride requests), a map, driver/vehicle details, and job counts.
 */
public class DriverHomePage extends BasePage {

    private final By statusSwitch  = resourceId("switch_driverstatus"); // online/offline
    private final By mapView       = resourceId("mapview");
    private final By driverName    = resourceId("txt_driver_name");     // "Rijosh Vk"
    private final By vehicleName   = resourceId("txt_vehicle_name");    // "000ag"
    private final By changeVehicle = resourceId("tv_change");
    private final By pendingTrips  = resourceId("ll_pendingTrips");
    private final By upcomingTrips = resourceId("ll_upcomingTrips");

    public boolean isLoaded() {
        return isDisplayed(statusSwitch) && isDisplayed(mapView);
    }

    public String getDriverName() {
        return getText(driverName);
    }

    public String getVehicleName() {
        return getText(vehicleName);
    }

    public boolean isMapDisplayed() {
        return isDisplayed(mapView);
    }

    public boolean isOnlineSwitchDisplayed() {
        return isDisplayed(statusSwitch);
    }

    /** True if the driver is currently online (the status switch is checked). */
    public boolean isOnline() {
        WebElement sw = waitForVisible(statusSwitch);
        return Boolean.parseBoolean(sw.getAttribute("checked"));
    }

    /** Toggles the online/offline status. SAFETY: going online makes the driver
     *  dispatchable on staging — pair with going offline at the end of a test. */
    public DriverHomePage toggleOnlineStatus() {
        tap(statusSwitch);
        return this;
    }

    /**
     * True if the status pill reads "Online". Prefer this over {@link #isOnline()}:
     * the switch's "checked" attribute is unreliable on this build (always false),
     * whereas the visible "Online"/"Offline" label reflects the real state. Going
     * online also requires a live GPS fix, so the label may lag a tap by a moment.
     */
    public boolean isStatusOnline() {
        return isPresent(uiAutomator("new UiSelector().text(\"Online\")"));
    }

    public boolean hasJobCounts() {
        return isDisplayed(pendingTrips) && isDisplayed(upcomingTrips);
    }

    public void tapChangeVehicle() {
        tap(changeVehicle);
    }

    /** Opens the navigation drawer via the toolbar hamburger (first ImageButton). */
    public DriverMenuPage openMenu() {
        tap(uiAutomator("new UiSelector().className(\"android.widget.ImageButton\")"));
        return new DriverMenuPage();
    }
}
