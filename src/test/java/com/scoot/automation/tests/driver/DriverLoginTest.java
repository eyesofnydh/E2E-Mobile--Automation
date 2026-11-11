package com.scoot.automation.tests.driver;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.config.ConfigReader;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.driver.DriverLoginPage;
import com.scoot.automation.pages.driver.DriverSignInSignUpPage;
import com.scoot.automation.utils.StepLogger;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DRIVER app — real login with the staging driver account
 * (driver.countryName / driver.testPhone / driver.testPassword).
 *
 * Run: mvn test -DsuiteXmlFile=testng-driver.xml -DtargetApp=driver
 *
 * SAFETY: authenticates for real; makes no trip actions.
 */
public class DriverLoginTest extends BaseTest {

    @Test(description = "Driver signs in with valid staging credentials and leaves the login screen")
    public void driverCanLogin() {
        new SplashPage().waitUntilLoaded();

        DriverSignInSignUpPage home = new DriverSignInSignUpPage();
        StepLogger.step("Verify driver sign-in home is loaded");
        Assert.assertTrue(home.isLoaded(), "Driver sign-in home did not load");

        StepLogger.step("Tap 'Continue with Phone Number'");
        DriverLoginPage login = home.tapContinueWithPhone();
        Assert.assertTrue(login.isLoaded(), "Driver login form did not load");

        String country  = ConfigReader.get("driver.countryName", "India");
        String phone     = ConfigReader.get("driver.testPhone");
        String password  = ConfigReader.get("driver.testPassword");
        Assert.assertNotNull(phone, "driver.testPhone is not set in config.properties");

        StepLogger.step("Log in as driver " + phone + " (" + country + ")");
        login.login(country, phone, password);

        StepLogger.step("Wait until the app leaves the driver login screen");
        WebDriverWait wait = new WebDriverWait(driver(),
                ConfigReader.getSeconds("timeout.explicit", 20).plusSeconds(15));
        wait.until(d -> !driver().currentActivity().contains("SigninActivity"));

        String landed = driver().currentActivity();
        StepLogger.step("Driver logged in. Landed on activity: " + landed);
        Assert.assertFalse(landed.contains("SigninActivity"),
                "Still on the driver login screen — login did not succeed.");
    }
}
