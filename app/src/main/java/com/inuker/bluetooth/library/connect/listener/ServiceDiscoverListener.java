package com.inuker.bluetooth.library.connect.listener;

import com.inuker.bluetooth.library.model.BleGattProfile;

/* JADX INFO: loaded from: classes.dex */
public interface ServiceDiscoverListener extends GattResponseListener {
    void onServicesDiscovered(int i, BleGattProfile bleGattProfile);
}
