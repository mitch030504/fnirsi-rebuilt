# Build And Release

## Local Build

Debug:

```bash
./gradlew assembleDebug
```

Release:

```bash
./gradlew assembleRelease
```

## Android Studio

Open the repository root and build the `app` module with the included wrapper.

Requirements:

- JDK 17
- Android SDK Platform 36
- Build-Tools 36.x

## Signing

The repository does not include a production signing keystore.

For a real distributable release:

1. Create or import a keystore.
2. Add signing configuration to `app/build.gradle`.
3. Keep secrets out of the repository.
4. Build `assembleRelease`.

Until that is configured, the easiest installable artifact is the debug APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Release Workflow

Typical release steps:

1. Build `assembleDebug` or a properly signed `assembleRelease`.
2. Commit source changes.
3. Tag the release.
4. Create a GitHub release.
5. Upload the APK as a release asset.

Example with GitHub CLI:

```bash
gh release create v1.0.0 app/build/outputs/apk/debug/app-debug.apk --title "v1.0.0" --notes-file RELEASE_NOTES.md
```

## Recommended Future Improvements

- add production signing config through local environment variables
- produce a proper release-signed APK or AAB
- automate GitHub releases with CI once the runtime parity work is fully closed
