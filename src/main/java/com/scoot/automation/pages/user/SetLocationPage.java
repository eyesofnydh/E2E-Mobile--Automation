package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * USER app — destination / "Set Location" search screen
 * (activity: com.trioangle.goferhandy.common.views.EditCurrentLocationActivity).
 *
 * Reached from HomePage.tapWhereTo(). SAFETY: searching and viewing place
 * suggestions is safe; selecting a destination advances toward fare/booking,
 * so tests stop at verifying suggestions unless explicitly booking on staging.
 */
public class SetLocationPage extends BasePage {

    private final By title        = resourceId("tv_title");        // "Set Location"
    private final By backArrow    = resourceId("iv_back_arrow");
    private final By searchField  = resourceId("edt_search");       // "Search location"
    private final By homeShortcut = resourceId("rlt_home");
    private final By workShortcut = resourceId("rlt_work");
    private final By setOnMap     = resourceId("rlt_loc_on_map");   // "Set location on map"

    // Place-suggestion rows appear in a RecyclerView as the user types.
    // Each row's primary text is the "address" TextView.
    private final By suggestionRows =
            uiAutomator("new UiSelector().resourceId(\"" + appResourceId("address") + "\")");

    public boolean isLoaded() {
        return isDisplayed(searchField) && isDisplayed(title);
    }

    public String getTitle() {
        return getText(title);
    }

    public SetLocationPage searchDestination(String query) {
        // Destination search uses a live TextWatcher; type via real keystrokes
        // so the place-suggestion API call is triggered.
        typeViaKeystrokes(searchField, query);
        return this;
    }

    /** Count of place suggestions currently shown (0 if none). */
    public int suggestionCount() {
        List<WebElement> rows = driver.findElements(suggestionRows);
        return rows.size();
    }

    /** Waits for place suggestions to load (they arrive async from the Places API). */
    public boolean hasSuggestions() {
        try {
            waitForVisible(suggestionRows); // waits for the first matching suggestion row
            return suggestionCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSetOnMapDisplayed() {
        return isDisplayed(setOnMap);
    }

    /** Selects the first place suggestion and returns to the home screen with it set. */
    public HomePage selectFirstSuggestion() {
        By firstRow = uiAutomator("new UiSelector().resourceId(\""
                + appResourceId("location_placesearch") + "\").instance(0)");
        waitForVisible(suggestionRows); // ensure suggestions have loaded
        tap(firstRow);
        return new HomePage();
    }

    public HomePage tapBack() {
        tap(backArrow);
        return new HomePage();
    }
}
