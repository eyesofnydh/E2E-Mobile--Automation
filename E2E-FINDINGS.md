# E2E Test Findings — User↔Driver Live Ride (2026-11-03)

Manual end-to-end run on **staging**. Setup: user app on real phone `R38M30A6PRT`,
driver app on emulator `emulator-5554`, both co-located in Kerala (Koratty) via a
continuous mock-GPS feed. Ride booked Koratty → Chalakudy Railway Station on the test
card `…1111`. Full chain completed: **book → accept → arrive → begin trip → end trip →
both rate → home.**

These were found by **manual exploration**, so they are **not** in the automated Excel
bug sheet (`target/bug-sheet/Scoot-BugSheet.xlsx` only logs *automated* TestNG failures
via `TestListener`). Logged here instead; can be pushed to the client tracker on request.

| ID | Severity | Area | Title |
|----|----------|------|-------|
| E2E-BUG-01 | High | Driver / dispatch | "Accept" is swipe-only but looks like a tap button; a tap does nothing (silent) |
| E2E-BUG-02 | High | User / dispatch | "No vehicles available" shown even when a driver was online and received the request |
| E2E-BUG-03 | Medium | User / support | Support number has wrong country code `+93` (Afghanistan), should be `+91` (India) |
| E2E-BUG-04 | Medium | Both / UI copy | Placeholder button label "Continue123" in the location-permission dialog |
| E2E-BUG-05 | Low-Med | Both / branding | App display name shows as "Scoot webc" / "Scoot Driver webc" |
| E2E-BUG-06 | Low | Both / copy | Broken grammar in location-permission dialog text |
| E2E-BUG-07 | High | User / dispatch | Full-screen interstitial ad on the rider app at the accept moment freezes the rider on "Contacting a driver…" — driver accept/arrive/begin/end are never shown; rider jumps straight to the rating screen at trip end |

---

### E2E-BUG-01 — Accept is swipe-only but looks tappable (High)
**Where:** Driver app, incoming request screen (`GoferRequestReceiveActivity`).
**Steps:** 1) Driver online, receives a ride request. 2) Tap the yellow "Accept" bar.
**Expected:** Ride is accepted. **Actual:** Nothing happens; the request counts down and
expires. Only a left→right **swipe** across the bar accepts it.
**Impact:** Very likely the root of the client's "drivers can't accept rides" reports — a
driver who taps (natural for a button) silently misses the ride. Either make it accept on
tap, or add an obvious slider affordance ("→ Slide to accept").

### E2E-BUG-02 — "No vehicles available" when a driver was actually online (High)
**Where:** User app, after REQUEST.
**Steps:** 1) One driver online and in range. 2) Request a ride. 3) Driver doesn't accept
within the short window. **Actual:** User sees *"No vehicles are currently available —
PLEASE TRY AGAIN."* **Problem:** A vehicle *was* available and *did* receive the request;
the message is misleading and hides that the failure was a missed/late accept (see BUG-01).

### E2E-BUG-03 — Wrong support phone country code (Medium)
**Where:** User app, "No vehicles available" screen — button "CALL +93 2894076959".
**Problem:** `+93` is Afghanistan. A Kerala/India app should show `+91`. Users calling
support reach the wrong number / fail to connect.

### E2E-BUG-04 — "Continue123" placeholder label (Medium)
**Where:** Both apps, first-launch location-permission dialog, primary button.
**Actual:** Button reads **"Continue123"** (and in a later state "Settings"). Looks like
leftover debug text shipped to the build. Should read "Continue" / "Allow".

### E2E-BUG-05 — App display name "Scoot webc" (Low-Medium)
**Where:** OS permission dialogs and push notifications ("Allow **Scoot webc**…",
"**Scoot Driver webc** • now"). The app's user-visible label carries a stray "webc"
suffix. Looks like a non-production app label.

### E2E-BUG-06 — Broken grammar in permission copy (Low)
**Where:** Location-permission dialog body.
**Actual:** "…Live tracking from nearby riders when the app is in user or not. This app
always in use background location." **Should be:** "…whether the app is in use or not.
This app always uses your location in the background."

---

## What worked (regression baseline)
Booking, live dispatch to a nearby online driver, swipe-accept, arrive, begin trip, end
trip with fare settlement on the test card, and two-way 5★ rating all functioned. Driver
going **online** requires a continuous live GPS fix (a single/last-known fix leaves it
offline). See `TEST_PLAN.md` E2E-01 and `README`/memory for the exact recipe.

### E2E-BUG-07 — Interstitial ad freezes the rider on "Contacting a driver…" (High)
**Where:** User app, active-request screen (`GoferSendingRequestActivity`), at the moment the driver accepts.
**Setup:** 2026-11-06 re-test. USER app on Samsung S10 (`R38M30A6PRT`), DRIVER app on I2012
(`138635215600189`), both logged in on separate phones, co-located at Koratty via live GPS,
driver Online. Same route Koratty → Chalakudy, test card `…1111`.

**Repro (run 1 — FAILED):** 1) Rider requests a ride (Comfort). 2) Driver swipe-accepts; driver
app correctly advances to En Route. 3) **A full-screen AdMob interstitial (a Reddit ad) pops over
the rider app right at the accept moment.** 4) After dismissing the ad, the rider app is **stuck on
"Contacting Comfort near you…" with only a Cancel button** and stays there for the entire trip —
it never shows the assigned driver, En Route, Arrived, or In-Trip. 5) At driver END TRIP the rider
app **jumps straight to the "Rate your ride" screen**, having shown none of the trip.

**Repro (run 2 — PASSED):** Identical steps, but **no interstitial ad appeared**. The rider app
updated normally: "En Route" with the assigned vehicle (`000ag`), stayed synced through
arrive/begin/end, then the rating screen. Full E2E worked.

**Diagnosis:** The only difference between the broken run and the clean run was the interstitial
ad on the rider app at accept time. The ad (its own Activity in the foreground) appears to
interrupt/consume the real-time "ride accepted" push/socket update, so the rider UI never leaves
the "searching for a driver" state even though a driver accepted and completed the trip.

**Impact:** High. This reproduces the client's "no vehicles / rider can't see the driver" class of
complaint from the rider side **even when dispatch and the driver worked perfectly**. The rider
believes no driver was found (and may Cancel or re-request), while a driver is actually en route.

**Fix suggestions:** (a) Don't show interstitial ads during an active ride request/trip; (b) make
the ride-status listener resilient to the app being backgrounded/covered — re-sync ride state on
resume (poll the current ride on `onResume`) so a missed push is recovered; (c) at minimum, when
the app returns to foreground on the request screen, re-query ride status instead of trusting only
the live push.

**Also seen during this run:** the driver's in-trip Google Maps nav consent dialog and the rating
screens still carry the "Scoot Driver webc" app-name (E2E-BUG-05); the driver rating screen is
titled "Rate Your Job" (driver "Job" terminology leaking, cf. APP-BUGS B13).
