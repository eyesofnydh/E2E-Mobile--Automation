package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * USER app — Settings screen (activity: common.views.Setting).
 * Profile summary, favourites (home/work), currency, language,
 * privacy policy, terms, sign out, delete account.
 */
public class SettingsPage extends BasePage {

    private final By title         = resourceId("tv_title");       // "Settings"
    private final By currency      = resourceId("currency_code");  // "$ CAD"
    private final By language      = resourceId("tv_language");    // "English"
    private final By homeFavourite = resourceId("home_lay");
    private final By workFavourite = resourceId("rlt_work");
    private final By privacyPolicy = resourceId("privacy_policy");
    private final By terms         = resourceId("terms_condition");
    private final By signout       = resourceId("llt_logout");
    private final By deleteAccount = resourceId("llt_del_account");

    public boolean isLoaded() {
        return isDisplayed(title) && isDisplayed(language);
    }

    public String getTitle() {
        return getText(title);
    }

    public String getCurrency() {
        return getText(currency);
    }

    public String getLanguage() {
        return getText(language);
    }

    public boolean hasFavourites() {
        return isDisplayed(homeFavourite) && isDisplayed(workFavourite);
    }

    public boolean hasLegalLinks() {
        return isDisplayed(privacyPolicy) && isDisplayed(terms);
    }

    public boolean isDeleteAccountShown() {
        return isDisplayed(deleteAccount);
    }

    /** Signs out from Settings — returns to the sign-in home. */
    public SignInSignUpPage signOut() {
        tap(signout);
        return new SignInSignUpPage();
    }
}
