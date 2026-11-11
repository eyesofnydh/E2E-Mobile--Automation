package com.scoot.automation.pages.driver;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * DRIVER app — incoming ride-request screen
 * (activity: com.trioangle.goferhandyprovider.gofer.views.GoferRequestReceiveActivity).
 *
 * IMPORTANT BUG (E2E-BUG-01): "Accept" is a left-to-right SWIPE bar, not a tap
 * button. A tap is silently ignored and the request times out. This page object
 * therefore swipes across the accept bar.
 */
public class DriverRequestReceivePage extends BasePage {

    private static final String ACTIVITY = "GoferRequestReceiveActivity";
    // The swipe widget shares this id across the request/trip screens.
    private final org.openqa.selenium.By swipeBar = resourceId("relativeLayout_swipeBtn_contentContainer");

    /** True while an incoming request is on screen. */
    public boolean isShowing() {
        try {
            return currentActivity().contains(ACTIVITY);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Swipes left→right across the accept bar to accept the ride. Retries a few
     * times (the window is only a few seconds) and returns true once the app
     * leaves the request screen (i.e. the accept registered).
     */
    public boolean swipeToAccept() {
        for (int attempt = 0; attempt < 4 && isShowing(); attempt++) {
            swipeAcceptBar();
            sleep(600);
        }
        return !isShowing();
    }

    private void swipeAcceptBar() {
        // Prefer the real widget bounds; fall back to a band near the bottom.
        List<WebElement> bars = driver.findElements(swipeBar);
        int startX, endX, y;
        if (!bars.isEmpty()) {
            Rectangle r = bars.get(0).getRect();
            y = r.getY() + r.getHeight() / 2;
            startX = r.getX() + (int) (r.getWidth() * 0.12);
            endX = r.getX() + (int) (r.getWidth() * 0.9);
        } else {
            var size = driver.manage().window().getSize();
            y = (int) (size.height * 0.91);
            startX = (int) (size.width * 0.16);
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
