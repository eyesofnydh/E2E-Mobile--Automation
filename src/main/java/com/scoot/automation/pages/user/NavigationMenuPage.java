package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * USER app — navigation drawer opened from the home hamburger menu.
 * Items: My Profile, Your Bookings, Payment, Wallet, Promotions,
 * Emergency contacts, Referral, Manual booking, Settings, Support, Logout.
 */
public class NavigationMenuPage extends BasePage {

    private final By profile      = resourceId("nav_profile");
    private final By yourBookings = resourceId("nav_yourbookings");
    private final By payment      = resourceId("nav_payment");
    private final By wallet       = resourceId("nav_wallet");
    private final By promotions   = resourceId("nav_promo");
    private final By referral     = resourceId("nav_referal");
    private final By settings     = resourceId("nav_setting");
    private final By support      = resourceId("nav_support");
    private final By logout       = resourceId("tv_logout");

    public boolean isOpen() {
        return isDisplayed(profile) && isDisplayed(logout);
    }

    public boolean hasAllCoreItems() {
        return isDisplayed(profile) && isDisplayed(yourBookings) && isDisplayed(wallet)
                && isDisplayed(payment) && isDisplayed(settings) && isDisplayed(support);
    }

    public YourTripsPage openYourBookings() {
        tap(yourBookings);
        return new YourTripsPage();
    }

    public ProfilePage openProfile()   { tap(profile); return new ProfilePage(); }
    public WalletPage  openWallet()    { tap(wallet);  return new WalletPage(); }
    public PaymentPage openPayment()   { tap(payment); return new PaymentPage(); }
    public SettingsPage openSettings() { tap(settings); return new SettingsPage(); }
    public void openPromotions() { tap(promotions); }
    public void openReferral()   { tap(referral); }
    public void openSupport()    { tap(support); }

    /** Logs the user out — returns the app to the sign-in home. */
    public SignInSignUpPage logout() {
        tap(logout);
        return new SignInSignUpPage();
    }
}
