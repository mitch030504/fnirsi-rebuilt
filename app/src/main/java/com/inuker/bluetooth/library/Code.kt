package com.inuker.bluetooth.library

object Code {
    const val BLE_NOT_SUPPORTED = -4
    const val BLUETOOTH_DISABLED = -5
    const val ILLEGAL_ARGUMENT = -3
    const val REQUEST_CANCELED = -2
    const val REQUEST_DENIED = -9
    const val REQUEST_EXCEPTION = -10
    const val REQUEST_FAILED = -1
    const val REQUEST_OVERFLOW = -8
    const val REQUEST_SUCCESS = 0
    const val REQUEST_TIMEDOUT = -7
    const val REQUEST_UNKNOWN = -11
    const val SERVICE_UNREADY = -6

    @JvmStatic
    fun toString(code: Int): String =
        when (code) {
            REQUEST_DENIED -> "REQUEST_DENIED"
            REQUEST_TIMEDOUT -> "REQUEST_TIMEDOUT"
            SERVICE_UNREADY -> "SERVICE_UNREADY"
            BLUETOOTH_DISABLED -> "BLUETOOTH_DISABLED"
            BLE_NOT_SUPPORTED -> "BLE_NOT_SUPPORTED"
            ILLEGAL_ARGUMENT -> "ILLEGAL_ARGUMENT"
            REQUEST_FAILED -> "REQUEST_FAILED"
            REQUEST_SUCCESS -> "REQUEST_SUCCESS"
            else -> "unknown code: $code"
        }
}
