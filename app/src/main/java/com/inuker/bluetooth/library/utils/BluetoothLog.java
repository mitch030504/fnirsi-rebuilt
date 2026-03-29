package com.inuker.bluetooth.library.utils;

import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothLog {
    private static final String LOG_TAG = "miio-bluetooth";

    public static void i(String str) {
        Log.i(LOG_TAG, str);
    }

    public static void e(String str) {
        Log.e(LOG_TAG, str);
    }

    public static void v(String str) {
        Log.v(LOG_TAG, str);
    }

    public static void d(String str) {
        Log.d(LOG_TAG, str);
    }

    public static void w(String str) {
        Log.w(LOG_TAG, str);
    }

    public static void e(Throwable th) {
        e(getThrowableString(th));
    }

    public static void w(Throwable th) {
        w(getThrowableString(th));
    }

    private static String getThrowableString(Throwable th) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        while (th != null) {
            th.printStackTrace(printWriter);
            th = th.getCause();
        }
        String string = stringWriter.toString();
        printWriter.close();
        return string;
    }
}
