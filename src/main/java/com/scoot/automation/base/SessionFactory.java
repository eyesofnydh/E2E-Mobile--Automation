package com.scoot.automation.base;

import com.scoot.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Builds an Appium {@link AndroidDriver} for a specific app on a specific device.
 *
 * Extracted from {@link BaseTest} so a single test can spin up more than one
 * session at once — e.g. the cross-app ride E2E, which runs the user app on one
 * phone and the driver app on another simultaneously. Each call is fully
 * parameterised (appKey + udid + platformVersion) and does NOT depend on the
 * global -DtargetApp / -Dudid context.
 */
public final class SessionFactory {

    private static final Logger log = LogManager.getLogger(SessionFactory.class);

    private SessionFactory() {
    }

    /**
     * @param appKey          "user" or "driver" (selects appPackage/appActivity config)
     * @param udid            adb serial of the target device (may be null for a single device)
     * @param platformVersion Android version of that device (may be null)
     */
    public static AndroidDriver createSession(String appKey, String udid, String platformVersion)
            throws MalformedURLException {

        String appPackage  = ConfigReader.get(appKey + ".appPackage");
        String appActivity = ConfigReader.get(appKey + ".appActivity");
        String apkPath     = ConfigReader.get(appKey + ".app", "");

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(ConfigReader.get("platformName", "Android"))
                .setAutomationName(ConfigReader.get("automationName", "UiAutomator2"))
                .setDeviceName(udid != null && !udid.isBlank() ? udid : ConfigReader.get("deviceName"))
                .setAppPackage(appPackage)
                .setAppActivity(appActivity)
                .setNoReset(ConfigReader.getBoolean("noReset", true))
                .setFullReset(ConfigReader.getBoolean("fullReset", false))
                .setAutoGrantPermissions(ConfigReader.getBoolean("autoGrantPermissions", true))
                .setNewCommandTimeout(ConfigReader.getSeconds("timeout.newCommand", 120));

        if (platformVersion != null && !platformVersion.isBlank()) {
            options.setPlatformVersion(platformVersion);
        }
        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }
        if (ConfigReader.getBoolean("forceAppLaunch", true)) {
            options.setCapability("appium:forceAppLaunch", true);
        }
        if (ConfigReader.getBoolean("shouldTerminateApp", true)) {
            options.setCapability("appium:shouldTerminateApp", true);
        }
        if (apkPath != null && !apkPath.isBlank()) {
            options.setApp(apkPath);
        }

        URL serverUrl = new URL(ConfigReader.get("appium.server.url", "http://127.0.0.1:4723"));
        AndroidDriver driver = new AndroidDriver(serverUrl, options);
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("timeout.implicit", 0)));

        log.info("Session created for app='{}' udid='{}' -> package {}",
                appKey, udid, driver.getCurrentPackage());
        return driver;
    }
}
