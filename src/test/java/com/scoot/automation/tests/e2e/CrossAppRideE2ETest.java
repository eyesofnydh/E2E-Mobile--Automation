package com.scoot.automation.tests.e2e;

import com.scoot.automation.base.AppSession;
import com.scoot.automation.base.DriverManager;
import com.scoot.automation.base.SessionFactory;
import com.scoot.automation.config.ConfigReader;
import com.scoot.automation.pages.RatingPage;
import com.scoot.automation.pages.driver.DriverHomePage;
import com.scoot.automation.pages.driver.DriverLoginPage;
import com.scoot.automation.pages.driver.DriverRequestReceivePage;
import com.scoot.automation.pages.driver.DriverSignInSignUpPage;
import com.scoot.automation.pages.driver.DriverTripPage;
import com.scoot.automation.pages.user.FareEstimatePage;
import com.scoot.automation.pages.user.HomePage;
import com.scoot.automation.pages.user.LoginPage;
import com.scoot.automation.pages.user.SetLocationPage;
import com.scoot.automation.pages.user.SignInSignUpPage;
import com.scoot.automation.utils.AdbHelper;
import com.scoot.automation.utils.StepLogger;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

/**
 * FULL CROSS-APP RIDE E2E — user books, driver accepts, full trip, both rate.
 *
 * Runs TWO Appium sessions at once: the user app on one phone and the driver app
 * on another, flipping the active context between them (see {@link AppSession}).
 *
 * <p>SAFETY / OPT-IN: this test places a REAL ride request on staging and settles
 * it on the test card. It is therefore skipped unless you explicitly opt in with
 * {@code -De2e.book=true}. Required device wiring (both phones co-located so
 * dispatch matches, driver able to go Online with a live GPS fix):
 *
 * <pre>
 *   mvn test -DsuiteXmlFile=testng-e2e.xml -De2e.book=true \
 *     -De2e.user.udid=192.168.3.28:5555   -De2e.user.platformVersion=12 \
 *     -De2e.driver.udid=138635215600189   -De2e.driver.platformVersion=14
 * </pre>
 *
 * Credentials come from config(.local).properties: user.testPhone/testPassword
 * and driver.testPhone/testPassword.
 *
 * <p>KNOWN BUG this test guards against (E2E-BUG-07): if a full-screen interstitial
 * ad pops on the rider app at the accept moment, the rider stays frozen on
 * "Contacting a driver…" and never sees the trip. The test records whether the
 * rider ever reflected the accepted ride and asserts it at the end, so that bug
 * surfaces as a failure while the driver-side flow still runs to completion.
 */
public class CrossAppRideE2ETest {

    private static final Logger log = LogManager.getLogger(CrossAppRideE2ETest.class);

    private AppSession user;
    private AppSession driver;
    private boolean driverWasBroughtOnline = false;

    @BeforeClass(alwaysRun = true)
    public void createSessions() throws MalformedURLException {
        if (!ConfigReader.getBoolean("e2e.book", false)) {
            throw new SkipException(
                    "CrossAppRideE2ETest is opt-in: it places a real staging ride. "
                    + "Re-run with -De2e.book=true (and -De2e.user.udid / -De2e.driver.udid).");
        }
        String userUdid = required("e2e.user.udid");
        String drvUdid  = required("e2e.driver.udid");
        String userPv   = ConfigReader.get("e2e.user.platformVersion", ConfigReader.get("platformVersion"));
        String drvPv    = ConfigReader.get("e2e.driver.platformVersion", ConfigReader.get("platformVersion"));

        StepLogger.reset();
        AndroidDriver u = SessionFactory.createSession("user", userUdid, userPv);
        user = new AppSession("user", userUdid, u);
        AndroidDriver d = SessionFactory.createSession("driver", drvUdid, drvPv);
        driver = new AppSession("driver", drvUdid, d);
    }

    @Test(description = "User books -> driver swipe-accepts -> full trip -> both rate -> home")
    public void fullRideAcceptAndTrip() {
        String destQuery = ConfigReader.get("e2e.destination", "Chalakudy");
        String vehicle   = ConfigReader.get("e2e.vehicle", ""); // "" = keep default selection

        // ---------- DRIVER: log in, grant location, go Online ----------
        driver.activate();
        grantLocation(driver.udid(), ConfigReader.get("driver.appPackage"));
        driverLoginIfNeeded();
        DriverHomePage driverHome = new DriverHomePage();
        Assert.assertTrue(waitFor(() -> driverHome.isLoaded(), 20),
                "Driver home did not load.");

        StepLogger.step("Bring the driver Online");
        if (!driverHome.isStatusOnline()) {
            driverHome.toggleOnlineStatus();
            driverWasBroughtOnline = true;
        }
        Assert.assertTrue(waitFor(driverHome::isStatusOnline, 25),
                "Driver did not go Online. Needs a live GPS fix and both phones co-located "
                + "so dispatch can match (see memory: real phones can stay Offline indoors).");

        // ---------- USER: log in, set destination, request ----------
        user.activate();
        grantLocation(user.udid(), ConfigReader.get("user.appPackage"));
        userLoginIfNeeded();
        dismissUserPopups();
        HomePage home = new HomePage();
        Assert.assertTrue(waitFor(home::isLoaded, 20), "User home did not load.");

        StepLogger.step("Open destination search and search for '" + destQuery + "'");
        SetLocationPage setLocation = home.tapWhereTo();
        setLocation.searchDestination(destQuery);
        Assert.assertTrue(waitFor(setLocation::hasSuggestions, 15),
                "No place suggestions for '" + destQuery + "'.");
        setLocation.selectFirstSuggestion();

        StepLogger.step("Tap Go and open the fare screen");
        Assert.assertTrue(waitFor(home::isGoButtonDisplayed, 10), "Go button not shown.");
        FareEstimatePage fare = home.tapGo();
        Assert.assertTrue(waitFor(fare::isLoaded, 20), "Fare screen did not load.");
        Assert.assertTrue(fare.availableRideCount() >= 1, "No vehicle options were listed.");
        StepLogger.step("Fares: " + fare.getVehicleNames() + " = " + fare.getVehicleFares()
                + " | payment: " + fare.getPaymentType());
        if (!vehicle.isBlank()) {
            fare.selectVehicle(vehicle);
        }

        StepLogger.step("REQUEST the ride (" + fare.getRequestButtonText() + ")");
        fare.requestRide();

        // ---------- DRIVER: wait for request, swipe-accept ----------
        driver.activate();
        DriverRequestReceivePage request = new DriverRequestReceivePage();
        Assert.assertTrue(waitFor(request::isShowing, 25),
                "Driver never received the request — dispatch did not match (co-location/Online?).");
        StepLogger.step("Driver swipe-accepts the ride");
        Assert.assertTrue(request.swipeToAccept(),
                "Swipe-accept did not register (E2E-BUG-01: Accept is swipe-only).");
        DriverTripPage trip = new DriverTripPage();
        Assert.assertTrue(waitFor(trip::isShowing, 15),
                "Driver did not advance to the trip screen after accept.");

        // ---------- USER: did the rider ever see the accepted ride? (E2E-BUG-07) ----------
        user.activate();
        boolean riderSynced = waitForRiderToReflectAccept(25);

        // ---------- DRIVER: full trip via swipes ----------
        driver.activate();
        String arrived = trip.swipeToAdvance();
        StepLogger.step("Swiped: " + arrived);
        Assert.assertTrue(arrived.toUpperCase().contains("ARRIV"),
                "Expected 'Confirm you've arrived'; saw '" + arrived + "'.");
        String begin = trip.swipeToAdvance();
        StepLogger.step("Swiped: " + begin);
        Assert.assertTrue(begin.toUpperCase().contains("BEGIN"),
                "Expected 'Begin trip'; saw '" + begin + "'.");
        String end = trip.swipeToAdvance();
        StepLogger.step("Swiped: " + end);
        Assert.assertTrue(end.toUpperCase().contains("END"),
                "Expected 'End trip'; saw '" + end + "'.");

        // ---------- RATINGS: both apps ----------
        driver.activate();
        RatingPage driverRating = new RatingPage();
        Assert.assertTrue(waitFor(driverRating::isShowing, 15),
                "Driver rating screen did not appear after End Trip.");
        driverRating.rateFiveAndSubmit();
        Assert.assertTrue(waitFor(() -> new DriverHomePage().isLoaded(), 15),
                "Driver did not return home after rating.");

        user.activate();
        RatingPage userRating = new RatingPage();
        Assert.assertTrue(waitFor(userRating::isShowing, 15),
                "User rating screen did not appear after trip end.");
        userRating.rateFiveAndSubmit();
        Assert.assertTrue(waitFor(() -> new HomePage().isLoaded(), 15),
                "User did not return home after rating.");

        // ---------- The guarded assertion (E2E-BUG-07) ----------
        Assert.assertTrue(riderSynced,
                "E2E-BUG-07: the rider app never reflected the accepted ride — it stayed on "
                + "'Contacting a driver…' through the trip (an interstitial ad at the accept "
                + "moment likely swallowed the ride-status update). The backend trip completed, "
                + "but the rider UX is broken.");
    }

    // ---- helpers ---------------------------------------------------------

    /** Polls until the rider app leaves the 'sending request / contacting' state. */
    private boolean waitForRiderToReflectAccept(int seconds) {
        for (int i = 0; i < seconds; i++) {
            String act = safeActivity(user.driver());
            String pkg = safePackage(user.driver());
            // An interstitial ad brings a foreign package to the foreground — dismiss it.
            if (pkg != null && !pkg.contains("scoottnc")) {
                StepLogger.step("Foreign overlay on rider app (" + pkg + ") — dismissing");
                try { user.driver().navigate().back(); } catch (Exception ignored) { }
            } else if (act != null && act.contains("GoferRequestAcceptActivity")) {
                StepLogger.step("Rider now shows the assigned driver (En Route).");
                return true;
            }
            sleep(1000);
        }
        StepLogger.step("Rider never left 'Contacting a driver…' (E2E-BUG-07 reproduced).");
        return false;
    }

    private void driverLoginIfNeeded() {
        if (safeActivity(driver.driver()).toLowerCase().contains("signin")) {
            StepLogger.step("Driver logged out — signing in");
            DriverSignInSignUpPage signin = new DriverSignInSignUpPage();
            DriverLoginPage login = signin.tapContinueWithPhone();
            login.login(ConfigReader.get("driver.countryName", "India"),
                    required("driver.testPhone"), required("driver.testPassword"));
            sleep(4000);
        }
    }

    private void userLoginIfNeeded() {
        if (safeActivity(user.driver()).toLowerCase().contains("signin")) {
            StepLogger.step("User logged out — signing in");
            SignInSignUpPage signin = new SignInSignUpPage();
            LoginPage login = signin.tapContinueWithPhone();
            login.login(ConfigReader.get("user.countryName", "India"),
                    required("user.testPhone"), required("user.testPassword"));
            sleep(4000);
        }
    }

    /** Best-effort dismissal of the user app's location-permission nag. */
    private void dismissUserPopups() {
        AndroidDriver d = user.driver();
        for (String label : new String[]{"Not now", "Continue"}) {
            try {
                var els = d.findElements(io.appium.java_client.AppiumBy
                        .androidUIAutomator("new UiSelector().textContains(\"" + label + "\")"));
                if (!els.isEmpty()) {
                    els.get(0).click();
                    sleep(800);
                }
            } catch (Exception ignored) { }
        }
    }

    private void grantLocation(String udid, String pkg) {
        AdbHelper.run("adb", "-s", udid, "shell", "pm", "grant", pkg,
                "android.permission.ACCESS_FINE_LOCATION");
        AdbHelper.run("adb", "-s", udid, "shell", "pm", "grant", pkg,
                "android.permission.ACCESS_COARSE_LOCATION");
        AdbHelper.run("adb", "-s", udid, "shell", "settings", "put", "secure", "location_mode", "3");
    }

    @AfterClass(alwaysRun = true)
    public void closeSessions() {
        try {
            if (driver != null && driverWasBroughtOnline) {
                driver.activate();
                DriverHomePage dh = new DriverHomePage();
                if (dh.isLoaded() && dh.isStatusOnline()) {
                    StepLogger.step("Taking the driver back Offline");
                    dh.toggleOnlineStatus();
                }
            }
        } catch (Exception e) {
            log.warn("Could not set driver offline in teardown: {}", e.getMessage());
        }
        if (user != null) user.quitQuietly();
        if (driver != null) driver.quitQuietly();
        ConfigReader.clearActiveContext();
        DriverManager.removeDriver();
    }

    // ---- small utilities -------------------------------------------------

    private String required(String key) {
        String v = ConfigReader.get(key);
        if (v == null || v.isBlank()) {
            throw new SkipException("Required property '" + key + "' is not set "
                    + "(put it in config.local.properties or pass -D" + key + "=...).");
        }
        return v;
    }

    private boolean waitFor(java.util.function.BooleanSupplier cond, int seconds) {
        for (int i = 0; i < seconds; i++) {
            try {
                if (cond.getAsBoolean()) return true;
            } catch (Exception ignored) { }
            sleep(1000);
        }
        return false;
    }

    private String safeActivity(AndroidDriver d) {
        try { return d.currentActivity(); } catch (Exception e) { return ""; }
    }

    private String safePackage(AndroidDriver d) {
        try { return d.getCurrentPackage(); } catch (Exception e) { return ""; }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
