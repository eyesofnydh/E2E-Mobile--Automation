package com.scoot.automation.tests.user;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.user.LoginPage;
import com.scoot.automation.pages.user.SignInSignUpPage;
import com.scoot.automation.utils.StepLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * USER app login-navigation test. It drives the real page objects but
 * deliberately STOPS at the login form — it never submits credentials,
 * so no OTP/SMS is sent and no real login occurs.
 *
 * Run with the user app (default): mvn test -DsuiteXmlFile=testng-user.xml
 */
public class UserLoginNavigationTest extends BaseTest {

    @Test(description = "Sign-in home loads and shows the main login options")
    public void signInHomeShowsLoginOptions() {
        new SplashPage().waitUntilLoaded();

        SignInSignUpPage home = new SignInSignUpPage();
        StepLogger.step("Verify the sign-in/sign-up home screen is loaded");
        Assert.assertTrue(home.isLoaded(), "Sign-in home screen did not load");

        StepLogger.step("Verify the welcome message is shown");
        Assert.assertEquals(home.getWelcomeText(), "Welcome Back", "Unexpected welcome text");

        StepLogger.step("Verify Google and Apple login options are present");
        Assert.assertTrue(home.isGoogleLoginDisplayed(), "Google login not shown");
        Assert.assertTrue(home.isAppleLoginDisplayed(), "Apple login not shown");
    }

    @Test(description = "Tapping 'Continue with Phone Number' opens the login form")
    public void continueWithPhoneOpensLoginForm() {
        new SplashPage().waitUntilLoaded();

        SignInSignUpPage home = new SignInSignUpPage();
        StepLogger.step("Tap 'Continue with Phone Number'");
        LoginPage login = home.tapContinueWithPhone();

        StepLogger.step("Verify the login form (phone + password) is displayed");
        Assert.assertTrue(login.isLoaded(), "Login form did not load");

        StepLogger.step("Verify the screen title reads 'Sign in'");
        Assert.assertEquals(login.getTitle(), "Sign in", "Unexpected login screen title");

        StepLogger.step("Verify the country-code picker is present");
        Assert.assertTrue(login.isCountryCodePickerDisplayed(), "Country-code picker not shown");

        // No credentials are entered; the Login button starts disabled by design.
        StepLogger.step("Verify the Login button is disabled before any input");
        Assert.assertFalse(login.isLoginButtonEnabled(),
                "Login button should be disabled until the form is filled");
    }
}
