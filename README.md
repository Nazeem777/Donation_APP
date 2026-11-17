# DonorPath

**DonorPath** is an Android app that acts as a **mediator between donors and major
donation/charity organizations**. Instead of donating through each charity's own app or
website separately, users donate through DonorPath, which routes the contribution to the
chosen organization — and rewards the donor for giving.

> **Status:** project scaffold only. The Gradle build configuration and Firebase wiring
> are in place, but the `app` module (source code, layouts, logic) has not been pushed to
> this repository yet. This README documents the app's concept and planned features as a
> blueprint for implementation.

---

## Concept

Donors today have to go to each charity individually — Red Cross, UNICEF, World Vision,
local food banks, disaster relief funds — each with its own app, its own sign-up, its own
payment flow, and usually nothing to show for giving beyond a receipt.

DonorPath solves this by sitting in the middle:

1. **Aggregator** — one app lists multiple well-known donation organizations and causes,
   so a donor can browse and choose where their money goes without leaving the app.
2. **Mediator** — DonorPath doesn't hold funds or compete with the charities; it forwards
   each donation to the selected organization (directly via their donation API/portal, or
   via a payment processor configured per partner) and keeps a transparent record of the
   transaction for the donor.
3. **Rewarder** — every completed donation earns the donor points/credits proportional to
   the amount given. Accumulated points can be redeemed for rewards, turning giving into
   something the donor gets tangible recognition for, which encourages repeat donations.

---

## Core features (planned)

### Donation
- Browse a catalog of partner charities/organizations, grouped by cause (disaster relief,
  health, education, environment, animal welfare, etc.)
- View each organization's profile: mission, verification status, and how funds are used
- Make a one-time or recurring donation to a chosen organization within the app
- Choose a donation amount or pick from suggested tiers
- Secure payment handling (card / e-wallet / bank transfer via a payment gateway)
- Digital receipt and donation history, itemized per organization

### Mediator layer
- Each partner organization is configured with its own payout destination, so funds
  routed through DonorPath land with the correct charity rather than a shared pool
- Donation status tracking (submitted → processed → confirmed by partner)
- Transparency log so a donor can see exactly which organization received which donation
  and when

### Rewards
- Points awarded per donation (e.g. a fixed rate per currency unit donated)
- Reward tiers / badges unlocked by cumulative giving (e.g. Bronze, Silver, Gold donor)
- A rewards catalog where points can be redeemed — discount vouchers, partner perks, or
  recognition items (certificates, profile badges)
- Leaderboard or personal donation stats to encourage continued giving
- Notifications when new rewards are unlocked or a redeemed reward is ready to use

### Account & trust
- User sign-up / login (Firebase Authentication)
- Donation and rewards history tied to the user's account (Firebase Firestore)
- Verified-organization badge so donors can trust which partners are legitimate
- Optional tax-receipt generation per donation, per organization's requirements

---

## Stack

- **Language:** Kotlin
- **Build system:** Gradle with Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`)
- **Android:** AndroidX enabled, non-transitive R classes
- **Backend:** Firebase — the root build declares the
  [Google Services](https://developers.google.com/android/guides/google-services-plugin)
  plugin (`com.google.gms.google-services`), intended to back:
  - **Firebase Authentication** — donor accounts
  - **Firestore** — partner organizations, donation records, reward balances
  - **Cloud Storage** — organization logos/media
- **Payments:** a payment gateway integration (e.g. Stripe) sits behind the donation flow
  to actually move funds to each partner organization
- **Root project name:** `DonorPath` (declared in `settings.gradle.kts`, module `:app`)

---

## Project structure

```
Donation_APP/
├── build.gradle.kts        # Root build file — declares the Android app and Google Services plugins
├── settings.gradle.kts     # Project name (DonorPath) and module list (:app)
├── gradle.properties       # JVM args, AndroidX flags
├── gradlew / gradlew.bat   # Gradle wrapper scripts
├── .gitignore
└── README.md
```

The `:app` module included in `settings.gradle.kts` is not present yet — this is where
the actual Android application (manifest, Kotlin source, resources, layouts) belongs.

### Suggested module layout, once added

```
app/
└── src/main/java/.../donorpath/
    ├── data/
    │   ├── model/           # Organization, Donation, Reward, User data classes
    │   └── repository/      # Firestore/Firebase and payment gateway access
    ├── ui/
    │   ├── organizations/   # Browse/select a charity to donate to
    │   ├── donate/          # Donation flow and payment
    │   ├── rewards/         # Points balance, tiers, redeemable rewards catalog
    │   ├── history/         # Past donations and receipts
    │   └── auth/            # Sign-up / login
    └── MainActivity.kt
```

---

## Getting started

### Requirements

- [Android Studio](https://developer.android.com/studio) (latest stable)
- JDK 17+ (bundled with recent Android Studio versions)
- A Firebase project (Authentication + Firestore enabled)
- Credentials for a payment gateway, once donation processing is implemented

### Setup

```bash
git clone https://github.com/Nazeem777/Donation_APP.git
cd Donation_APP
```

Open the folder in Android Studio and let it sync Gradle. Since the `app` module doesn't
exist yet, Studio will report a missing module — this is expected until the module is
created/added.

To build from the command line once the `app` module is present:

```bash
./gradlew build
```

(On Windows: `gradlew.bat build`)

---

## Next steps

- [ ] Add the `app` module (`File → New Module` in Android Studio, or commit the
      existing local module if one already exists)
- [ ] Add `app/google-services.json` from the Firebase console to enable the Google
      Services plugin
- [ ] Model partner organizations in Firestore (name, cause category, payout details,
      verification status)
- [ ] Implement the donation flow and wire it to a payment gateway per organization
- [ ] Implement the points/rewards system (earn rate, tiers, redemption catalog)
- [ ] Commit the manifest, Kotlin source, and resource files
- [ ] Expand this README with setup instructions for Firebase keys and payment gateway
      credentials once those integrations are added

---

## License

Coursework / personal project, shared for educational and portfolio purposes.
