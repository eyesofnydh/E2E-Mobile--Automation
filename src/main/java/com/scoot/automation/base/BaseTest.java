package com.scoot.automation.base;

import com.scoot.automation.config.ConfigReader;
import com.scoot.automation.utils.StepLogger;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Creates and tears down the Appium session for every test method.
 *
 * Selenium -> Appium differences worth noting:
 *  - Instead of ChromeOptions/FirefoxOptions you build UiAutomator2Options.
 *  - Instead of pointing at a WebDriver binary you point at a running
 *    Appium server URL; the server owns the device connection.
 *  - There is no driver.get(url); the app is launched via appPackage/appActivity.
 */
public abstract class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws MalformedURLException {
        StepLogger.reset();
        String app = ConfigReader.targetApp();
        log.info("Starting Appium session for target app: {}", app);

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(ConfigReader.get("platformName", "Android"))
                .setAutomationName(ConfigReader.get("automationName", "UiAutomator2"))
                .setDeviceName(ConfigReader.get("deviceName"))
                .setPlatformVersion(ConfigReader.get("platformVersion"))
                .setAppPackage(ConfigReader.appPackage())
                .setAppActivity(ConfigReader.appActivity())
                .setNoReset(ConfigReader.getBoolean("noReset", true))
                .setFullReset(ConfigReader.getBoolean("fullReset", false))
                .setAutoGrantPermissions(ConfigReader.getBoolean("autoGrantPermissions", true))
                .setNewCommandTimeout(ConfigReader.getSeconds("timeout.newCommand", 120));

        // With more than one device connected (e.g. real phone for the user app +
        // an emulator for the driver app), deviceName alone is ambiguous. Pin the
        // exact device by its adb serial via `udid` when supplied, e.g.:
        //   mvn test "-DsuiteXmlFile=testng-driver.xml" -DtargetApp=driver -Dudid=emulator-5554
        String udid = ConfigReader.get("udid");
        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }

        // Cold-start the app each session so tests always begin on the first screen,
        // even with noReset=true (otherwise Appium resumes the last screen shown), and
        // terminate it when the session ends. Neither has a typed setter in
        // java-client 9.3.0, so set them as raw capabilities.
        if (ConfigReader.getBoolean("forceAppLaunch", true)) {
            options.setCapability("appium:forceAppLaunch", true);
        }
        if (ConfigReader.getBoolean("shouldTerminateApp", true)) {
            options.setCapability("appium:shouldTerminateApp", true);
        }

        // Only (re)install if an APK path is supplied; otherwise launch the installed build.
        String apkPath = ConfigReader.appPath();
        if (apkPath != null && !apkPath.isBlank()) {
            options.setApp(apkPath);
        }

        URL serverUrl = new URL(ConfigReader.get("appium.server.url", "http://127.0.0.1:4723"));
        AndroidDriver driver = new AndroidDriver(serverUrl, options);

        // Keep implicit wait at 0; BasePage uses explicit waits exclusively.
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("timeout.implicit", 0)));

        DriverManager.setDriver(driver);
        log.info("Session created. Current package: {}", driver.getCurrentPackage());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        AndroidDriver driver = DriverManager.getDriver();
        if (driver != null) {
            log.info("Quitting Appium session.");
            driver.quit();
            DriverManager.removeDriver();
        }
    }

    protected AndroidDriver driver() {
        return DriverManager.getDriver();
    }
}
