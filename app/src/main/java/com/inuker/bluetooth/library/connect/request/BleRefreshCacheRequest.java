package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;

/* JADX INFO: loaded from: classes.dex */
public class BleRefreshCacheRequest extends BleRequest {
    public BleRefreshCacheRequest(BleGeneralResponse bleGeneralResponse) {
        super(bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.request.BleRequest
    public void processRequest() {
        refreshDeviceCache();
        this.mHandler.postDelayed(new Runnable() { // from class: com.inuker.bluetooth.library.connect.request.BleRefreshCacheRequest.1
            @Override // java.lang.Runnable
            public void run() {
                BleRefreshCacheRequest.this.onRequestCompleted(0);
            }
        }, 3000L);
    }
}
