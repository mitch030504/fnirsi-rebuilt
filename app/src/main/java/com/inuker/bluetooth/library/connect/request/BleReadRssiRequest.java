package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.ReadRssiListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;

/* JADX INFO: loaded from: classes.dex */
public class BleReadRssiRequest extends BleRequest implements ReadRssiListener {
    public BleReadRssiRequest(BleGeneralResponse bleGeneralResponse) {
        super(bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.request.BleRequest
    public void processRequest() {
        int currentStatus = getCurrentStatus();
        if (currentStatus == 0) {
            onRequestCompleted(-1);
            return;
        }
        if (currentStatus == 2) {
            startReadRssi();
        } else if (currentStatus == 19) {
            startReadRssi();
        } else {
            onRequestCompleted(-1);
        }
    }

    private void startReadRssi() {
        if (!readRemoteRssi()) {
            onRequestCompleted(-1);
        } else {
            startRequestTiming();
        }
    }

    @Override // com.inuker.bluetooth.library.connect.listener.ReadRssiListener
    public void onReadRemoteRssi(int i, int i2) {
        stopRequestTiming();
        if (i2 == 0) {
            putIntExtra(Constants.EXTRA_RSSI, i);
            onRequestCompleted(0);
        } else {
            onRequestCompleted(-1);
        }
    }
}
