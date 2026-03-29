package com.inuker.bluetooth.library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.inuker.bluetooth.library.utils.BluetoothLog;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothService extends Service {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        BluetoothLog.v(String.format("BluetoothService onCreate", new Object[0]));
        Context applicationContext = getApplicationContext();
        mContext = applicationContext;
        BluetoothContext.set(applicationContext);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        BluetoothLog.v(String.format("BluetoothService onBind", new Object[0]));
        return BluetoothServiceImpl.getInstance();
    }
}
