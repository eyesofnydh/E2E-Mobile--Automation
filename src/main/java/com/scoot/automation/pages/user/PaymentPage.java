package com.scoot.automation.pages.user;

import com.scoot.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * USER app — Payment methods screen (activity: common.views.payment.PaymentPage).
 * Lists saved cards, "Add Debit/Credit Card", and the wallet balance.
 */
public class PaymentPage extends BasePage {

    private final By title        = resourceId("tv_title");       // "Payment"
    private final By methodsLabel = resourceId("paytext");        // "Payment Methods"
    private final By cardNames    = uiAutomator(
            "new UiSelector().resourceId(\"" + appResourceId("tv_paymentName") + "\")");
    private final By walletBalance = resourceId("tv_caller_name"); // "$20500.29"
    private final By useWallet     = resourceId("rb_user_wallet");

    public boolean isLoaded() {
        return isDisplayed(title) && isDisplayed(methodsLabel);
    }

    public String getTitle() {
        return getText(title);
    }

    public List<String> getCardNames() {
        return driver.findElements(cardNames).stream()
                .map(WebElement::getText).collect(Collectors.toList());
    }

    public int savedCardCount() {
        return driver.findElements(cardNames).size();
    }

    public String getWalletBalance() {
        return getText(walletBalance);
    }

    public boolean isUseWalletOptionShown() {
        return isDisplayed(useWallet);
    }
}
