package com.inuker.bluetooth.library;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public interface IBluetoothClient {
    void clearRequest(String str, int i);

    void connect(String str, BleConnectOptions bleConnectOptions, BleConnectResponse bleConnectResponse);

    void disconnect(String str);

    void indicate(String str, UUID uuid, UUID uuid2, BleNotifyResponse bleNotifyResponse);

    void notify(String str, UUID uuid, UUID uuid2, BleNotifyResponse bleNotifyResponse);

    void read(String str, UUID uuid, UUID uuid2, BleReadResponse bleReadResponse);

    void readDescriptor(String str, UUID uuid, UUID uuid2, UUID uuid3, BleReadResponse bleReadResponse);

    void readRssi(String str, BleReadRssiResponse bleReadRssiResponse);

    void refreshCache(String str);

    void registerBluetoothBondListener(BluetoothBondListener bluetoothBondListener);

    void registerBluetoothStateListener(BluetoothStateListener bluetoothStateListener);

    void registerConnectStatusListener(String str, BleConnectStatusListener bleConnectStatusListener);

    void search(SearchRequest searchRequest, SearchResponse searchResponse);

    void stopSearch();

    void unindicate(String str, UUID uuid, UUID uuid2, BleUnnotifyResponse bleUnnotifyResponse);

    void unnotify(String str, UUID uuid, UUID uuid2, BleUnnotifyResponse bleUnnotifyResponse);

    void unregisterBluetoothBondListener(BluetoothBondListener bluetoothBondListener);

    void unregisterBluetoothStateListener(BluetoothStateListener bluetoothStateListener);

    void unregisterConnectStatusListener(String str, BleConnectStatusListener bleConnectStatusListener);

    void write(String str, UUID uuid, UUID uuid2, byte[] bArr, BleWriteResponse bleWriteResponse);

    void writeDescriptor(String str, UUID uuid, UUID uuid2, UUID uuid3, byte[] bArr, BleWriteResponse bleWriteResponse);

    void writeNoRsp(String str, UUID uuid, UUID uuid2, byte[] bArr, BleWriteResponse bleWriteResponse);
}
