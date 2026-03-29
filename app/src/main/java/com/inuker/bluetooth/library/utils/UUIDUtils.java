package com.inuker.bluetooth.library.utils;

import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class UUIDUtils {
    public static final String UUID_FORMAT = "0000%04x-0000-1000-8000-00805f9b34fb";

    public static UUID makeUUID(int i) {
        return UUID.fromString(String.format(UUID_FORMAT, Integer.valueOf(i)));
    }

    public static int getValue(UUID uuid) {
        return (int) (uuid.getMostSignificantBits() >>> 32);
    }
}
