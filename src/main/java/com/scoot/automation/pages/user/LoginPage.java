package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * USER app — phone + password login screen
 * (activity: com.trioangle.goferhandy.common.signinsignup.LoginActivityCommon).
 *
 * SAFETY: entering real credentials and tapping Login can trigger an OTP/SMS
 * and a real authentication. Only call {@link #tapLogin()} with a dedicated
 * staging/test account. The navigation test only verifies the form renders.
 */
public class LoginPage extends BasePage {

    private final By title           = resourceId("tv_title");       // "Sign in"
    private final By backArrow       = resourceId("iv_back_arrow");
    private final By countryCode     = resourceId("rlClickConsumer"); // country-code picker (+1)
    private final By phoneField      = resourceId("edt_phone");
    private final By passwordField   = resourceId("edt_pwd");
    private final By loginButton     = resourceId("sigin_button");    // disabled until input is valid
    private final By forgotPassword  = resourceId("forgot_password");

    // Country-picker dialog
    private final By countrySearch   = resourceId("editText_search");

    public boolean isLoaded() {
        return isDisplayed(phoneField) && isDisplayed(loginButton);
    }

    public String getTitle() {
        return getText(title);
    }

    /** The Login button is disabled until the form is filled — useful as an assertion. */
    public boolean isLoginButtonEnabled() {
        return waitForVisible(loginButton).isEnabled();
    }

    /**
     * Opens the country picker, searches for the country, and selects it.
     * The phone number 9747599745 is Indian, so the default +1 (US) must be
     * changed to India (+91) before entering the number.
     */
    public LoginPage selectCountry(String countryName) {
        tap(countryCode);
        type(countrySearch, countryName);
        // Tap the matching row by its country-name TextView (resource-id + text).
        By countryRow = uiAutomator(
                "new UiSelector().resourceId(\"" + appResourceId("textView_countryName")
                        + "\").textStartsWith(\"" + countryName + "\")");
        tap(countryRow);
        return this;
    }

    public LoginPage enterPhone(String phone) {
        type(phoneField, phone);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    /** SAFETY: only use with a staging/test account — this performs a real login. */
    public void tapLogin() {
        tap(loginButton);
    }

    /**
     * Full login flow: select country, enter phone + password, submit.
     * SAFETY: performs a real authentication — use only with the staging test account.
     */
    public void login(String country, String phone, String password) {
        selectCountry(country);
        enterPhone(phone);
        enterPassword(password);
        tapLogin();
    }

    public void tapForgotPassword() {
        tap(forgotPassword);
    }

    public void tapBack() {
        tap(backArrow);
    }

    public boolean isCountryCodePickerDisplayed() {
        return isDisplayed(countryCode);
    }
}
