package com.inuker.bluetooth.library.connect.response;

import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public interface BleNotifyResponse extends BleResponse {
    void onNotify(UUID uuid, UUID uuid2, byte[] bArr);
}
