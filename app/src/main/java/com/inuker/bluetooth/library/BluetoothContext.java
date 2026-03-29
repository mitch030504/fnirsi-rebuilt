package com.inuker.bluetooth.library;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothContext {
    private static Context mContext;
    private static Handler mHandler;

    public static void set(Context context) {
        mContext = context;
    }

    public static Context get() {
        return mContext;
    }

    public static void post(Runnable runnable) {
        postDelayed(runnable, 0L);
    }

    public static void postDelayed(Runnable runnable, long j) {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(runnable, j);
    }
}
