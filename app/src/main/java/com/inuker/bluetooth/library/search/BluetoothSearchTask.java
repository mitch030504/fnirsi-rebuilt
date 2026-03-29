package com.inuker.bluetooth.library.search;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.core.os.EnvironmentCompat;
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothSearchTask implements Handler.Callback {
    private static final int MSG_SEARCH_TIMEOUT = 34;
    private BluetoothSearcher mBluetoothSearcher;
    private Handler mHandler;
    private int searchDuration;
    private int searchType;

    public BluetoothSearchTask(SearchTask searchTask) {
        setSearchType(searchTask.getSearchType());
        setSearchDuration(searchTask.getSearchDuration());
        this.mHandler = new Handler(Looper.myLooper(), this);
    }

    public void setSearchType(int i) {
        this.searchType = i;
    }

    public void setSearchDuration(int i) {
        this.searchDuration = i;
    }

    public boolean isBluetoothLeSearch() {
        return this.searchType == 2;
    }

    public boolean isBluetoothClassicSearch() {
        return this.searchType == 1;
    }

    private BluetoothSearcher getBluetoothSearcher() {
        if (this.mBluetoothSearcher == null) {
            this.mBluetoothSearcher = BluetoothSearcher.newInstance(this.searchType);
        }
        return this.mBluetoothSearcher;
    }

    public void start(BluetoothSearchResponse bluetoothSearchResponse) {
        getBluetoothSearcher().startScanBluetooth(bluetoothSearchResponse);
        this.mHandler.sendEmptyMessageDelayed(34, this.searchDuration);
    }

    public void cancel() {
        this.mHandler.removeCallbacksAndMessages(null);
        getBluetoothSearcher().cancelScanBluetooth();
    }

    public String toString() {
        String str;
        if (isBluetoothLeSearch()) {
            str = "Ble";
        } else {
            str = isBluetoothClassicSearch() ? "classic" : EnvironmentCompat.MEDIA_UNKNOWN;
        }
        int i = this.searchDuration;
        return i >= 1000 ? String.format("%s search (%ds)", str, Integer.valueOf(i / 1000)) : String.format("%s search (%.1fs)", str, Double.valueOf((((double) i) * 1.0d) / 1000.0d));
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what != 34) {
            return true;
        }
        getBluetoothSearcher().stopScanBluetooth();
        return true;
    }
}
