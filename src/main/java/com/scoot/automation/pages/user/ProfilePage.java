package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * USER app — My Profile screen (activity: common.views.ProfileActivity).
 * Shows first/last name, email, mobile, with Edit Profile / Change Password.
 */
public class ProfilePage extends BasePage {

    private final By title           = resourceId("tv_title");       // "My Profile"
    private final By editProfile     = resourceId("btneditprofile");
    private final By changePassword  = resourceId("btnchangepassword");

    public boolean isLoaded() {
        return isDisplayed(title) && isDisplayed(editProfile);
    }

    public String getTitle() {
        return getText(title);
    }

    public boolean hasEditAndChangePassword() {
        return isDisplayed(editProfile) && isDisplayed(changePassword);
    }

    public void tapEditProfile()    { tap(editProfile); }
    public void tapChangePassword() { tap(changePassword); }
}
