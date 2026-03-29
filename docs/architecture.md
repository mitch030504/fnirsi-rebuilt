# Architecture

## Overview

The app is a single Android application module centered around BLE communication with FNIRSI USB meter/tester devices.

The rebuilt codebase keeps the original functional structure but modernizes it in these areas:

- Kotlin-first app layer under `com.uct`
- Android 12+ BLE permission handling
- Android Studio / Gradle wrapper project structure
- safer byte parsing and protocol helpers

## Main Components

### Entry / Scan

- `MainActivity.kt`
- `Main1Activity.kt`

Responsibilities:

- request BLE/location permissions as required by Android version
- scan for nearby BLE devices
- display discovered devices
- open the detail screen with selected device address and name

### Detail / Live Session

- `BleDetailActivity.kt`

Responsibilities:

- establish BLE connection
- enable notifications
- send session/control commands
- parse inbound frames
- update charts and measurement views
- expose device utilities such as alarms and capacity-group actions

### Protocol Layer

- `FnirsiProtocol.kt`
- `CmdUtli.kt`
- `CrcTool.kt`
- `TextTools.kt`
- `Global.kt`

Responsibilities:

- build outbound frames
- validate inbound CRC
- split multi-frame notification payloads
- convert little-endian integer fields
- map protocol and model identifiers to labels

### UI Support

- `BleDetailChartSupport.kt`
- `BleDetailDialogs.kt`
- adapters under `com/uct/adapter`

Responsibilities:

- chart rendering
- dialog creation and validation
- list/adaptor support for scan and group screens

## BLE Runtime Flow

1. Scan activity discovers a device and opens detail activity with `MAC` and `NAME`.
2. Detail activity connects with `BluetoothClient`.
3. Notify is enabled on `FFE0/FFE4`.
4. Session init command `0x81` is sent.
5. Device streams frames over BLE notifications.
6. Frames are split and dispatched by command ID.
7. UI updates on the main thread through the activity.

## Important State

- cached GATT profiles are stored in `Global.bleGattProfileMap`
- chart data is held in lists of `CurrentVoltageEntity`
- capacity group data is represented by `RljsEntity`
- session flags in `BleDetailActivity.kt` control start/stop and handshake behavior

## Reconstructed Areas

The original project was not available. The following were reconstructed during the rebuild:

- Gradle project metadata
- Android manifest updates for current SDKs
- broken/decompiler-damaged methods
- app-layer Java to Kotlin migration
- compatibility fixes for newer Android BLE behavior

## Remaining Risk Areas

- vendor protocol edge cases not exercised by available hardware
- one-shot device-info behavior on some devices
- decompiled third-party library code still present in parts of the tree
