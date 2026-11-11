package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * USER app — fare / vehicle selection screen shown after tapping "Go"
 * (layout id activity_fare_estimation, hosted in GoferMainActivity).
 *
 * Lists available vehicle types with fares and a payment method, plus the
 * "REQUEST <vehicle>" button that books the ride.
 *
 * SAFETY: {@link #requestRide()} actually books a ride on staging (with the
 * test card shown on screen). Only call it in an explicit booking test that
 * also handles cancellation or driver acceptance — not in read-only checks.
 */
public class FareEstimatePage extends BasePage {

    private final By availableRidesTitle = resourceId("tv_available_rides"); // "Available Rides"
    private final By vehicleNames        = uiAutomator(
            "new UiSelector().resourceId(\"" + appResourceId("car_name") + "\")");
    private final By vehicleFares        = uiAutomator(
            "new UiSelector().resourceId(\"" + appResourceId("car_amount") + "\")");
    private final By paymentType         = resourceId("tv_payment_type");
    private final By promoCode           = resourceId("rlt_promo_code");
    private final By requestButton       = resourceId("requestuber");        // "REQUEST SCOOT X"
    private final By scheduleRide        = resourceId("scheduleride");

    /** Waits for the vehicle list to populate — fares load async after "Go". */
    public boolean isLoaded() {
        try {
            waitForVisible(vehicleNames);   // first vehicle row visible
            return !driver.findElements(vehicleNames).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getVehicleNames() {
        return driver.findElements(vehicleNames).stream()
                .map(WebElement::getText).collect(Collectors.toList());
    }

    public List<String> getVehicleFares() {
        return driver.findElements(vehicleFares).stream()
                .map(WebElement::getText).collect(Collectors.toList());
    }

    public int availableRideCount() {
        return driver.findElements(vehicleNames).size();
    }

    public String getPaymentType() {
        return getText(paymentType);
    }

    /** Selects a vehicle type by its visible name (e.g. "Scoot X", "Comfort"). */
    public FareEstimatePage selectVehicle(String vehicleName) {
        tap(uiAutomator("new UiSelector().resourceId(\"" + appResourceId("car_name")
                + "\").text(\"" + vehicleName + "\")"));
        return this;
    }

    public String getRequestButtonText() {
        return getText(requestButton);
    }

    public boolean isPromoAvailable() {
        return isDisplayed(promoCode);
    }

    public boolean isScheduleRideAvailable() {
        return isDisplayed(scheduleRide);
    }

    /** SAFETY: books the ride for real on staging. */
    public void requestRide() {
        tap(requestButton);
    }
}
