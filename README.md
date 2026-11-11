# Scoot Mobile Automation

Appium + Java + TestNG + Page Object Model automation for the **Scoot** Android apps
(both the **User** app `com.scoottnc.user` and the **Driver** app `com.scoottncdriver.driver`).

Built for a QA engineer coming from Selenium — comments throughout flag where Appium differs from Selenium.

---

## 1. Prerequisites

| Tool | Version used here | Notes |
|------|-------------------|-------|
| Java (JDK) | 17+ (JDK 25 installed) | Project compiles to Java 17 |
| Maven | 3.9+ | `mvn -v` |
| Node.js | 18+ | Appium runs on Node |
| Appium | 2.x / 3.x | `appium -v` |
| UiAutomator2 driver | 8.x | `appium driver list --installed` |
| Android SDK platform-tools | adb on PATH | `adb devices` |
| A device/emulator | Android 12 (API 31) tested | Real device over USB with USB debugging ON |

### Set ANDROID_HOME (Windows, one time)
```powershell
setx ANDROID_HOME "C:\Users\wac\AppData\Local\Android\Sdk"
setx PATH "$($env:PATH);C:\Users\wac\AppData\Local\Android\Sdk\platform-tools"
```
Then reopen the terminal.

### Install Appium + driver (if missing)
```powershell
npm install -g appium
appium driver install uiautomator2
```

---

## 2. Configuration

All tunables live in [`src/main/resources/config.properties`](src/main/resources/config.properties) — **no config in code**.

Key settings:
- `targetApp` = `user` or `driver` (which app to test)
- `deviceName`, `platformVersion` — match your device (`adb devices`, `adb shell getprop ro.build.version.release`)
- `appium.server.url` — default `http://127.0.0.1:4723`
- timeouts, `noReset`, `autoGrantPermissions`

Every property can be overridden on the command line, e.g. `-DtargetApp=driver`.

---

## 3. Project structure

```
pom.xml                         Maven build + Surefire wired to testng.xml
testng.xml                      Smoke suite + TestListener registration
src/main/java/com/scoot/automation/
  base/
    BaseTest.java               Appium session setup/teardown (UiAutomator2Options)
    DriverManager.java          ThreadLocal<AndroidDriver> (parallel-safe)
  config/
    ConfigReader.java           Reads config.properties (+ -D overrides)
  pages/
    BasePage.java               Reusable helpers: waits, tap, type, typeViaKeystrokes, swipe, scroll, getText, isDisplayed
    SplashPage.java             First screen (both apps)
    user/
      SignInSignUpPage.java     Sign-in/sign-up home
      LoginPage.java            Phone + password login (incl. country picker)
      HomePage.java             Logged-in home / map / "Where to?"
      SetLocationPage.java      Destination search (place suggestions)
  utils/
    AdbHelper.java              clear app data, grant permissions, check install
    ExtentReportManager.java    ExtentReports HTML report
    StepLogger.java             One call logs a step to Log4j2 + Extent + Allure + bug sheet
    TestListener.java           Screenshot on failure + Extent + bug-sheet row
    BugSheetLogger.java         Appends failures to an Excel (.xlsx) bug sheet
src/main/resources/
  config.properties             All configuration
  log4j2.xml                    Logging config
src/test/java/com/scoot/automation/tests/
  SmokeTest.java                Launches the app, verifies first screen
```

---

## 4. Run

**Terminal 1 — start Appium (with the SDK path set):**
```powershell
$env:ANDROID_HOME = "C:\Users\wac\AppData\Local\Android\Sdk"
appium
```

**Terminal 2 — run a suite:**
```powershell
# Smoke — default suite. Launches ONE app (targetApp, default = user)
# and verifies it reaches its first screen. This is not a cross-app run.
mvn test

# Smoke against the driver app instead
mvn test -DtargetApp=driver

# Logged-out: sign-in home + login-form navigation
mvn test "-DsuiteXmlFile=testng-user.xml"

# Real login with the staging account (user.testPhone / user.testPassword)
mvn test "-DsuiteXmlFile=testng-login.xml"

# Logged-in: home + destination search + fare/vehicle selection (no booking)
mvn test "-DsuiteXmlFile=testng-home.xml"

# Logged-in: navigation drawer + ride history + profile/payment/wallet/settings
mvn test "-DsuiteXmlFile=testng-account.xml"

# DRIVER app — login (run from a logged-out state)
mvn test "-DsuiteXmlFile=testng-driver-login.xml" "-DtargetApp=driver"

# DRIVER app — home + online/offline switch (logged in)
mvn test "-DsuiteXmlFile=testng-driver.xml" "-DtargetApp=driver"
```
> In PowerShell, quote the `-D` argument: `"-DsuiteXmlFile=testng-home.xml"`.

### Implemented coverage (user app)

| Suite | Test | Verifies |
|-------|------|----------|
| testng.xml | SmokeTest | Selected app (`targetApp`, default user) launches to its first screen — run once per app |
| testng-user.xml | UserLoginNavigationTest | Sign-in home loads; "Continue with Phone" opens login form |
| testng-login.xml | UserLoginTest | Real login (India +91, phone + password) lands on home |
| testng-home.xml | UserHomeBookingEntryTest | Home/map loads; "Where to?" returns place suggestions |
| testng-home.xml | UserFareEstimateTest | Select destination → "Go" → vehicle options + fares (no booking) |
| testng-home.xml | UserScheduleRideTest | "Schedule a Ride" sheet: date/time picker; confirm pickup time |
| testng-account.xml | UserMenuHistoryTest | Drawer menu items present; ride history Past/Upcoming tabs |
| testng-account.xml | UserAccountTest | Profile, Payment (cards + wallet), Wallet, Settings screens |
| testng-driver-login.xml | DriverLoginTest | Driver real login → driver home (HandyMainActivity) |
| testng-driver.xml | DriverHomeTest | Driver home: details, map, online/offline switch |
| testng-driver.xml | DriverMenuTest | Driver drawer items; Trip Requests / Scheduled Rides screen |

> **Driver going online** needs GPS/location enabled (and dispatch readiness); a plain
> switch tap stays offline without it. Required before ride-accept E2E.

Driver page objects: `pages/driver/` — `DriverSignInSignUpPage`, `DriverLoginPage`,
`DriverHomePage`, `DriverMenuPage`, `DriverTripRequestsPage`.

See **[TEST_PLAN.md](TEST_PLAN.md)** for the full plan (all modules, cross-app E2E,
client-issue traceability) and automation status.

Screen → page object map (user app):

| Screen (activity) | Page object |
|-------------------|-------------|
| SplashActivity | `pages/SplashPage` |
| SigninSignupActivityCommon | `pages/user/SignInSignUpPage` |
| LoginActivityCommon | `pages/user/LoginPage` |
| GoferMainActivity (home/map) | `pages/user/HomePage` |
| EditCurrentLocationActivity (destination) | `pages/user/SetLocationPage` |
| activity_fare_estimation (fare/vehicle) | `pages/user/FareEstimatePage` |
| Schedule-a-Ride bottom sheet | `pages/user/ScheduleRidePage` |
| Navigation drawer | `pages/user/NavigationMenuPage` |
| YourTrips (ride history) | `pages/user/YourTripsPage` |
| ProfileActivity | `pages/user/ProfilePage` |
| PaymentPage | `pages/user/PaymentPage` |
| AddWalletActivity | `pages/user/WalletPage` |
| Setting | `pages/user/SettingsPage` |

> **Appium note — live-search fields:** destination search uses a `TextWatcher`.
> Appium's atomic `sendKeys` sets the text but may not trigger the search, so
> `BasePage.typeViaKeystrokes()` types via `adb input text` (real keystrokes).
> Regular fields (phone, password) use normal `sendKeys`.

> **SAFETY:** tests stop at the destination-search / fare-entry stage. No test
> selects a destination + taps "Go" + confirms a ride, so no real booking or
> payment is ever made.

---

## 5. Reports & artifacts

| Artifact | Location |
|----------|----------|
| ExtentReports HTML | `target/extent-report/Scoot-ExtentReport-<timestamp>.html` (open in a browser) |
| Allure raw results | `target/allure-results/` → render with `allure serve target/allure-results` |
| Failure screenshots | `target/screenshots/` |
| Excel bug sheet | `target/bug-sheet/Scoot-BugSheet.xlsx` (one row per failed test, with repro steps) |
| Logs | `logs/scoot-automation.log` |

> Allure CLI (optional): `npm install -g allure-commandline`, then `allure serve target/allure-results`.

---

## 6. Coding rules enforced

- **Locator priority:** accessibility id → resource-id → UiAutomator → XPath (last resort)
- **No `Thread.sleep`** — explicit waits only (`WebDriverWait` in `BasePage`)
- **No hardcoded coordinates** — swipes are percentage-based; scrolling uses `UiScrollable`
- **Config stays out of code** — everything in `config.properties`
- Comments mark Appium-vs-Selenium differences for learning

---

## 7. Adding new page objects with Appium Inspector

1. Start the Appium server (`appium`).
2. Open **Appium Inspector** (download from the Appium Inspector GitHub releases).
3. Use these capabilities (JSON) to attach to the running app:
   ```json
   {
     "platformName": "Android",
     "appium:automationName": "UiAutomator2",
     "appium:deviceName": "R38M30A6PRT",
     "appium:appPackage": "com.scoottnc.user",
     "appium:appActivity": "com.trioangle.goferhandy.common.SplashActivity",
     "appium:noReset": true
   }
   ```
   Server URL: `http://127.0.0.1:4723`
4. Tap **Start Session**, navigate the app, and click elements in the inspector to read their
   `content-desc` (accessibility id), `resource-id`, text, and XPath.
5. Create a new class under `pages/` extending `BasePage`, add locators in the preferred order,
   and expose action methods. Write the test under `tests/` extending `BaseTest`.

> Safety: do not complete real bookings or payments. Stop flows before final confirmation
> unless running against a dedicated staging/test account.
