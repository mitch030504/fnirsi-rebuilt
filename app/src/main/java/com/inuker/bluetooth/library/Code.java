package com.inuker.bluetooth.library;

/* JADX INFO: loaded from: classes.dex */
public class Code {
    public static final int BLE_NOT_SUPPORTED = -4;
    public static final int BLUETOOTH_DISABLED = -5;
    public static final int ILLEGAL_ARGUMENT = -3;
    public static final int REQUEST_CANCELED = -2;
    public static final int REQUEST_DENIED = -9;
    public static final int REQUEST_EXCEPTION = -10;
    public static final int REQUEST_FAILED = -1;
    public static final int REQUEST_OVERFLOW = -8;
    public static final int REQUEST_SUCCESS = 0;
    public static final int REQUEST_TIMEDOUT = -7;
    public static final int REQUEST_UNKNOWN = -11;
    public static final int SERVICE_UNREADY = -6;

    public static String toString(int i) {
        if (i == -9) {
            return "REQUEST_DENIED";
        }
        if (i == -7) {
            return "REQUEST_TIMEDOUT";
        }
        if (i == -6) {
            return "SERVICE_UNREADY";
        }
        if (i == -5) {
            return "BLUETOOTH_DISABLED";
        }
        if (i == -4) {
            return "BLE_NOT_SUPPORTED";
        }
        if (i == -3) {
            return "ILLEGAL_ARGUMENT";
        }
        if (i == -1) {
            return "REQUEST_FAILED";
        }
        if (i == 0) {
            return "REQUEST_SUCCESS";
        }
        return "unknown code: " + i;
    }
}
