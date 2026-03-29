package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGattDescriptor;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.ReadDescriptorListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleReadDescriptorRequest extends BleRequest implements ReadDescriptorListener {
    private UUID mCharacterUUID;
    private UUID mDescriptorUUID;
    private UUID mServiceUUID;

    public BleReadDescriptorRequest(UUID uuid, UUID uuid2, UUID uuid3, BleGeneralResponse bleGeneralResponse) {
        super(bleGeneralResponse);
        this.mServiceUUID = uuid;
        this.mCharacterUUID = uuid2;
        this.mDescriptorUUID = uuid3;
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
        if (!readDescriptor(this.mServiceUUID, this.mCharacterUUID, this.mDescriptorUUID)) {
            onRequestCompleted(-1);
        } else {
            startRequestTiming();
        }
    }

    @Override // com.inuker.bluetooth.library.connect.listener.ReadDescriptorListener
    public void onDescriptorRead(BluetoothGattDescriptor bluetoothGattDescriptor, int i, byte[] bArr) {
        stopRequestTiming();
        if (i == 0) {
            putByteArray(Constants.EXTRA_BYTE_VALUE, bArr);
            onRequestCompleted(0);
        } else {
            onRequestCompleted(-1);
        }
    }
}
