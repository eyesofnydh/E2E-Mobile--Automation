package com.scoot.automation.base;

import io.appium.java_client.android.AndroidDriver;

/**
 * Holds the AndroidDriver per thread so tests can run in parallel safely.
 *
 * Appium note: AndroidDriver is the mobile counterpart of Selenium's
 * RemoteWebDriver. One driver == one live session talking to the Appium
 * server, which in turn drives UiAutomator2 on the device.
 */
public final class DriverManager {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static AndroidDriver getDriver() {
        return DRIVER.get();
    }

    public static void setDriver(AndroidDriver driver) {
        DRIVER.set(driver);
    }

    public static void removeDriver() {
        DRIVER.remove();
    }
}
