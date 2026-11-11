package com.scoot.automation.pages.driver;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * DRIVER app — sign in / sign up home
 * (activity: goferhandyprovider.common.ui.SigninSignupHomeActivityCommon).
 */
public class DriverSignInSignUpPage extends BasePage {

    private final By welcomeText       = resourceId("tv_welcome");   // "Welcome Back"
    private final By continueWithPhone = resourceId("signin");       // "Continue with Phone Number"
    private final By register          = resourceId("signup");       // "Register"
    private final By lookingForUserApp = resourceId("tv_user_app");  // "Looking for user app?"

    public boolean isLoaded() {
        return isDisplayed(welcomeText) && isDisplayed(continueWithPhone);
    }

    public String getWelcomeText() {
        return getText(welcomeText);
    }

    public DriverLoginPage tapContinueWithPhone() {
        tap(continueWithPhone);
        return new DriverLoginPage();
    }

    public boolean isUserAppLinkShown() {
        return isDisplayed(lookingForUserApp);
    }
}
