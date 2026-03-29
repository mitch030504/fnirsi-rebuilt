package com.inuker.bluetooth.library.search;

import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;

/* JADX INFO: loaded from: classes.dex */
public interface IBluetoothSearchHelper {
    void startSearch(BluetoothSearchRequest bluetoothSearchRequest, BluetoothSearchResponse bluetoothSearchResponse);

    void stopSearch();
}
