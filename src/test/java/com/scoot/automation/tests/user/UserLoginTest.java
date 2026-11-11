package com.scoot.automation.tests.user;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.config.ConfigReader;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.user.LoginPage;
import com.scoot.automation.pages.user.SignInSignUpPage;
import com.scoot.automation.utils.StepLogger;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * USER app — real login with the staging test account (credentials come from
 * config.properties: user.country / user.testPhone / user.testPassword).
 *
 * This authenticates for real but makes NO booking and no payment. It verifies
 * that valid credentials move the app off the login screen.
 *
 * Run: mvn test -DsuiteXmlFile=testng-login.xml
 */
public class UserLoginTest extends BaseTest {

    @Test(description = "User logs in with valid staging credentials and leaves the login screen")
    public void userCanLoginWithValidCredentials() {
        new SplashPage().waitUntilLoaded();

        SignInSignUpPage home = new SignInSignUpPage();
        StepLogger.step("Tap 'Continue with Phone Number'");
        LoginPage login = home.tapContinueWithPhone();

        String country  = ConfigReader.get("user.countryName", "India");
        String phone     = ConfigReader.get("user.testPhone");
        String password  = ConfigReader.get("user.testPassword");
        Assert.assertNotNull(phone, "user.testPhone is not set in config.properties");

        StepLogger.step("Select country '" + country + "', enter phone " + phone + " and password, submit");
        login.login(country, phone, password);

        // Success = we navigate away from the login activity (no booking is made).
        StepLogger.step("Wait until the app leaves the login screen");
        WebDriverWait wait = new WebDriverWait(driver(),
                ConfigReader.getSeconds("timeout.explicit", 20).plusSeconds(15));
        wait.until(d -> !driver().currentActivity().contains("LoginActivityCommon"));

        String landed = driver().currentActivity();
        StepLogger.step("Logged in. Landed on activity: " + landed);
        Assert.assertFalse(landed.contains("LoginActivityCommon"),
                "Still on the login screen — login did not succeed.");
    }
}
