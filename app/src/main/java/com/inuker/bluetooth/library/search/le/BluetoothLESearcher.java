package com.inuker.bluetooth.library.search.le;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import com.inuker.bluetooth.library.search.BluetoothSearcher;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothLESearcher extends BluetoothSearcher {
    private final BluetoothAdapter.LeScanCallback mLeScanCallback;

    private BluetoothLESearcher() {
        this.mLeScanCallback = new BluetoothAdapter.LeScanCallback() { // from class: com.inuker.bluetooth.library.search.le.BluetoothLESearcher.1
            @Override // android.bluetooth.BluetoothAdapter.LeScanCallback
            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
                BluetoothLESearcher.this.notifyDeviceFounded(new SearchResult(bluetoothDevice, i, bArr));
            }
        };
        this.mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter();
    }

    public static BluetoothLESearcher getInstance() {
        return BluetoothLESearcherHolder.instance;
    }

    private static class BluetoothLESearcherHolder {
        private static BluetoothLESearcher instance = new BluetoothLESearcher();

        private BluetoothLESearcherHolder() {
        }
    }

    @Override // com.inuker.bluetooth.library.search.BluetoothSearcher
    public void startScanBluetooth(BluetoothSearchResponse bluetoothSearchResponse) {
        super.startScanBluetooth(bluetoothSearchResponse);
        this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
    }

    @Override // com.inuker.bluetooth.library.search.BluetoothSearcher
    public void stopScanBluetooth() {
        try {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        } catch (Exception e) {
            BluetoothLog.e(e);
        }
        super.stopScanBluetooth();
    }

    @Override // com.inuker.bluetooth.library.search.BluetoothSearcher
    protected void cancelScanBluetooth() {
        this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        super.cancelScanBluetooth();
    }
}
