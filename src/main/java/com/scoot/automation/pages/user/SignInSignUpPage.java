package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * USER app — the Sign in / Sign up home screen
 * (activity: com.trioangle.goferhandy.common.signinsignup.SigninSignupActivityCommon).
 *
 * Locators use resource-id: this screen exposes clean ids but no content-desc,
 * so resource-id is the best available choice (accessibility id would win if present).
 * Bare ids are used (no package prefix) — the UiAutomator2 driver resolves them
 * against the app-under-test's package automatically.
 */
public class SignInSignUpPage extends BasePage {

    private final By welcomeText        = resourceId("tv_welcome");          // "Welcome Back"
    private final By continueWithPhone  = resourceId("tv_login");            // "Continue with Phone Number"
    private final By registerButton     = resourceId("tv_register");         // "Register"
    private final By googleLogin        = resourceId("rlt_google");
    private final By appleLogin         = resourceId("rlt_apple");

    public boolean isLoaded() {
        return isDisplayed(welcomeText) && isDisplayed(continueWithPhone);
    }

    public String getWelcomeText() {
        return getText(welcomeText);
    }

    /** Taps "Continue with Phone Number" and returns the phone/password login page. */
    public LoginPage tapContinueWithPhone() {
        tap(continueWithPhone);
        return new LoginPage();
    }

    public void tapRegister() {
        tap(registerButton);
    }

    public boolean isGoogleLoginDisplayed() {
        return isDisplayed(googleLogin);
    }

    public boolean isAppleLoginDisplayed() {
        return isDisplayed(appleLogin);
    }
}
