package com.inuker.bluetooth.library.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.inuker.bluetooth.library.BluetoothContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothUtils {
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothManager mBluetoothManager;
    private static Handler mHandler;

    public static Context getContext() {
        return BluetoothContext.get();
    }

    private static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public static void post(Runnable runnable) {
        getHandler().post(runnable);
    }

    public static void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        registerGlobalReceiver(broadcastReceiver, intentFilter);
    }

    private static void registerGlobalReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        Context context = getContext();
        if (Build.VERSION.SDK_INT >= 33) {
            context.registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            return;
        }
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public static void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        unregisterGlobalReceiver(broadcastReceiver);
    }

    private static void unregisterGlobalReceiver(BroadcastReceiver broadcastReceiver) {
        getContext().unregisterReceiver(broadcastReceiver);
    }

    public static void sendBroadcast(Intent intent) {
        sendGlobalBroadcast(intent);
    }

    public static void sendBroadcast(String str) {
        sendGlobalBroadcast(new Intent(str));
    }

    private static void sendGlobalBroadcast(Intent intent) {
        Context context = getContext();
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    public static boolean isBleSupported() {
        return Build.VERSION.SDK_INT >= 18 && getContext() != null && getContext().getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }

    public static boolean isBluetoothEnabled() {
        return getBluetoothState() == 12;
    }

    public static int getBluetoothState() {
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.getState();
        }
        return 0;
    }

    public static boolean openBluetooth() {
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.enable();
        }
        return false;
    }

    public static boolean closeBluetooth() {
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.disable();
        }
        return false;
    }

    public static BluetoothManager getBluetoothManager() {
        if (!isBleSupported()) {
            return null;
        }
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getContext().getSystemService("bluetooth");
        }
        return mBluetoothManager;
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    public static BluetoothDevice getRemoteDevice(String str) {
        BluetoothAdapter bluetoothAdapter;
        if (TextUtils.isEmpty(str) || (bluetoothAdapter = getBluetoothAdapter()) == null) {
            return null;
        }
        return bluetoothAdapter.getRemoteDevice(str);
    }

    public static List<BluetoothDevice> getConnectedBluetoothLeDevices() {
        ArrayList arrayList = new ArrayList();
        BluetoothManager bluetoothManager = getBluetoothManager();
        if (bluetoothManager != null) {
            arrayList.addAll(bluetoothManager.getConnectedDevices(7));
        }
        return arrayList;
    }

    public static int getConnectStatus(String str) {
        BluetoothManager bluetoothManager = getBluetoothManager();
        if (bluetoothManager == null) {
            return -1;
        }
        try {
            return bluetoothManager.getConnectionState(getRemoteDevice(str), 7);
        } catch (Throwable th) {
            BluetoothLog.e(th);
            return -1;
        }
    }

    public static int getBondState(String str) {
        if (getBluetoothManager() == null) {
            return 10;
        }
        try {
            return getRemoteDevice(str).getBondState();
        } catch (Throwable th) {
            BluetoothLog.e(th);
            return 10;
        }
    }

    public static List<BluetoothDevice> getBondedBluetoothClassicDevices() {
        Set<BluetoothDevice> bondedDevices;
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        ArrayList arrayList = new ArrayList();
        if (bluetoothAdapter != null && (bondedDevices = bluetoothAdapter.getBondedDevices()) != null) {
            arrayList.addAll(bondedDevices);
        }
        return arrayList;
    }

    public static boolean isDeviceConnected(String str) {
        if (TextUtils.isEmpty(str) || !isBleSupported()) {
            return false;
        }
        return getBluetoothManager().getConnectionState(getBluetoothAdapter().getRemoteDevice(str), 7) == 2;
    }

    public static boolean checkMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean refreshGattCache(android.bluetooth.BluetoothGatt r5) {
        boolean z = false;
        if (r5 != null) {
            try {
                java.lang.reflect.Method method = android.bluetooth.BluetoothGatt.class.getMethod("refresh", new Class[0]);
                if (method != null) {
                    method.setAccessible(true);
                    z = ((Boolean) method.invoke(r5, new Object[0])).booleanValue();
                }
            } catch (Exception e) {
                BluetoothLog.e(e);
            }
        }
        BluetoothLog.v(String.format("refreshDeviceCache return %b", Boolean.valueOf(z)));
        return z;
    }
}
