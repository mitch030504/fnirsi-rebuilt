# FNIRSI Rebuilt

## Disclosure

This project is vibe coded. I do not care, because it was made for my personal use first and I am sharing it because other people might still get good use out of it.

Reconstructed Android Studio project for the legacy FNIRSI Bluetooth Android app, migrated to a modern Kotlin-first codebase and updated for current Android SDK levels.

This repository was rebuilt from the original APK and then incrementally repaired until it built and ran again on modern Android. It is intended for maintenance, compatibility fixes, and protocol research around supported FNIRSI USB tester devices.

## Status

- Android Studio project opens and builds with the included Gradle wrapper
- App targets Android SDK 36
- BLE scan, connect, and live data paths are restored
- Main app-layer code under `com.uct` has been migrated to Kotlin
- Large parts of the bundled BLE stack have also been migrated to Kotlin
- Release asset in GitHub Releases is currently a debug-signed APK for easy installation/testing

## Supported Functionality

- BLE device scan and selection
- Connect/disconnect flow
- Live voltage/current/power display
- D+/D- voltage display
- Charge protocol detection
- Capacity/counter screens
- Chart rendering
- Alarm settings and resistance-related tools

## Known Limitations

- This project originates from a decompiled APK, not the original vendor source tree
- Some devices may not emit the original one-shot device-info frame consistently in the rebuilt app, so model detection can fall back to the scanned BLE device name
- The bundled third-party BLE/chart code was reconstructed from the APK where necessary
- The published APK is debug-signed unless you configure your own release keystore

## Remaining Work

- Finish the remaining Java-to-Kotlin migration in the leftover scaffolding/library code
- Do broader on-device parity testing across more FNIRSI models and edge cases
- Add regression coverage around the BLE/session layer before deeper refactors
- Replace debug-signing in releases with a proper release-signing setup if you plan to redistribute binaries

## Requirements

- Android Studio `Meerkat 2024.3.1 Patch 1` or newer
- JDK `17`
- Android SDK Platform `36`
- Android SDK Build-Tools `36.x`

## Open In Android Studio

1. Open the repository root in Android Studio.
2. Let Studio sync the project with the included Gradle wrapper.
3. Install any missing SDK components if prompted.
4. Let Studio generate `local.properties` for your local SDK path.
5. Build or run the `app` module.

## Command Line Build

Debug APK:

```bash
./gradlew assembleDebug
```

Release APK:

```bash
./gradlew assembleRelease
```

Debug build output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Project Layout

- `app/`: Android application module
- `app/src/main/java/com/uct/`: rebuilt application code
- `app/src/main/res/`: layouts, strings, drawables, raw assets
- `docs/architecture.md`: app structure and runtime flow
- `docs/protocol.md`: BLE UUIDs, frame format, command notes
- `docs/build-and-release.md`: build, signing, and release workflow
- `RELEASE_NOTES.md`: current release notes used for GitHub Releases

## Toolchain

- Android Gradle Plugin: `8.9.2`
- Gradle Wrapper: `8.11.1`
- Kotlin: `1.9.24`
- `compileSdk`: `36`
- `targetSdk`: `36`
- `minSdk`: `22`

## Development Notes

- `local.properties` should not be committed
- `app/build/` and top-level `build/` are generated artifacts
- If you want a distributable production APK, configure a real signing keystore before publishing

## Legal/Provenance Note

This repository is a reconstruction derived from a vendor APK for interoperability and maintenance purposes. Review local laws, redistribution rights, and any vendor licensing constraints before distributing modified binaries publicly.
