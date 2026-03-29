package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGattCharacteristic;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.ReadCharacterListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleReadRequest extends BleRequest implements ReadCharacterListener {
    private UUID mCharacterUUID;
    private UUID mServiceUUID;

    public BleReadRequest(UUID uuid, UUID uuid2, BleGeneralResponse bleGeneralResponse) {
        super(bleGeneralResponse);
        this.mServiceUUID = uuid;
        this.mCharacterUUID = uuid2;
    }

    @Override // com.inuker.bluetooth.library.connect.request.BleRequest
    public void processRequest() {
        int currentStatus = getCurrentStatus();
        if (currentStatus == 0) {
            onRequestCompleted(-1);
            return;
        }
        if (currentStatus == 2) {
            startRead();
        } else if (currentStatus == 19) {
            startRead();
        } else {
            onRequestCompleted(-1);
        }
    }

    private void startRead() {
        if (!readCharacteristic(this.mServiceUUID, this.mCharacterUUID)) {
            onRequestCompleted(-1);
        } else {
            startRequestTiming();
        }
    }

    @Override // com.inuker.bluetooth.library.connect.listener.ReadCharacterListener
    public void onCharacteristicRead(BluetoothGattCharacteristic bluetoothGattCharacteristic, int i, byte[] bArr) {
        stopRequestTiming();
        if (i == 0) {
            putByteArray(Constants.EXTRA_BYTE_VALUE, bArr);
            onRequestCompleted(0);
        } else {
            onRequestCompleted(-1);
        }
    }
}
