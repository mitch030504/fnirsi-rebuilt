# FNIRSI Rebuilt v1.0.0

## Disclosure

This release is vibe coded. I do not care, because it was made for my personal use first and I am sharing it because other people might still get good use out of it.

## Summary

Initial published reconstruction of the legacy FNIRSI Bluetooth Android application as a modern Android Studio project.

## Included In This Release

- Android Studio / Gradle wrapper project
- Kotlin-first rebuilt `com.uct` application layer
- Android SDK 36 compatibility updates
- Android 12+ BLE permission handling
- restored BLE scan, connect, and live data flow
- restored charting, protocol display, alarms, and capacity-related screens
- packaged debug APK for installation/testing

## Notes

- The attached APK is debug-signed
- This project was reconstructed from an APK, not the original vendor repository
- Some devices may still rely on fallback naming if the device-info model packet is not emitted during session startup
