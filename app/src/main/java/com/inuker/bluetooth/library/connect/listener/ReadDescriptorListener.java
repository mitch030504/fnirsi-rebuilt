package com.inuker.bluetooth.library.connect.listener;

import android.bluetooth.BluetoothGattDescriptor;

/* JADX INFO: loaded from: classes.dex */
public interface ReadDescriptorListener extends GattResponseListener {
    void onDescriptorRead(BluetoothGattDescriptor bluetoothGattDescriptor, int i, byte[] bArr);
}
