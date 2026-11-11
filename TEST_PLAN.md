# Scoot — Test Plan

End-to-end test plan for the Scoot **User** app (`com.scoottnc.user`) and **Driver**
app (`com.scoottncdriver.driver`). Covers functional, cross-app, and non-functional
cases, with traceability to the client's issue/requirement tracker.

**Environment:** Staging. Device: Samsung SM-G973U, Android 12 (API 31).
**Test account (user):** +91 9747599745 (display name "Rijo Test"). Payment: test card `…1111`.
**Safety:** Booking is permitted on staging (test card only). Never perform a real-money
payment. Flag any real payment gateway.

**Legend — Status:** ✅ automated & passing · 🟡 page object ready, test pending · ⬜ planned (needs device exploration)
**Priority:** P1 critical · P2 high · P3 medium

---

## 1. User app — Authentication

| ID | Case | Priority | Status |
|----|------|----------|--------|
| U-AUTH-01 | App launches to first screen (splash) | P1 | ✅ |
| U-AUTH-02 | Sign-in home shows phone/register/Google/Apple options | P1 | ✅ |
| U-AUTH-03 | "Continue with Phone" opens login form | P1 | ✅ |
| U-AUTH-04 | Login button disabled until form is valid | P2 | ✅ |
| U-AUTH-05 | Valid login (India +91, phone + password) → home | P1 | ✅ |
| U-AUTH-06 | Invalid credentials → "Those credentials don't look right" dialog | P1 | 🟡 |
| U-AUTH-07 | Country-code picker: search + select (e.g. India) | P2 | ✅ |
| U-AUTH-08 | Forgot password flow | P2 | ⬜ |
| U-AUTH-09 | Register / sign-up flow (+ OTP verification) | P1 | ⬜ |
| U-AUTH-10 | Social login (Google / Apple) — presence only | P3 | 🟡 |
| U-AUTH-11 | Logout (from drawer) returns to sign-in | P2 | 🟡 |
| U-AUTH-12 | Language & currency selection on sign-in home | P3 | ⬜ |

## 2. User app — Home & Booking

| ID | Case | Priority | Status |
|----|------|----------|--------|
| U-HOME-01 | Logged-in home shows map + greeting + "Where to?" | P1 | ✅ |
| U-HOME-02 | Pickup defaults to current location | P2 | 🟡 |
| U-BOOK-01 | "Where to?" opens destination search | P1 | ✅ |
| U-BOOK-02 | Destination search returns place suggestions | P1 | ✅ |
| U-BOOK-03 | Select suggestion → destination set on home | P1 | ✅ |
| U-BOOK-04 | Home → Work / Add home / Add work shortcuts | P3 | ⬜ |
| U-BOOK-05 | "Set location on map" picker | P3 | ⬜ |
| U-BOOK-06 | +Add stops (multi-stop) | P3 | ⬜ |
| U-BOOK-07 | "Go" → fare estimate with vehicle options + fares | P1 | ✅ |
| U-BOOK-08 | Select a specific vehicle type (Scoot X / Comfort / XL) | P1 | 🟡 |
| U-BOOK-09 | Payment method shown; change payment method | P2 | 🟡 |
| U-BOOK-10 | Apply promo code (Admin promo issue — client) | P2 | ⬜ |
| U-BOOK-11 | REQUEST ride → "searching for driver" state | P1 | ⬜ |
| U-BOOK-12 | Cancel ride before driver accepts | P1 | ⬜ |
| U-BOOK-13 | Cancel ride after accept → refund applied | P1 | ⬜ |
| U-BOOK-14 | Driver assigned → live tracking on map | P1 | ⬜ |
| U-BOOK-15 | Call / chat driver | P3 | ⬜ |
| U-BOOK-16 | Trip in progress → ETA / duration updates | P1 | ⬜ |
| U-BOOK-17 | Trip complete → fare summary / receipt | P1 | ⬜ |
| U-BOOK-18 | Rate + tip driver | P2 | ⬜ |

## 3. User app — Scheduled rides

| ID | Case | Priority | Status |
|----|------|----------|--------|
| U-SCH-01 | Switch "Ride Now" → schedule sheet (date/time picker) | P1 | ✅ |
| U-SCH-01b | Confirm a pickup time (returns to home) | P1 | ✅ |
| U-SCH-02 | Schedule a ride end-to-end | P1 | ⬜ |
| U-SCH-03 | Schedule MULTIPLE rides (no restriction) — client req | P1 | ⬜ |
| U-SCH-04 | Edit scheduled pickup time → user notified | P2 | ⬜ |
| U-SCH-05 | Cancel scheduled ride | P1 | ⬜ |
| U-SCH-06 | No duplicate notification when a ride is scheduled | P2 | ⬜ |

## 4. User app — Account, history, notifications

| ID | Case | Priority | Status |
|----|------|----------|--------|
| U-MENU-01 | Open navigation drawer (hamburger) | P2 | ✅ |
| U-MENU-02 | Drawer shows all core items | P2 | ✅ |
| U-ACCT-01 | View profile (name, email, mobile, edit/change-pwd buttons) | P2 | ✅ |
| U-ACCT-02 | Edit profile / change password actions | P2 | 🟡 |
| U-HIST-01 | Ride history list + Past/Upcoming tabs | P2 | ✅ |
| U-WALL-01 | Payment methods (saved cards) + wallet balance | P2 | ✅ |
| U-WALL-02 | Wallet screen balance + Add Amount | P2 | ✅ |
| U-SET-01 | Settings: currency, language, favourites, legal links | P2 | ✅ |
| U-NOTE-01 | Notifications list | P3 | ⬜ |
| U-NOTE-02 | Driver-assigned notification (dynamic w/ name) — client req | P2 | ⬜ |
| U-NOTE-03 | No notification when scheduled ride cancelled by driver — client req | P2 | ⬜ |

## 5. Driver app

| ID | Case | Priority | Status |
|----|------|----------|--------|
| D-AUTH-01 | Driver app launches to first screen | P1 | ✅ |
| D-AUTH-02 | Driver login (phone + password) → driver home | P1 | ✅ |
| D-AUTH-03 | Single active device (login elsewhere logs out previous) — client req | P2 | ⬜ |
| D-HOME-01 | Driver home: details, map, online switch present | P1 | ✅ |
| D-ONL-01 | Online switch responds to tap | P1 | ✅ |
| D-ONL-02 | Actually go ONLINE (needs GPS/location + dispatch readiness) | P1 | ✅ (emulator: driver goes Online in Kochi/Kerala with a continuous `emu geo fix` feed; confirmed by GeoFire `driverStatus=Online`) |
| D-MENU-01 | Driver drawer shows all items | P2 | ✅ |
| D-REQ-00 | Trip Requests / Scheduled Rides screen loads (list or empty) | P1 | ✅ |
| D-REQ-01 | Receive incoming regular/immediate ride request | P1 | ⬜ (needs driver online + a live request) |
| D-REQ-02 | Receive scheduled ride request | P1 | ⬜ (needs a scheduled request) |
| D-REQ-03 | Accept ride (within ~5s window) | P1 | ⬜ |
| D-REQ-04 | Reject / ignore ride | P2 | ⬜ |
| D-REQ-05 | Accept MULTIPLE non-overlapping scheduled rides — client req | P1 | ⬜ |
| D-REQ-06 | Cancelled-by-user ride no longer acceptable — client req | P1 | ⬜ |
| D-TRIP-01 | Navigate to pickup (in-app, minimal re-routing) — client req | P2 | ⬜ |
| D-TRIP-02 | Cancel accepted ride before passenger onboard — client req | P1 | ⬜ |
| D-TRIP-03 | Cannot cancel after passenger onboarded — client req | P1 | ⬜ |
| D-TRIP-04 | Start trip → complete trip | P1 | ⬜ |
| D-EARN-01 | Earnings / trip history | P2 | ⬜ |
| D-NOTE-01 | Notifications received in sleep/background — client req | P1 | ⬜ |
| D-NOTE-02 | Notification sound/tone for scheduled rides — client req | P3 | ⬜ |

## 6. End-to-end (cross-app) scenarios

Require **two sessions** — ideally two devices (one user, one driver), or sequential
two-phase runs on one device. See "Automation architecture" below.

| ID | Case | Priority | Status |
|----|------|----------|--------|
| E2E-01 | User books → driver accepts → trip → complete → rate | P1 | ✅ (manual drive 2026-11-03: user on real phone + driver on emulator co-located in Kerala. Full chain ran: book Koratty→Chalakudy → driver accept (SWIPE) → arrive → begin trip → end trip (fare settled, test card) → both rated 5★ → both back to home) |
| E2E-02 | User books → user cancels before accept | P1 | ⬜ |
| E2E-03 | User books → driver accepts → user cancels → refund | P1 | ⬜ |
| E2E-04 | User schedules → driver accepts scheduled → runs at time | P1 | ⬜ |
| E2E-05 | Driver cancels before onboard → user notified correctly | P2 | ⬜ |

## 7. Non-functional (from client requirements)

| ID | Case | Priority | Status |
|----|------|----------|--------|
| NFR-01 | Map search does not increase API hits unnecessarily | P2 | ⬜ (needs API/log monitoring) |
| NFR-02 | Re-routing only when required (not every 3s) | P2 | ⬜ |
| NFR-03 | Background location tracking stops when no active ride | P2 | ⬜ |
| NFR-04 | User-friendly error ("Something went wrong") not technical codes | P3 | ⬜ |

## 8. Traceability — client issue tracker → test IDs

Source: client Google Sheet "Scoot - AMC" (tabs *Time Estimation*, *Issues*).

| Client item | Test IDs |
|-------------|----------|
| Scheduled rides: no restriction (user multiple / driver non-overlap) | U-SCH-03, D-REQ-05 |
| Cancelled-by-user ride still shown to drivers | D-REQ-06, E2E-02 |
| Double notification when a ride is scheduled | U-SCH-06 |
| Driver notification opens to a plain screen | D-NOTE-01, D-REQ-02 |
| Ride cancellation refund (staging → prod) | U-BOOK-13, E2E-03 |
| Driver cancel before/after onboarding | D-TRIP-02, D-TRIP-03 |
| Remove user notification on driver-cancelled scheduled ride | U-NOTE-03, E2E-05 |
| Driver-assigned notification (dynamic w/ name) | U-NOTE-02 |
| Notify user on pickup-time change | U-SCH-04 |
| Promotion option (Admin) not working | U-BOOK-10 |
| Sleep/background notifications (Android) | D-NOTE-01 |
| Single-device driver login | D-AUTH-03 |
| Stop unnecessary background tracking | NFR-03 |
| Error-message friendliness | NFR-04 |

## 9. Automation architecture notes

- **Single framework, both apps:** `targetApp=user|driver` capability switch.
- **Cross-app E2E:** UiAutomator2 drives one app per session on a device. For
  user↔driver scenarios, use **two devices/emulators** (preferred) with two Appium
  sessions, or a **two-phase sequential** run on one device (user books and quits;
  driver session accepts the still-pending request). Timing-sensitive — keep the
  driver online and the request fresh.
- **Live-search fields** use `BasePage.typeViaKeystrokes` (adb input) not sendKeys.
- **Determinism:** `forceAppLaunch` cold-starts each session; login persists via `noReset`.

## 10. Current automation summary

- ✅ ~23 cases automated and passing. USER: auth/login, home, destination search,
  fare/vehicle, scheduled-ride sheet, drawer menu, ride history, profile, payment,
  wallet, settings. DRIVER: login, home, online switch, drawer menu, Trip Requests.
- 🟡 several cases have page objects ready, tests pending (vehicle select, edit profile,
  logout/sign-out, social-login presence).
- ⬜ remainder planned, pending (a) driver credentials, (b) 2nd device/emulator for
  cross-app E2E, (c) device-based screen exploration (promotions, referral, support,
  register/OTP, forgot password, add card/wallet, booking completion).
- Suites: `testng.xml`, `testng-user.xml`, `testng-login.xml`, `testng-home.xml`, `testng-account.xml`.
- 15 page objects (SplashPage + 14 user pages).
