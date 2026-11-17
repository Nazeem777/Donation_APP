# DonorPath

**DonorPath** is an Android app that acts as a **mediator between donors and a catalog of
donation/charity organizations**. Instead of donating through each charity's own app or
website separately, users donate through DonorPath, which routes the contribution to the
chosen organization — and rewards the donor with points for giving, redeemable for perks
based on how much they've donated.

This is a fully working, buildable Android app — not just a mockup. It runs standalone
(no backend, Firebase project, or payment gateway account required) using a local
database, so it can be cloned and run immediately.

---

## Concept

Donors today have to go to each charity individually, each with its own app, its own
sign-up, its own payment flow, and usually nothing to show for giving beyond a receipt.

DonorPath sits in the middle:

1. **Aggregator** — one app lists multiple partner organizations and causes, so a donor
   can browse and choose where their money goes without leaving the app.
2. **Mediator** — a donation made in DonorPath is recorded against the chosen
   organization and kept in a transparent history, rather than donating directly on each
   charity's own platform.
3. **Rewarder** — every completed donation earns the donor reward points proportional to
   the amount given. Points unlock donor tiers (Bronze → Silver → Gold → Platinum) and can
   be redeemed for rewards in-app.

---

## Features (implemented)

- **Home** — browse partner organizations (name, cause category, description,
  verification badge) and see your current tier/points progress toward the next tier
- **Organization detail** — view a partner's profile and start a donation to them
- **Donate** — pick a suggested amount or enter a custom one, confirm the donation, and
  immediately see the points earned
- **Rewards** — view your points balance and tier, and redeem points for rewards from a
  catalog (vouchers, certificates, donor badges) once you can afford them
- **History** — a full log of every donation made: organization, amount, points earned,
  and timestamp
- **Profile** — view total donated and total points, and edit your display name

All data (organizations, donations, rewards, profile) is persisted locally on-device with
Room, so your history and points survive app restarts.

> **Note on payments:** the donation flow in this build is simulated — confirming a
> donation deducts nothing real and credits points immediately. A production version
> would swap the simulated step in `DonateViewModel.donate()` for a real payment gateway
> call (e.g. Stripe) before crediting the organization and the donor.

---

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3, single-activity app with Navigation Compose
  (bottom nav: Home · Rewards · History · Profile, plus pushed Organization Detail /
  Donate screens)
- **Persistence:** Room (SQLite) — `OrganizationEntity`, `DonationEntity`, `RewardEntity`,
  `UserProfileEntity`, each with a DAO exposing Kotlin `Flow`s
- **State/DI:** plain `ViewModel`s driven by `StateFlow`, wired to a small hand-rolled
  `AppContainer`/`SimpleViewModelFactory` (no Hilt/Dagger needed for this scope)
- **Build system:** Gradle 8.11.1 with Kotlin DSL and a version catalog
  (`gradle/libs.versions.toml`)
- **Android:** `compileSdk`/`targetSdk` 36, `minSdk` 26, AGP 8.7.3, Kotlin 2.0.21, KSP for
  Room's annotation processing

---

## Project structure

```
Donation_APP/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/donorpath/app/
│           ├── MainActivity.kt
│           ├── DonorPathApp.kt              # Application class, owns the AppContainer
│           ├── di/
│           │   ├── AppContainer.kt          # Wires Room DB -> repository singleton
│           │   └── SimpleViewModelFactory.kt
│           ├── data/
│           │   ├── local/                   # Room entities, DAOs, AppDatabase
│           │   └── repository/
│           │       ├── DonorPathRepository.kt  # Donation/reward business logic + seed data
│           │       └── DonorTier.kt            # Bronze/Silver/Gold/Platinum thresholds
│           └── ui/
│               ├── theme/                   # Compose Material3 theme
│               ├── navigation/              # Screen routes + NavHost + bottom bar
│               └── screens/
│                   ├── home/
│                   ├── orgdetail/
│                   ├── donate/
│                   ├── rewards/
│                   ├── history/
│                   └── profile/
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts            # Root build file
├── settings.gradle.kts
├── gradlew / gradlew.bat
└── README.md
```

---

## Getting started

### Requirements

- [Android Studio](https://developer.android.com/studio) (latest stable) or JDK 17+ and
  the Android SDK for command-line builds
- `compileSdk 36` / `build-tools 36.0.0` installed (Android Studio will prompt to install
  these on first sync if missing)

### Run it

```bash
git clone https://github.com/Nazeem777/Donation_APP.git
cd Donation_APP
```

Open the folder in Android Studio, let it sync, and press **Run**. No Firebase project,
API keys, or payment credentials are needed — everything runs against the local Room
database.

From the command line:

```bash
./gradlew assembleDebug
```

(On Windows: `gradlew.bat assembleDebug`) — the APK is written to
`app/build/outputs/apk/debug/app-debug.apk`.

### Using the app

1. **Home** shows the partner organizations — tap one to view its detail page.
2. Tap **Donate**, pick/enter an amount, and confirm — this records the donation and
   credits reward points.
3. Check **Rewards** to see your points/tier and redeem anything you can afford.
3. **History** lists every donation you've made; **Profile** shows your totals and lets
   you set a display name.

---

## Possible next steps

- Swap the simulated donation step for a real payment gateway (Stripe, PayPal, etc.)
- Replace the hardcoded seed organizations with a real partner directory (Firebase
  Firestore, or a REST API), including real payout routing per partner
- Add account sign-in (Firebase Authentication) so points/history sync across devices
- Add a redemption fulfillment flow (voucher codes, email delivery) instead of an
  in-app "Redeemed" flag
- Add automated tests for `DonorPathRepository` (donation accounting, tier thresholds,
  reward redemption)

---

## Course context

Built as a donation/mediator app demonstrating:

- MVVM architecture with `ViewModel` + `StateFlow` + Jetpack Compose
- Local persistence with Room (entities, DAOs, reactive queries via `Flow`)
- Multi-screen navigation with Navigation Compose and a bottom navigation bar
- A small domain model for a loyalty/rewards system (points, tiers, redemption)

## License

Coursework / personal project, shared for educational and portfolio purposes.
