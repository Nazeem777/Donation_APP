# DonorPath

An Android donation app, built with Kotlin and Gradle (Kotlin DSL).

> **Status:** project scaffold only. The Gradle build configuration is in place, but the
> `app` module (actual source code, layouts, and logic) has not been pushed to this
> repository yet. This README documents what's currently set up and what to add next.

---

## Stack

- **Language:** Kotlin
- **Build system:** Gradle with Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`)
- **Android:** AndroidX enabled, non-transitive R classes
- **Backend:** Firebase — the root build declares the
  [Google Services](https://developers.google.com/android/guides/google-services-plugin)
  plugin (`com.google.gms.google-services`), so this project is set up to connect to a
  Firebase project (e.g. Authentication, Firestore, Storage) once `google-services.json`
  is added
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

---

## Getting started

### Requirements

- [Android Studio](https://developer.android.com/studio) (latest stable)
- JDK 17+ (bundled with recent Android Studio versions)
- A Firebase project, if/when the `app` module is added with Firebase dependencies

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
- [ ] Define app dependencies (Firebase SDKs, Jetpack libraries, etc.) in
      `app/build.gradle.kts`
- [ ] Commit the manifest, Kotlin source, and resource files
- [ ] Expand this README with actual features once the app module lands (screens, core
      donation flow, auth, data model, etc.)

---

## License

Coursework / personal project, shared for educational and portfolio purposes.
