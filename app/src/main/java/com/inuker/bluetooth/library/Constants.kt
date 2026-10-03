package com.inuker.bluetooth.library

import java.util.UUID

object Constants {
    const val ACTION_CHARACTER_CHANGED = "action.character_changed"
    const val ACTION_CONNECT_STATUS_CHANGED = "action.connect_status_changed"
    const val BLE_NOT_SUPPORTED = -4
    const val BLUETOOTH_DISABLED = -5
    const val BOND_BONDED = 12
    const val BOND_BONDING = 11
    const val BOND_NONE = 10

    @JvmField
    val CLIENT_CHARACTERISTIC_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    const val CODE_CLEAR_REQUEST = 20
    const val CODE_CONNECT = 1
    const val CODE_DISCONNECT = 2
    const val CODE_INDICATE = 10
    const val CODE_NOTIFY = 6
    const val CODE_READ = 3
    const val CODE_READ_DESCRIPTOR = 13
    const val CODE_READ_RSSI = 8
    const val CODE_REFRESH_CACHE = 21
    const val CODE_SEARCH = 11
    const val CODE_STOP_SESARCH = 12
    const val CODE_UNNOTIFY = 7
    const val CODE_WRITE = 4
    const val CODE_WRITE_DESCRIPTOR = 14
    const val CODE_WRITE_NORSP = 5
    const val DEVICE_FOUND = 4
    const val EXTRA_BYTE_VALUE = "extra.byte.value"
    const val EXTRA_CHARACTER_UUID = "extra.character.uuid"
    const val EXTRA_CODE = "extra.code"
    const val EXTRA_DESCRIPTOR_UUID = "extra.descriptor.uuid"
    const val EXTRA_GATT_PROFILE = "extra.gatt.profile"
    const val EXTRA_MAC = "extra.mac"
    const val EXTRA_OPTIONS = "extra.options"
    const val EXTRA_REQUEST = "extra.request"
    const val EXTRA_RSSI = "extra.rssi"
    const val EXTRA_SEARCH_RESULT = "extra.search.result"
    const val EXTRA_SERVICE_UUID = "extra.service.uuid"
    const val EXTRA_STATE = "extra.state"
    const val EXTRA_STATUS = "extra.status"
    const val EXTRA_TYPE = "extra.type"
    const val EXTRA_VERSION = "extra.version"
    const val ILLEGAL_ARGUMENT = -3
    const val REQUEST_CANCELED = -2
    const val REQUEST_DENIED = -9
    const val REQUEST_EXCEPTION = -10
    const val REQUEST_FAILED = -1
    const val REQUEST_NOTIFY = 4
    const val REQUEST_OVERFLOW = -8
    const val REQUEST_READ = 1
    const val REQUEST_RSSI = 8
    const val REQUEST_SUCCESS = 0
    const val REQUEST_TIMEDOUT = -7
    const val REQUEST_WRITE = 2
    const val SEARCH_CANCEL = 3
    const val SEARCH_START = 1
    const val SEARCH_STOP = 2
    const val SEARCH_TYPE_BLE = 2
    const val SEARCH_TYPE_CLASSIC = 1
    const val SERVICE_UNREADY = -6
    const val STATE_OFF = 10
    const val STATE_ON = 12
    const val STATE_TURNING_OFF = 13
    const val STATE_TURNING_ON = 11
    const val STATUS_CONNECTED = 16
    const val STATUS_DEVICE_CONNECTED = 2
    const val STATUS_DEVICE_CONNECTING = 1
    const val STATUS_DEVICE_DISCONNECTED = 0
    const val STATUS_DEVICE_DISCONNECTING = 3
    const val STATUS_DEVICE_SERVICE_READY = 19
    const val STATUS_DISCONNECTED = 32
    const val STATUS_UNKNOWN = -1

    @JvmStatic
    fun getStatusText(status: Int): String =
        when (status) {
            STATUS_DEVICE_DISCONNECTED -> "Disconnected"
            STATUS_DEVICE_CONNECTING -> "Connecting"
            STATUS_DEVICE_CONNECTED -> "Connected"
            STATUS_DEVICE_DISCONNECTING -> "Disconnecting"
            STATUS_DEVICE_SERVICE_READY -> "Service Ready"
            else -> "Unknown $status"
        }
}
