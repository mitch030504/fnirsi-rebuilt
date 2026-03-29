package com.inuker.bluetooth.library.search;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothSearchRequest implements Handler.Callback {
    private static final int MSG_DEVICE_FOUND = 18;
    private static final int MSG_START_SEARCH = 17;
    private static final int SCAN_INTERVAL = 100;
    private BluetoothSearchTask mCurrentTask;
    private Handler mHandler;
    private BluetoothSearchResponse mSearchResponse;
    private List<BluetoothSearchTask> mSearchTaskList = new ArrayList();

    public BluetoothSearchRequest(SearchRequest searchRequest) {
        Iterator<SearchTask> it = searchRequest.getTasks().iterator();
        while (it.hasNext()) {
            this.mSearchTaskList.add(new BluetoothSearchTask(it.next()));
        }
        this.mHandler = new Handler(Looper.myLooper(), this);
    }

    public void setSearchResponse(BluetoothSearchResponse bluetoothSearchResponse) {
        this.mSearchResponse = bluetoothSearchResponse;
    }

    public void start() {
        BluetoothSearchResponse bluetoothSearchResponse = this.mSearchResponse;
        if (bluetoothSearchResponse != null) {
            bluetoothSearchResponse.onSearchStarted();
        }
        notifyConnectedBluetoothDevices();
        this.mHandler.sendEmptyMessageDelayed(17, 100L);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 17) {
            scheduleNewSearchTask();
            return true;
        }
        if (i != 18) {
            return true;
        }
        SearchResult searchResult = (SearchResult) message.obj;
        BluetoothSearchResponse bluetoothSearchResponse = this.mSearchResponse;
        if (bluetoothSearchResponse == null) {
            return true;
        }
        bluetoothSearchResponse.onDeviceFounded(searchResult);
        return true;
    }

    private void scheduleNewSearchTask() {
        if (this.mSearchTaskList.size() > 0) {
            BluetoothSearchTask bluetoothSearchTaskRemove = this.mSearchTaskList.remove(0);
            this.mCurrentTask = bluetoothSearchTaskRemove;
            bluetoothSearchTaskRemove.start(new BluetoothSearchTaskResponse(bluetoothSearchTaskRemove));
        } else {
            this.mCurrentTask = null;
            BluetoothSearchResponse bluetoothSearchResponse = this.mSearchResponse;
            if (bluetoothSearchResponse != null) {
                bluetoothSearchResponse.onSearchStopped();
            }
        }
    }

    public void cancel() {
        BluetoothSearchTask bluetoothSearchTask = this.mCurrentTask;
        if (bluetoothSearchTask != null) {
            bluetoothSearchTask.cancel();
            this.mCurrentTask = null;
        }
        this.mSearchTaskList.clear();
        BluetoothSearchResponse bluetoothSearchResponse = this.mSearchResponse;
        if (bluetoothSearchResponse != null) {
            bluetoothSearchResponse.onSearchCanceled();
        }
        this.mSearchResponse = null;
    }

    private void notifyConnectedBluetoothDevices() {
        boolean z = false;
        boolean z2 = false;
        for (BluetoothSearchTask bluetoothSearchTask : this.mSearchTaskList) {
            if (bluetoothSearchTask.isBluetoothLeSearch()) {
                z = true;
            } else {
                if (!bluetoothSearchTask.isBluetoothClassicSearch()) {
                    throw new IllegalArgumentException("unknown search task type!");
                }
                z2 = true;
            }
        }
        if (z) {
            notifyConnectedBluetoothLeDevices();
        }
        if (z2) {
            notifyBondedBluetoothClassicDevices();
        }
    }

    private void notifyConnectedBluetoothLeDevices() {
        Iterator<BluetoothDevice> it = BluetoothUtils.getConnectedBluetoothLeDevices().iterator();
        while (it.hasNext()) {
            notifyDeviceFounded(new SearchResult(it.next()));
        }
    }

    private void notifyBondedBluetoothClassicDevices() {
        Iterator<BluetoothDevice> it = BluetoothUtils.getBondedBluetoothClassicDevices().iterator();
        while (it.hasNext()) {
            notifyDeviceFounded(new SearchResult(it.next()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyDeviceFounded(SearchResult searchResult) {
        this.mHandler.obtainMessage(18, searchResult).sendToTarget();
    }

    private class BluetoothSearchTaskResponse implements BluetoothSearchResponse {
        BluetoothSearchTask task;

        BluetoothSearchTaskResponse(BluetoothSearchTask bluetoothSearchTask) {
            this.task = bluetoothSearchTask;
        }

        @Override // com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
        public void onSearchStarted() {
            BluetoothLog.v(String.format("%s onSearchStarted", this.task));
        }

        @Override // com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
        public void onDeviceFounded(SearchResult searchResult) {
            BluetoothLog.v(String.format("onDeviceFounded %s", searchResult));
            BluetoothSearchRequest.this.notifyDeviceFounded(searchResult);
        }

        @Override // com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
        public void onSearchStopped() {
            BluetoothLog.v(String.format("%s onSearchStopped", this.task));
            BluetoothSearchRequest.this.mHandler.sendEmptyMessageDelayed(17, 100L);
        }

        @Override // com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
        public void onSearchCanceled() {
            BluetoothLog.v(String.format("%s onSearchCanceled", this.task));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<BluetoothSearchTask> it = this.mSearchTaskList.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString() + ", ");
        }
        return sb.toString();
    }
}
