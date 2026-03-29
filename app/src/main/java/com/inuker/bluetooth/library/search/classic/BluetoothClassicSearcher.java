package com.inuker.bluetooth.library.search.classic;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.inuker.bluetooth.library.search.BluetoothSearcher;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import kotlin.jvm.internal.ShortCompanionObject;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothClassicSearcher extends BluetoothSearcher {
    private BluetoothSearchReceiver mReceiver;

    private BluetoothClassicSearcher() {
        this.mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter();
    }

    public static BluetoothClassicSearcher getInstance() {
        return BluetoothClassicSearcherHolder.instance;
    }

    private static class BluetoothClassicSearcherHolder {
        private static BluetoothClassicSearcher instance = new BluetoothClassicSearcher();

        private BluetoothClassicSearcherHolder() {
        }
    }

    @Override // com.inuker.bluetooth.library.search.BluetoothSearcher
    public void startScanBluetooth(BluetoothSearchResponse bluetoothSearchResponse) {
        super.startScanBluetooth(bluetoothSearchResponse);
        registerReceiver();
        if (this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
        this.mBluetoothAdapter.startDiscovery();
    }

    @Override // com.inuker.bluetooth.library.search.BluetoothSearcher
    public void stopScanBluetooth() {
        unregisterReceiver();
        if (this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
        super.stopScanBluetooth();
    }

    @Override // com.inuker.bluetooth.library.search.BluetoothSearcher
    protected void cancelScanBluetooth() {
        unregisterReceiver();
        if (this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
        super.cancelScanBluetooth();
    }

    private void registerReceiver() {
        if (this.mReceiver == null) {
            BluetoothSearchReceiver bluetoothSearchReceiver = new BluetoothSearchReceiver();
            this.mReceiver = bluetoothSearchReceiver;
            BluetoothUtils.registerReceiver(bluetoothSearchReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
        }
    }

    private void unregisterReceiver() {
        BluetoothSearchReceiver bluetoothSearchReceiver = this.mReceiver;
        if (bluetoothSearchReceiver != null) {
            BluetoothUtils.unregisterReceiver(bluetoothSearchReceiver);
            this.mReceiver = null;
        }
    }

    private class BluetoothSearchReceiver extends BroadcastReceiver {
        private BluetoothSearchReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.device.action.FOUND")) {
                BluetoothClassicSearcher.this.notifyDeviceFounded(new SearchResult((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"), intent.getShortExtra("android.bluetooth.device.extra.RSSI", ShortCompanionObject.MIN_VALUE), null));
            }
        }
    }
}
