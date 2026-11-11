package com.scoot.automation.pages;

import com.scoot.automation.utils.StepLogger;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Post-trip rating screen, shared by both apps:
 *  - User app:   "Rate your ride"  (activity DriverRatingActivity)
 *  - Driver app: "Rate Your Job"   (activity Riderrating)
 *
 * Both show a star row + a comment box + SUBMIT (and a Skip link). Star widgets
 * differ between the apps, so this gives 5 stars best-effort and always falls
 * back to Skip so the ride can be closed out and the app returns home.
 */
public class RatingPage extends BasePage {

    private final By ratingBar   = AppiumBy.className("android.widget.RatingBar");
    private final By driverRating = resourceId("driver_rating");   // driver app RatingBar container
    private final By submitById  = resourceId("rate_submit");       // driver app SUBMIT
    private final By submitByText = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"SUBMIT\")");
    private final By skipByText   = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Skip\")");

    public boolean isShowing() {
        return isPresent(ratingBar) || isPresent(driverRating)
                || isPresent(submitById) || isPresent(submitByText) || isPresent(skipByText);
    }

    /** Gives 5 stars (best effort) and submits; falls back to Skip. */
    public void rateFiveAndSubmit() {
        giveFiveStars();
        if (clickFirst(submitById) || clickFirst(submitByText)) {
            StepLogger.step("Submitted rating");
            return;
        }
        if (clickFirst(skipByText)) {
            StepLogger.step("Submit not found — skipped rating");
        }
    }

    private void giveFiveStars() {
        WebElement bar = firstOf(driverRating);
        if (bar == null) {
            bar = firstOf(ratingBar);
        }
        if (bar != null) {
            Rectangle r = bar.getRect();
            // Tap near the far-right of the star row = the 5th star.
            int x = r.getX() + (int) (r.getWidth() * 0.92);
            int y = r.getY() + r.getHeight() / 2;
            tapAt(x, y);
        }
    }

    private WebElement firstOf(By locator) {
        List<WebElement> els = driver.findElements(locator);
        return els.isEmpty() ? null : els.get(0);
    }

    private boolean clickFirst(By locator) {
        List<WebElement> els = driver.findElements(locator);
        if (els.isEmpty()) {
            return false;
        }
        try {
            els.get(0).click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void tapAt(int x, int y) {
        // W3C single tap via a zero-length swipe.
        swipeBetween(x, y, x, y);
    }
}
