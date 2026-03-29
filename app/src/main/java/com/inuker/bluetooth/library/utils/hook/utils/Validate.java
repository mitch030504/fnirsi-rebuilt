package com.inuker.bluetooth.library.utils.hook.utils;

/* JADX INFO: loaded from: classes.dex */
public class Validate {
    public static void isTrue(boolean z, String str, Object... objArr) {
        if (!z) {
            throw new IllegalArgumentException(String.format(str, objArr));
        }
    }
}
