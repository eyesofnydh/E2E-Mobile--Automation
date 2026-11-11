package com.scoot.automation.pages.driver;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * DRIVER app — navigation drawer.
 * Items: Your Jobs, Trip Requests, Earnings, Manage Documents, Manage Vehicles,
 * Pay to Admin, Ratings, Referral, Settings, Support, Logout.
 */
public class DriverMenuPage extends BasePage {

    private final By tripRequests = resourceId("nav_tripreuest");
    private final By settings     = resourceId("nav_settings");
    private final By support      = resourceId("nav_support");
    private final By referral     = resourceId("nav_referral");
    private final By logout       = resourceId("tv_logout");

    private By itemByText(String text) {
        return uiAutomator("new UiSelector().resourceId(\"" + appResourceId("design_menu_item_text")
                + "\").text(\"" + text + "\")");
    }

    public boolean isOpen() {
        return isDisplayed(logout) && isDisplayed(tripRequests);
    }

    public boolean hasAllCoreItems() {
        return isDisplayed(itemByText("Your Jobs")) && isDisplayed(tripRequests)
                && isDisplayed(itemByText("Earnings")) && isDisplayed(itemByText("Ratings"))
                && isDisplayed(settings) && isDisplayed(support);
    }

    public DriverTripRequestsPage openTripRequests() {
        tap(tripRequests);
        return new DriverTripRequestsPage();
    }

    public void openYourJobs()  { tap(itemByText("Your Jobs")); }
    public void openEarnings()  { tap(itemByText("Earnings")); }
    public void openRatings()   { tap(itemByText("Ratings")); }
    public void openSettings()  { tap(settings); }
    public void openSupport()   { tap(support); }
    public void openReferral()  { tap(referral); }

    public DriverSignInSignUpPage logout() {
        tap(logout);
        return new DriverSignInSignUpPage();
    }
}
