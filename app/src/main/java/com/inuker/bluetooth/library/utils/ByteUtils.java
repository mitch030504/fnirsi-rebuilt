package com.inuker.bluetooth.library.utils;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class ByteUtils {
    public static final int BYTE_MAX = 255;
    public static final byte[] EMPTY_BYTES = new byte[0];

    public static int ubyteToInt(byte b) {
        return b & BYTE_MAX;
    }

    public static byte[] getNonEmptyByte(byte[] bArr) {
        return bArr != null ? bArr : EMPTY_BYTES;
    }

    public static String byteToString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        if (!isEmpty(bArr)) {
            for (byte b : bArr) {
                sb.append(String.format("%02X", Byte.valueOf(b)));
            }
        }
        return sb.toString();
    }

    public static byte[] trimLast(byte[] bArr) {
        int length = bArr.length - 1;
        while (length >= 0 && bArr[length] == 0) {
            length--;
        }
        return Arrays.copyOfRange(bArr, 0, length + 1);
    }

    public static byte[] stringToBytes(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length + 1) / 2];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) Integer.parseInt(str.substring(i, Math.min(2, length - i) + i), 16);
        }
        return bArr;
    }

    public static boolean isEmpty(byte[] bArr) {
        return bArr == null || bArr.length == 0;
    }

    public static byte[] fromInt(int i) {
        byte[] bArr = new byte[4];
        for (int i2 = 0; i2 < 4; i2++) {
            bArr[i2] = (byte) (i >>> (i2 * 8));
        }
        return bArr;
    }

    public static boolean byteEquals(byte[] bArr, byte[] bArr2) {
        int length;
        if (bArr == null && bArr2 == null) {
            return true;
        }
        if (bArr == null || bArr2 == null || (length = bArr.length) != bArr2.length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (bArr[i] != bArr2[i]) {
                return false;
            }
        }
        return true;
    }

    public static byte[] fillBeforeBytes(byte[] bArr, int i, byte b) {
        int length = bArr != null ? bArr.length : 0;
        if (length >= i) {
            return bArr;
        }
        byte[] bArr2 = new byte[i];
        int i2 = i - 1;
        int i3 = length - 1;
        while (i2 >= 0) {
            if (i3 >= 0) {
                bArr2[i2] = bArr[i3];
            } else {
                bArr2[i2] = b;
            }
            i2--;
            i3--;
        }
        return bArr2;
    }

    public static byte[] cutBeforeBytes(byte[] bArr, byte b) {
        if (isEmpty(bArr)) {
            return bArr;
        }
        for (int i = 0; i < bArr.length; i++) {
            if (bArr[i] != b) {
                return Arrays.copyOfRange(bArr, i, bArr.length);
            }
        }
        return EMPTY_BYTES;
    }

    public static byte[] cutAfterBytes(byte[] bArr, byte b) {
        if (isEmpty(bArr)) {
            return bArr;
        }
        for (int length = bArr.length - 1; length >= 0; length--) {
            if (bArr[length] != b) {
                return Arrays.copyOfRange(bArr, 0, length + 1);
            }
        }
        return EMPTY_BYTES;
    }

    public static byte[] getBytes(byte[] bArr, int i, int i2) {
        byte[] bArr2 = null;
        if (bArr == null) {
            return null;
        }
        if (i >= 0 && i < bArr.length && i2 >= 0 && i2 < bArr.length) {
            if (i > i2) {
                return null;
            }
            bArr2 = new byte[(i2 - i) + 1];
            for (int i3 = i; i3 <= i2; i3++) {
                bArr2[i3 - i] = bArr[i3];
            }
        }
        return bArr2;
    }

    public static boolean isAllFF(byte[] bArr) {
        int length = bArr != null ? bArr.length : 0;
        for (int i = 0; i < length; i++) {
            if (ubyteToInt(bArr[i]) != 255) {
                return false;
            }
        }
        return true;
    }

    public static byte[] fromLong(long j) {
        byte[] bArr = new byte[8];
        for (int i = 0; i < 8; i++) {
            bArr[i] = (byte) (j >>> (i * 8));
        }
        return bArr;
    }

    public static void copy(byte[] bArr, byte[] bArr2, int i, int i2) {
        if (bArr == null || bArr2 == null || i < 0) {
            return;
        }
        while (i2 < bArr2.length && i < bArr.length) {
            bArr[i] = bArr2[i2];
            i++;
            i2++;
        }
    }
}
