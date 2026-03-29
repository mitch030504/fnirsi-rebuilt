package com.inuker.bluetooth.library.connect.request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.RuntimeChecker;
import com.inuker.bluetooth.library.connect.IBleConnectDispatcher;
import com.inuker.bluetooth.library.connect.IBleConnectWorker;
import com.inuker.bluetooth.library.connect.listener.GattResponseListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public abstract class BleRequest implements IBleConnectWorker, IBleRequest, Handler.Callback, GattResponseListener, RuntimeChecker {
    protected static final int MSG_REQUEST_TIMEOUT = 32;
    protected String mAddress;
    protected IBleConnectDispatcher mDispatcher;
    private boolean mFinished;
    protected boolean mRequestTimeout;
    protected BleGeneralResponse mResponse;
    private RuntimeChecker mRuntimeChecker;
    protected IBleConnectWorker mWorker;
    protected Bundle mExtra = new Bundle();
    protected Handler mHandler = new Handler(Looper.myLooper(), this);
    protected Handler mResponseHandler = new Handler(Looper.getMainLooper());

    protected long getTimeoutInMillis() {
        return 30000L;
    }

    public abstract void processRequest();

    public BleRequest(BleGeneralResponse bleGeneralResponse) {
        this.mResponse = bleGeneralResponse;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public void setAddress(String str) {
        this.mAddress = str;
    }

    public void setWorker(IBleConnectWorker iBleConnectWorker) {
        this.mWorker = iBleConnectWorker;
    }

    public void onResponse(final int i) {
        if (this.mFinished) {
            return;
        }
        this.mFinished = true;
        this.mResponseHandler.post(new Runnable() { // from class: com.inuker.bluetooth.library.connect.request.BleRequest.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (BleRequest.this.mResponse != null) {
                        BleRequest.this.mResponse.onResponse(i, BleRequest.this.mExtra);
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        });
    }

    public String toString() {
        return getClass().getSimpleName();
    }

    public void putIntExtra(String str, int i) {
        this.mExtra.putInt(str, i);
    }

    public int getIntExtra(String str, int i) {
        return this.mExtra.getInt(str, i);
    }

    public void putByteArray(String str, byte[] bArr) {
        this.mExtra.putByteArray(str, bArr);
    }

    public void putParcelable(String str, Parcelable parcelable) {
        this.mExtra.putParcelable(str, parcelable);
    }

    public Bundle getExtra() {
        return this.mExtra;
    }

    protected String getStatusText() {
        return Constants.getStatusText(getCurrentStatus());
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean readDescriptor(UUID uuid, UUID uuid2, UUID uuid3) {
        return this.mWorker.readDescriptor(uuid, uuid2, uuid3);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean writeDescriptor(UUID uuid, UUID uuid2, UUID uuid3, byte[] bArr) {
        return this.mWorker.writeDescriptor(uuid, uuid2, uuid3, bArr);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean openGatt() {
        return this.mWorker.openGatt();
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean discoverService() {
        return this.mWorker.discoverService();
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public int getCurrentStatus() {
        return this.mWorker.getCurrentStatus();
    }

    @Override // com.inuker.bluetooth.library.connect.request.IBleRequest
    public final void process(IBleConnectDispatcher iBleConnectDispatcher) {
        checkRuntime();
        this.mDispatcher = iBleConnectDispatcher;
        BluetoothLog.w(String.format("Process %s, status = %s", getClass().getSimpleName(), getStatusText()));
        if (!BluetoothUtils.isBleSupported()) {
            onRequestCompleted(-4);
            return;
        }
        if (!BluetoothUtils.isBluetoothEnabled()) {
            onRequestCompleted(-5);
            return;
        }
        try {
            registerGattResponseListener(this);
            processRequest();
        } catch (Throwable th) {
            BluetoothLog.e(th);
            onRequestCompleted(-10);
        }
    }

    protected void onRequestCompleted(int i) {
        checkRuntime();
        log(String.format("request complete: code = %d", Integer.valueOf(i)));
        this.mHandler.removeCallbacksAndMessages(null);
        clearGattResponseListener(this);
        onResponse(i);
        this.mDispatcher.onRequestCompleted(this);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public void closeGatt() {
        log(String.format("close gatt", new Object[0]));
        this.mWorker.closeGatt();
    }

    public boolean handleMessage(Message message) {
        if (message.what == 32) {
            this.mRequestTimeout = true;
            closeGatt();
        }
        return true;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public void registerGattResponseListener(GattResponseListener gattResponseListener) {
        this.mWorker.registerGattResponseListener(gattResponseListener);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public void clearGattResponseListener(GattResponseListener gattResponseListener) {
        this.mWorker.clearGattResponseListener(gattResponseListener);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean refreshDeviceCache() {
        return this.mWorker.refreshDeviceCache();
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean readCharacteristic(UUID uuid, UUID uuid2) {
        return this.mWorker.readCharacteristic(uuid, uuid2);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean writeCharacteristic(UUID uuid, UUID uuid2, byte[] bArr) {
        return this.mWorker.writeCharacteristic(uuid, uuid2, bArr);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean writeCharacteristicWithNoRsp(UUID uuid, UUID uuid2, byte[] bArr) {
        return this.mWorker.writeCharacteristicWithNoRsp(uuid, uuid2, bArr);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean setCharacteristicNotification(UUID uuid, UUID uuid2, boolean z) {
        return this.mWorker.setCharacteristicNotification(uuid, uuid2, z);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean setCharacteristicIndication(UUID uuid, UUID uuid2, boolean z) {
        return this.mWorker.setCharacteristicIndication(uuid, uuid2, z);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean readRemoteRssi() {
        return this.mWorker.readRemoteRssi();
    }

    protected void log(String str) {
        BluetoothLog.v(String.format("%s %s >>> %s", getClass().getSimpleName(), getAddress(), str));
    }

    public void setRuntimeChecker(RuntimeChecker runtimeChecker) {
        this.mRuntimeChecker = runtimeChecker;
    }

    @Override // com.inuker.bluetooth.library.RuntimeChecker
    public void checkRuntime() {
        this.mRuntimeChecker.checkRuntime();
    }

    @Override // com.inuker.bluetooth.library.connect.request.IBleRequest
    public void cancel() {
        checkRuntime();
        log(String.format("request canceled", new Object[0]));
        this.mHandler.removeCallbacksAndMessages(null);
        clearGattResponseListener(this);
        onResponse(-2);
    }

    public void onConnectStatusChanged(boolean z) {
        if (z) {
            return;
        }
        onRequestCompleted(this.mRequestTimeout ? -7 : -1);
    }

    protected void startRequestTiming() {
        this.mHandler.sendEmptyMessageDelayed(32, getTimeoutInMillis());
    }

    protected void stopRequestTiming() {
        this.mHandler.removeMessages(32);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public BleGattProfile getGattProfile() {
        return this.mWorker.getGattProfile();
    }
}
