package com.scoot.automation.pages;

import com.scoot.automation.base.DriverManager;
import com.scoot.automation.config.ConfigReader;
import com.scoot.automation.utils.AdbHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Reusable interaction helpers shared by every page object.
 *
 * Locator strategy preference (best -> last resort):
 *   1. accessibility id   -> AppiumBy.accessibilityId("...")
 *   2. resource-id        -> AppiumBy.id("...")  (full id or short id)
 *   3. UiAutomator        -> AppiumBy.androidUIAutomator("new UiSelector()...")
 *   4. XPath              -> By.xpath("...")      (slow & brittle; avoid)
 */
public abstract class BasePage {

    protected static final Logger log = LogManager.getLogger(BasePage.class);

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, ConfigReader.getSeconds("timeout.explicit", 20));
    }

    // ---- Locator convenience factories ----------------------------------

    protected By accessibilityId(String id) {
        return AppiumBy.accessibilityId(id);
    }

    /**
     * Accepts a bare id ("tv_login") or a fully-qualified one ("pkg:id/tv_login").
     * A bare id is prefixed with the app-under-test package, because the
     * UiAutomator2 driver matches resource-id literally and does not reliably
     * prepend the package itself.
     */
    protected By resourceId(String id) {
        if (id.contains(":id/") || id.contains(":")) {
            return AppiumBy.id(id);
        }
        return AppiumBy.id(ConfigReader.appPackage() + ":id/" + id);
    }

    protected By uiAutomator(String uiSelector) {
        return AppiumBy.androidUIAutomator(uiSelector);
    }

    /** Fully-qualified resource-id string (pkg:id/short) for use inside UiAutomator selectors. */
    protected String appResourceId(String shortId) {
        return ConfigReader.appPackage() + ":id/" + shortId;
    }

    // ---- Explicit waits (no Thread.sleep anywhere in this framework) -----

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected boolean waitForInvisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ---- Actions ---------------------------------------------------------

    protected void tap(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement el = waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /**
     * Types by emulating real keystrokes via `adb input text`. Use this for
     * fields with a live TextWatcher search (e.g. place/destination search),
     * where Appium's atomic sendKeys sets the text but can fail to trigger the
     * watcher. Spaces are encoded as %s for `adb input text`.
     */
    protected void typeViaKeystrokes(By locator, String text) {
        WebElement el = waitForVisible(locator);
        el.click();   // focus the field so adb input goes to it
        el.clear();
        AdbHelper.run("adb", "shell", "input", "text", text.replace(" ", "%s"));
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    public boolean isDisplayed(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** True if at least one element matches, without failing when none do. */
    protected boolean isPresent(By locator) {
        List<WebElement> els = driver.findElements(locator);
        return !els.isEmpty();
    }

    // ---- Scrolling / swiping (W3C Actions; no hardcoded coordinates) -----

    /**
     * Scroll a scrollable container until an element described by UiAutomator
     * text becomes visible. This is the idiomatic Appium way to reach
     * off-screen elements and needs no raw coordinates.
     */
    protected WebElement scrollToText(String visibleText) {
        String selector = "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().textContains(\"" + visibleText + "\"))";
        return driver.findElement(AppiumBy.androidUIAutomator(selector));
    }

    /** Percentage-based swipe so it works on any screen size (no fixed pixels). */
    protected void swipe(Direction direction) {
        var size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = size.height / 2;
        int endX = startX;
        int endY = startY;
        int dxEdge = (int) (size.width * 0.4);
        int dyEdge = (int) (size.height * 0.4);

        switch (direction) {
            case UP -> endY = startY - dyEdge;
            case DOWN -> endY = startY + dyEdge;
            case LEFT -> endX = startX - dxEdge;
            case RIGHT -> endX = startX + dxEdge;
        }

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600),
                PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * Low-level swipe between two points. Intended for callers that compute
     * coordinates from an element's own bounds (e.g. wheel pickers), not for
     * hardcoded screen positions.
     */
    protected void swipeBetween(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(500),
                PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    // ---- App state helpers ----------------------------------------------

    public String currentPackage() {
        return driver.getCurrentPackage();
    }

    public String currentActivity() {
        return driver.currentActivity();
    }
}
