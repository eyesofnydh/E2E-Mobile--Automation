package com.scoot.automation.pages.driver;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * DRIVER app — Trip Requests / Scheduled Rides screen
 * (activity: goferhandyprovider.gofer.views.TripRequestActivity).
 *
 * Lists incoming/scheduled ride requests the driver can accept. When there are
 * none it shows "No Job Request Found". This is the driver side of the
 * user↔driver booking flow.
 */
public class DriverTripRequestsPage extends BasePage {

    private final By title       = resourceId("handy_header_tvTitle"); // "Scheduled Rides"
    private final By backArrow   = resourceId("handy__img_arrow_back");
    private final By noDataFound = resourceId("no_data_found");        // "No Job Request Found"
    private final By requestCards = uiAutomator(
            "new UiSelector().resourceId(\"" + appResourceId("card_view") + "\")");

    public boolean isLoaded() {
        return isDisplayed(title);
    }

    public String getTitle() {
        return getText(title);
    }

    public boolean isEmpty() {
        return isDisplayed(noDataFound);
    }

    public int requestCount() {
        return driver.findElements(requestCards).size();
    }

    public boolean hasRequests() {
        return requestCount() > 0;
    }

    public DriverHomePage tapBack() {
        tap(backArrow);
        return new DriverHomePage();
    }
}
