package com.inuker.bluetooth.library.connect.request;

import android.os.Message;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.ServiceDiscoverListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;

/* JADX INFO: loaded from: classes.dex */
public class BleConnectRequest extends BleRequest implements ServiceDiscoverListener {
    private static final int MSG_CONNECT = 1;
    private static final int MSG_CONNECT_TIMEOUT = 3;
    private static final int MSG_DISCOVER_SERVICE = 2;
    private static final int MSG_DISCOVER_SERVICE_TIMEOUT = 4;
    private static final int MSG_RETRY_DISCOVER_SERVICE = 5;
    private int mConnectCount;
    private BleConnectOptions mConnectOptions;
    private int mServiceDiscoverCount;

    public BleConnectRequest(BleConnectOptions bleConnectOptions, BleGeneralResponse bleGeneralResponse) {
        super(bleGeneralResponse);
        this.mConnectOptions = bleConnectOptions == null ? new BleConnectOptions.Builder().build() : bleConnectOptions;
    }

    @Override // com.inuker.bluetooth.library.connect.request.BleRequest
    public void processRequest() {
        processConnect();
    }

    private void processConnect() {
        this.mHandler.removeCallbacksAndMessages(null);
        this.mServiceDiscoverCount = 0;
        int currentStatus = getCurrentStatus();
        if (currentStatus == 0) {
            if (!doOpenNewGatt()) {
                closeGatt();
                return;
            } else {
                this.mHandler.sendEmptyMessageDelayed(3, this.mConnectOptions.getConnectTimeout());
                return;
            }
        }
        if (currentStatus == 2) {
            processDiscoverService();
        } else {
            if (currentStatus != 19) {
                return;
            }
            onConnectSuccess();
        }
    }

    private boolean doOpenNewGatt() {
        this.mConnectCount++;
        return openGatt();
    }

    private boolean doDiscoverService() {
        this.mServiceDiscoverCount++;
        return discoverService();
    }

    private void retryConnectIfNeeded() {
        if (this.mConnectCount < this.mConnectOptions.getConnectRetry() + 1) {
            retryConnectLater();
        } else {
            onRequestCompleted(-1);
        }
    }

    private void retryDiscoverServiceIfNeeded() {
        if (this.mServiceDiscoverCount < this.mConnectOptions.getServiceDiscoverRetry() + 1) {
            retryDiscoverServiceLater();
        } else {
            closeGatt();
        }
    }

    private void onServiceDiscoverFailed() {
        BluetoothLog.v(String.format("onServiceDiscoverFailed", new Object[0]));
        refreshDeviceCache();
        this.mHandler.sendEmptyMessage(5);
    }

    private void processDiscoverService() {
        BluetoothLog.v(String.format("processDiscoverService, status = %s", getStatusText()));
        int currentStatus = getCurrentStatus();
        if (currentStatus == 0) {
            retryConnectIfNeeded();
            return;
        }
        if (currentStatus != 2) {
            if (currentStatus != 19) {
                return;
            }
            onConnectSuccess();
        } else if (!doDiscoverService()) {
            onServiceDiscoverFailed();
        } else {
            this.mHandler.sendEmptyMessageDelayed(4, this.mConnectOptions.getServiceDiscoverTimeout());
        }
    }

    private void retryConnectLater() {
        log(String.format("retry connect later", new Object[0]));
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.sendEmptyMessageDelayed(1, 1000L);
    }

    private void retryDiscoverServiceLater() {
        log(String.format("retry discover service later", new Object[0]));
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.sendEmptyMessageDelayed(2, 1000L);
    }

    private void processConnectTimeout() {
        log(String.format("connect timeout", new Object[0]));
        this.mHandler.removeCallbacksAndMessages(null);
        closeGatt();
    }

    private void processDiscoverServiceTimeout() {
        log(String.format("service discover timeout", new Object[0]));
        this.mHandler.removeCallbacksAndMessages(null);
        closeGatt();
    }

    @Override // com.inuker.bluetooth.library.connect.request.BleRequest, android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            processConnect();
        } else if (i == 2) {
            processDiscoverService();
        } else if (i == 3) {
            processConnectTimeout();
        } else if (i == 4) {
            processDiscoverServiceTimeout();
        } else if (i == 5) {
            retryDiscoverServiceIfNeeded();
        }
        return super.handleMessage(message);
    }

    @Override // com.inuker.bluetooth.library.connect.request.BleRequest
    public String toString() {
        return "BleConnectRequest{options=" + this.mConnectOptions + '}';
    }

    @Override // com.inuker.bluetooth.library.connect.request.BleRequest, com.inuker.bluetooth.library.connect.listener.GattResponseListener
    public void onConnectStatusChanged(boolean z) {
        checkRuntime();
        this.mHandler.removeMessages(3);
        if (z) {
            this.mHandler.sendEmptyMessageDelayed(2, 300L);
        } else {
            this.mHandler.removeCallbacksAndMessages(null);
            retryConnectIfNeeded();
        }
    }

    @Override // com.inuker.bluetooth.library.connect.listener.ServiceDiscoverListener
    public void onServicesDiscovered(int i, BleGattProfile bleGattProfile) {
        checkRuntime();
        this.mHandler.removeMessages(4);
        if (i == 0) {
            onConnectSuccess();
        } else {
            onServiceDiscoverFailed();
        }
    }

    private void onConnectSuccess() {
        BleGattProfile gattProfile = getGattProfile();
        if (gattProfile != null) {
            putParcelable(Constants.EXTRA_GATT_PROFILE, gattProfile);
        }
        onRequestCompleted(0);
    }
}
