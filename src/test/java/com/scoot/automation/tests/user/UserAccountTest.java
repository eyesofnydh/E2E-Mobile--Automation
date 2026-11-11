package com.scoot.automation.tests.user;

import com.scoot.automation.base.BaseTest;
import com.scoot.automation.pages.SplashPage;
import com.scoot.automation.pages.user.*;
import com.scoot.automation.utils.StepLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * USER app — account area screens reached from the drawer: Profile, Payment,
 * Wallet, Settings. Read-only verification (no sign-out, no edits).
 * Requires the staging account to be signed in.
 */
public class UserAccountTest extends BaseTest {

    private NavigationMenuPage openMenu() {
        new SplashPage().waitUntilLoaded();
        HomePage home = new HomePage();
        Assert.assertTrue(home.isLoaded(), "Home screen did not load");
        return home.openMenu();
    }

    @Test(description = "My Profile shows user details with Edit/Change Password")
    public void profileShowsUserDetails() {
        StepLogger.step("Open My Profile from the drawer");
        ProfilePage profile = openMenu().openProfile();
        Assert.assertTrue(profile.isLoaded(), "Profile did not load");
        Assert.assertEquals(profile.getTitle(), "My Profile", "Unexpected profile title");
        StepLogger.step("Verify Edit Profile and Change Password are present");
        Assert.assertTrue(profile.hasEditAndChangePassword(), "Edit/Change Password not shown");
    }

    @Test(description = "Payment screen lists saved cards and wallet balance")
    public void paymentShowsCardsAndWallet() {
        StepLogger.step("Open Payment from the drawer");
        PaymentPage payment = openMenu().openPayment();
        Assert.assertTrue(payment.isLoaded(), "Payment screen did not load");
        StepLogger.step("Saved cards: " + payment.getCardNames() + " | Wallet: " + payment.getWalletBalance());
        Assert.assertTrue(payment.savedCardCount() >= 1, "No saved cards listed");
        Assert.assertFalse(payment.getWalletBalance().isBlank(), "Wallet balance not shown");
    }

    @Test(description = "Wallet screen shows balance and Add Amount")
    public void walletShowsBalance() {
        StepLogger.step("Open Wallet from the drawer");
        WalletPage wallet = openMenu().openWallet();
        Assert.assertTrue(wallet.isLoaded(), "Wallet did not load");
        StepLogger.step("Wallet balance: " + wallet.getBalance());
        Assert.assertTrue(wallet.isAddAmountDisplayed(), "Add Amount not shown");
    }

    @Test(description = "Settings shows currency, language, favourites and legal links")
    public void settingsShowsOptions() {
        StepLogger.step("Open Settings from the drawer");
        SettingsPage settings = openMenu().openSettings();
        Assert.assertTrue(settings.isLoaded(), "Settings did not load");
        StepLogger.step("Currency: " + settings.getCurrency() + " | Language: " + settings.getLanguage());
        Assert.assertTrue(settings.hasFavourites(), "Favourites (home/work) not shown");
        Assert.assertTrue(settings.hasLegalLinks(), "Privacy/Terms not shown");
    }
}
