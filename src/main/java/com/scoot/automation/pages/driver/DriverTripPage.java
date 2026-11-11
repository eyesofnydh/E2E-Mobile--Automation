package com.scoot.automation.pages.driver;

import com.scoot.automation.pages.BasePage;
import com.scoot.automation.utils.StepLogger;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * DRIVER app — in-progress trip screen
 * (activity: com.trioangle.goferhandyprovider.gofer.views.GoferRequestAcceptActivity).
 *
 * After accepting, the driver advances the ride by SWIPING one bottom bar whose
 * label cycles: "CONFIRM YOU'VE ARRIVED" → "BEGIN TRIP" → "END TRIP". Like Accept,
 * these are swipe bars, not tap buttons (E2E-BUG-01). A Google-Maps navigation
 * consent dialog ("GOT IT") can appear over the bar and is dismissed first.
 */
public class DriverTripPage extends BasePage {

    private static final String ACTIVITY = "GoferRequestAcceptActivity";
    private final By swipeBar   = resourceId("relativeLayout_swipeBtn_contentContainer");
    private final By buttonText = resourceId("tv_btnText");
    private final By gotItButton =
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"GOT IT\")");

    public boolean isShowing() {
        try {
            return currentActivity().contains(ACTIVITY);
        } catch (Exception e) {
            return false;
        }
    }

    /** Current bar label, e.g. "CONFIRM YOU'VE ARRIVED" / "BEGIN TRIP" / "END TRIP". */
    public String currentActionLabel() {
        List<WebElement> t = driver.findElements(buttonText);
        return t.isEmpty() ? "" : t.get(0).getText();
    }

    public void dismissMapsDialogIfPresent() {
        List<WebElement> got = driver.findElements(gotItButton);
        if (!got.isEmpty()) {
            StepLogger.step("Dismissing Google Maps navigation dialog (GOT IT)");
            got.get(0).click();
            sleep(800);
        }
    }

    /**
     * Swipes the current action bar once. Returns the label it showed before the
     * swipe (so the caller can assert the sequence).
     */
    public String swipeToAdvance() {
        dismissMapsDialogIfPresent();
        String label = currentActionLabel();
        swipeBarOnce();
        sleep(1200);
        dismissMapsDialogIfPresent();
        return label;
    }

    private void swipeBarOnce() {
        List<WebElement> bars = driver.findElements(swipeBar);
        int startX, endX, y;
        if (!bars.isEmpty()) {
            Rectangle r = bars.get(0).getRect();
            y = r.getY() + r.getHeight() / 2;
            startX = r.getX() + (int) (r.getWidth() * 0.12);
            endX = r.getX() + (int) (r.getWidth() * 0.9);
        } else {
            var size = driver.manage().window().getSize();
            y = (int) (size.height * 0.94);
            startX = (int) (size.width * 0.14);
            endX = (int) (size.width * 0.9);
        }
        swipeBetween(startX, y, endX, y);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
