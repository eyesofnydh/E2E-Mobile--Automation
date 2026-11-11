l# Scoot — Manual Bug Sweep (User + Driver apps)

Exploratory manual test on **staging**, 2026-11-03/04. User app on real phone +
driver app on emulator, both logged in as the same staging account (Rijo Test /
Rijosh Vk, phone 9747599745). Covers functional, UI, copy/localization, and config
bugs across the main screens of both apps.

**Not captured by the automated Excel bug sheet** (that only logs automated TestNG
failures). This is the manual-exploration log. See also `E2E-FINDINGS.md` for the
live-ride end-to-end run.

## Severity summary

| # | Sev | App | Area | Bug |
|---|-----|-----|------|-----|
| B01 | High | Driver | Dispatch/UX | Accept & all trip actions are **swipe-only but look like tap buttons**; a tap does nothing → ride missed silently |
| B02 | High | User | Dispatch | **"No vehicles are currently available"** shown even when a driver was online and received the request |
| B03 | High | Both | Config | **Currency is CAD (Canadian $)** on an India/+91 app; fares, wallet, receipts all in `$`; CAD is the only option |
| B04 | Med | User | Support | Support phone shows wrong country code **`+93`** (Afghanistan); should be `+91` |
| B05 | Med | Both | Copy | Placeholder button label **"Continue123"** in the location-permission dialog |
| B06 | Med | Both | UX | Location-permission dialog **re-appears on every app launch** even after permission is granted (nag loop) |
| B07 | Med | User | Receipt | Job Details receipt: **pickup and destination are identical** (both "G.P.T Road, Chalakkudy"); actual pickup (Koratty/Infopark) is lost |
| B08 | Med | User | Receipt | Job Details: **Duration/Distance show placeholder "1.00 Mins / 1.00 KM"** instead of actual (~20 min / 9.86 km) |
| B09 | Med | User | History | Ride-history map thumbnails show **Perth, Australia** (not the real trip); map tiles dated "©2014" |
| B10 | Med | Both | Auth | **Single-device login** force-logs-out the other app on the same account ("logged in on another device") — can't be rider+driver on one number at once (client D-AUTH-03) |
| B11 | Low-Med | Both | Branding | App display name shows as **"Scoot webc" / "Scoot Driver webc"** in OS dialogs & notifications |
| B12 | Low | Both | Copy | Broken grammar in permission dialog ("…when the app is in user or not. This app always in use background location") |
| B13 | Low | User | Copy/UX | "Your Bookings" drawer item opens a screen titled **"Your Job"**; rider trips labeled "Job" (driver terminology) |
| B14 | Low | User | UI | **"hello"** lowercase above the user's name in the drawer header |
| B15 | Low | User | Copy | Privacy Policy effective date malformed: **"12/012024"** (missing slash) |
| B16 | Low | User | UX | **Promotions** opens a bare promo-code entry with **no offers listed** (aligns with client "promotions not working") |
| B17 | Low | User | UI | "Job ID18577" / "Service No" — missing space between label and number in places |
| B18 | Low | User | UX | Leftover **"Rate your ride"** prompt re-appears on app relaunch after the trip was already rated |

## Notes / detail

- **B01** is the highest-value finding: it very likely explains the client's "drivers
  can't accept rides" complaints. Every driver action (Accept / Confirm Arrived / Begin
  Trip / End Trip) is a left→right swipe bar; a tap is silently ignored.
- **B02** pairs with B01 — a missed/late accept surfaces to the rider as "no vehicles",
  hiding the real cause.
- **B03** (currency CAD) is pervasive: Settings → Currency = "$ CAD" (only option),
  wallet `$20500.29`, all fares `$`. For an India launch this should be `₹ INR`.
- **B07/B08** make the receipt effectively wrong for every completed trip.
- **B10** also made driver-app testing flaky: logging the rider in on the phone logged
  the driver out on the emulator mid-sweep.

## Coverage

**Covered** — User: splash, sign-in, login (country picker/validation), home/map,
destination search, fare/vehicle, drawer, profile, change-password (validation),
payment methods, promotions (validation), wallet + add-amount, settings
(currency/language/legal), privacy-policy webview, ride history + job-details receipt,
rating. Driver: login, home, online toggle, drawer, trip requests, full trip flow
(accept→arrive→begin→end→rate), forced-logout.

**Not yet covered** (recommend a follow-up pass; needs a second account to avoid the
B10 single-device logout): driver drawer sub-screens (Earnings, Manage Documents,
Manage Vehicles, Pay to Admin, Ratings, Referral, Support), user Edit-Profile form,
Emergency contacts, Referral, Manual booking, Language options, register/OTP, forgot
password, scheduled-ride full flow, multi-stop, cancellation/refund flows.
