package com.scoot.automation.base;

import com.scoot.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;

/**
 * One live app session (app + device) in a multi-session test.
 *
 * Call {@link #activate()} before interacting with this app's page objects: it
 * points {@link DriverManager} at this session's driver and sets the active
 * {@link ConfigReader} context so resource-id lookups use this app's package and
 * adb helpers target this device. In a cross-app E2E you flip between two
 * sessions with {@code user.activate()} / {@code driver.activate()}.
 */
public final class AppSession {

    private final String appKey;   // "user" or "driver"
    private final String udid;
    private final AndroidDriver driver;

    public AppSession(String appKey, String udid, AndroidDriver driver) {
        this.appKey = appKey;
        this.udid = udid;
        this.driver = driver;
    }

    /** Make this the session all page objects and adb helpers act on. */
    public AppSession activate() {
        DriverManager.setDriver(driver);
        ConfigReader.setActiveContext(appKey, udid);
        return this;
    }

    public AndroidDriver driver() {
        return driver;
    }

    public String appKey() {
        return appKey;
    }

    public String udid() {
        return udid;
    }

    /** Quit the Appium session; ignores errors so teardown never masks a failure. */
    public void quitQuietly() {
        try {
            driver.quit();
        } catch (Exception ignored) {
            // best-effort cleanup
        }
    }
}
