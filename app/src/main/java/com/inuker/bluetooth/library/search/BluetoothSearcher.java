package com.inuker.bluetooth.library.search;

import android.bluetooth.BluetoothAdapter;
import com.inuker.bluetooth.library.search.classic.BluetoothClassicSearcher;
import com.inuker.bluetooth.library.search.le.BluetoothLESearcher;
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothSearcher {
    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothSearchResponse mSearchResponse;

    public static BluetoothSearcher newInstance(int i) {
        if (i == 1) {
            return BluetoothClassicSearcher.getInstance();
        }
        if (i == 2) {
            return BluetoothLESearcher.getInstance();
        }
        throw new IllegalStateException(String.format("unknown search type %d", Integer.valueOf(i)));
    }

    protected void startScanBluetooth(BluetoothSearchResponse bluetoothSearchResponse) {
        this.mSearchResponse = bluetoothSearchResponse;
        notifySearchStarted();
    }

    protected void stopScanBluetooth() {
        notifySearchStopped();
        this.mSearchResponse = null;
    }

    protected void cancelScanBluetooth() {
        notifySearchCanceled();
        this.mSearchResponse = null;
    }

    private void notifySearchStarted() {
        BluetoothSearchResponse bluetoothSearchResponse = this.mSearchResponse;
        if (bluetoothSearchResponse != null) {
            bluetoothSearchResponse.onSearchStarted();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyDeviceFounded(SearchResult searchResult) {
        BluetoothSearchResponse bluetoothSearchResponse = this.mSearchResponse;
        if (bluetoothSearchResponse != null) {
            bluetoothSearchResponse.onDeviceFounded(searchResult);
        }
    }

    private void notifySearchStopped() {
        BluetoothSearchResponse bluetoothSearchResponse = this.mSearchResponse;
        if (bluetoothSearchResponse != null) {
            bluetoothSearchResponse.onSearchStopped();
        }
    }

    private void notifySearchCanceled() {
        BluetoothSearchResponse bluetoothSearchResponse = this.mSearchResponse;
        if (bluetoothSearchResponse != null) {
            bluetoothSearchResponse.onSearchCanceled();
        }
    }
}
