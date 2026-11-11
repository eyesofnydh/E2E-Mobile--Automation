package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * USER app — Wallet screen (activity: common.views.payment.AddWalletActivity).
 * Shows the wallet balance and an "ADD AMOUNT" action.
 */
public class WalletPage extends BasePage {

    private final By title     = resourceId("tv_title");        // "Wallet"
    private final By balance   = resourceId("tv_caller_name");  // "$ 20500.29"
    private final By addAmount = resourceId("add_amount");      // "ADD AMOUNT"

    public boolean isLoaded() {
        return isDisplayed(title) && isDisplayed(balance);
    }

    public String getTitle() {
        return getText(title);
    }

    public String getBalance() {
        return getText(balance);
    }

    public boolean isAddAmountDisplayed() {
        return isDisplayed(addAmount);
    }

    public void tapAddAmount() {
        tap(addAmount);
    }
}
