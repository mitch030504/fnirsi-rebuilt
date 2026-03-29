# BLE Protocol Notes

## GATT UUIDs

- Notify service: `0000FFE0-0000-1000-8000-00805F9B34FB`
- Notify characteristic: `0000FFE4-0000-1000-8000-00805F9B34FB`
- Write service: `0000FFE5-0000-1000-8000-00805F9B34FB`
- Write characteristic: `0000FFE9-0000-1000-8000-00805F9B34FB`

## Frame Format

Frames are structured as:

```text
AA | cmd | len | payload... | crc_low
```

Notes:

- Header is always `0xAA`
- `len` is the payload length in bytes
- CRC is based on CRC16/XMODEM, but only the low byte is appended and verified

## Endianness

- 2-byte fields are little-endian
- 4-byte fields are little-endian

Helpers involved:

- `byteToInt2`
- `bytesToInt`
- `intToByte2LH`

## Observed Commands

### Outbound

- `0x81`: session init/startup handshake
- `0x82`: start-related action
- `0x84`: stop/shutdown/unsubscribe-related action
- `0x85`: query/refresh current state
- `0x86`: start capacity-test request with payload
- `0x87`: stop capacity test
- `0x88`: select capacity-test group
- `0x89`: clear capacity-test group
- `0x8A`: query capacity-test groups

### Inbound

- `0x01`: acknowledgement-like response
- `0x02`: acknowledgement-like response
- `0x03`: device info
- `0x04`: live voltage/current/power values
- `0x05`: internal resistance and temperature
- `0x06`: D+/D- voltage and protocol identification
- `0x07`: chart sample stream
- `0x08`: capacity totals
- `0x09`: fast-charge state
- `0x0A`: capacity group entry

## Known Model Mapping

- `9 -> FNIRSI-C1`
- `38 -> FNB38`
- `48 -> FNB48`

If a device sends a different model ID, the current mapping will need to be extended.

## Known Protocol Labels

- `Unknown`
- `DCP 1.5A`
- `QC2.0 5V`
- `QC2.0 9V`
- `QC2.0 12V`
- `QC2.0 20V`
- `QC3.0`
- `APPLE 2.1A`
- `APPLE 2.4A`
- `SAMSUNG 2.OA`
- `USB2.0 FULL`
- `USB2.0 HIGH`
- `FCP AFC 9V`
- `FCP AFC 12V`
- `HUAWEI SCP`
- `PD MTK`

## Practical Note

The device may bundle multiple logical frames into a single BLE notification. `FnirsiProtocol.extractFrames()` handles this by scanning the notification payload for valid `0xAA`-framed packets and CRC-checking each candidate frame.
