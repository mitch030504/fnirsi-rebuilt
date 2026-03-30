# FNIRSI Rebuilt v1.1.0

## Disclosure

This release is vibe coded. I do not care, because it was made for my personal use first and I am sharing it because other people might still get good use out of it.

## Summary

This is the latest debug APK from the ongoing Java-to-Kotlin migration of the rebuilt FNIRSI Android app. The project still comes from APK reconstruction rather than original vendor source, but the current build is Android Studio-friendly, targets SDK 36, and is back to a working BLE/live-data state after the recent Kotlin porting passes.

## What Changed

- Migrated the full `com.uct` app layer to Kotlin.
- Migrated large parts of the bundled BLE stack and adapter layer from Java to Kotlin.
- Kept the project buildable in Android Studio with AGP `8.9.2`, Gradle `8.11.1`, and SDK `36`.
- Preserved modern Android BLE permission handling for Android 12+.
- Fixed rebuild/runtime issues hit during the port, including signed-byte parsing bugs and BLE callback regressions.
- Kept the current APK in a working state for scan, connect, and live measurement display after reverting a broken connection-flow experiment.
- Packaged the latest debug APK for install/testing.

## What Still Needs To Be Done

- Finish the remaining Java-to-Kotlin migration in the leftover library/scaffolding code.
- Do more device-side parity testing for protocol detection, model detection, charts, alarms, and capacity flows across more FNIRSI models.
- Clean up the reconstructed third-party code further so the project is easier to maintain long term.
- Add a proper release-signing setup instead of shipping debug-signed APKs.
- Add more regression coverage around the BLE/session layer so future refactors do not break working behavior.

## Notes

- The attached APK is debug-signed.
- This project was reconstructed from an APK, not the original vendor repository.
- Some devices may still fall back to the scanned BLE name if the original one-shot model/device-info packet is not emitted during startup.
