package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * USER app — ride history / "Your Job" screen
 * (activity: com.trioangle.goferhandy.common.trips.YourTrips).
 *
 * Has "Past" and "Upcoming" tabs; each lists trip cards with status, fare,
 * vehicle and Job ID.
 */
public class YourTripsPage extends BasePage {

    private final By title     = resourceId("tv_title");     // "Your Job"
    private final By backArrow = resourceId("iv_back_arrow");
    private final By tripCards = uiAutomator(
            "new UiSelector().resourceId(\"" + appResourceId("card_view") + "\")");
    private final By pastTab     = uiAutomator("new UiSelector().text(\"Past\")");
    private final By upcomingTab = uiAutomator("new UiSelector().text(\"Upcoming\")");

    public boolean isLoaded() {
        return isDisplayed(title) && (isDisplayed(pastTab) || isDisplayed(upcomingTab));
    }

    public String getTitle() {
        return getText(title);
    }

    public boolean hasPastAndUpcomingTabs() {
        return isDisplayed(pastTab) && isDisplayed(upcomingTab);
    }

    public int visibleTripCount() {
        return driver.findElements(tripCards).size();
    }

    public YourTripsPage openPastTab() {
        tap(pastTab);
        return this;
    }

    public YourTripsPage openUpcomingTab() {
        tap(upcomingTab);
        return this;
    }

    public HomePage tapBack() {
        tap(backArrow);
        return new HomePage();
    }
}
