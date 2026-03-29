package com.inuker.bluetooth.library.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class MD5Utils {
    public static byte[] MD5_12(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes(), 0, str.length());
            byte[] bArrDigest = messageDigest.digest();
            int length = bArrDigest.length;
            if (length >= 12) {
                int i = length / 2;
                return Arrays.copyOfRange(bArrDigest, i - 6, i + 6);
            }
            return ByteUtils.EMPTY_BYTES;
        } catch (NoSuchAlgorithmException unused) {
            return null;
        }
    }
}
