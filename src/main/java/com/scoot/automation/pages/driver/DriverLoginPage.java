package com.scoot.automation.pages.driver;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * DRIVER app — phone + password login (activity: goferhandyprovider.common.ui.SigninActivity).
 *
 * Structurally similar to the user login (shared ccp country picker, edt_phone,
 * edt_pwd, sigin_button) but with a different header title id.
 *
 * SAFETY: performs a real authentication — use only with the staging driver account.
 */
public class DriverLoginPage extends BasePage {

    private final By title         = resourceId("handy_header_tvTitle"); // "Signin"
    private final By countryCode   = resourceId("rlClickConsumer");
    private final By countrySearch = resourceId("editText_search");
    private final By phoneField    = resourceId("edt_phone");
    private final By passwordField = resourceId("edt_pwd");
    private final By loginButton   = resourceId("sigin_button");
    private final By forgotPassword = resourceId("forgot_password");

    public boolean isLoaded() {
        return isDisplayed(phoneField) && isDisplayed(loginButton);
    }

    public String getTitle() {
        return getText(title);
    }

    public DriverLoginPage selectCountry(String countryName) {
        tap(countryCode);
        type(countrySearch, countryName);
        By countryRow = uiAutomator(
                "new UiSelector().resourceId(\"" + appResourceId("textView_countryName")
                        + "\").textStartsWith(\"" + countryName + "\")");
        tap(countryRow);
        return this;
    }

    public DriverLoginPage enterPhone(String phone) {
        type(phoneField, phone);
        return this;
    }

    public DriverLoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    public void tapLogin() {
        tap(loginButton);
    }

    public void tapForgotPassword() {
        tap(forgotPassword);
    }

    /** SAFETY: real login — staging driver account only. */
    public void login(String country, String phone, String password) {
        selectCountry(country);
        enterPhone(phone);
        enterPassword(password);
        tapLogin();
    }
}
