package com.inuker.bluetooth.library.connect;

import android.os.HandlerThread;
import android.os.Looper;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import java.util.HashMap;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleConnectManager {
    private static final String TAG = BleConnectManager.class.getSimpleName();
    private static HashMap<String, IBleConnectMaster> mBleConnectMasters = new HashMap<>();
    private static HandlerThread mWorkerThread;

    private static Looper getWorkerLooper() {
        if (mWorkerThread == null) {
            HandlerThread handlerThread = new HandlerThread(TAG);
            mWorkerThread = handlerThread;
            handlerThread.start();
        }
        return mWorkerThread.getLooper();
    }

    private static IBleConnectMaster getBleConnectMaster(String str) {
        IBleConnectMaster iBleConnectMaster = mBleConnectMasters.get(str);
        if (iBleConnectMaster != null) {
            return iBleConnectMaster;
        }
        IBleConnectMaster iBleConnectMasterNewInstance = BleConnectMaster.newInstance(str, getWorkerLooper());
        mBleConnectMasters.put(str, iBleConnectMasterNewInstance);
        return iBleConnectMasterNewInstance;
    }

    public static void connect(String str, BleConnectOptions bleConnectOptions, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).connect(bleConnectOptions, bleGeneralResponse);
    }

    public static void disconnect(String str) {
        getBleConnectMaster(str).disconnect();
    }

    public static void read(String str, UUID uuid, UUID uuid2, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).read(uuid, uuid2, bleGeneralResponse);
    }

    public static void write(String str, UUID uuid, UUID uuid2, byte[] bArr, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).write(uuid, uuid2, bArr, bleGeneralResponse);
    }

    public static void writeNoRsp(String str, UUID uuid, UUID uuid2, byte[] bArr, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).writeNoRsp(uuid, uuid2, bArr, bleGeneralResponse);
    }

    public static void readDescriptor(String str, UUID uuid, UUID uuid2, UUID uuid3, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).readDescriptor(uuid, uuid2, uuid3, bleGeneralResponse);
    }

    public static void writeDescriptor(String str, UUID uuid, UUID uuid2, UUID uuid3, byte[] bArr, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).writeDescriptor(uuid, uuid2, uuid3, bArr, bleGeneralResponse);
    }

    public static void notify(String str, UUID uuid, UUID uuid2, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).notify(uuid, uuid2, bleGeneralResponse);
    }

    public static void unnotify(String str, UUID uuid, UUID uuid2, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).unnotify(uuid, uuid2, bleGeneralResponse);
    }

    public static void readRssi(String str, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).readRssi(bleGeneralResponse);
    }

    public static void indicate(String str, UUID uuid, UUID uuid2, BleGeneralResponse bleGeneralResponse) {
        getBleConnectMaster(str).indicate(uuid, uuid2, bleGeneralResponse);
    }

    public static void clearRequest(String str, int i) {
        getBleConnectMaster(str).clearRequest(i);
    }

    public static void refreshCache(String str) {
        getBleConnectMaster(str).refreshCache();
    }
}
