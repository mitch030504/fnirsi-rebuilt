package com.inuker.bluetooth.library;

import android.content.Context;
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
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothClient implements IBluetoothClient {
    private IBluetoothClient mClient;

    public BluetoothClient(Context context) {
        if (context == null) {
            throw new NullPointerException("Context null");
        }
        this.mClient = BluetoothClientImpl.getInstance(context);
    }

    public void connect(String str, BleConnectResponse bleConnectResponse) {
        connect(str, null, bleConnectResponse);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void connect(String str, BleConnectOptions bleConnectOptions, BleConnectResponse bleConnectResponse) {
        BluetoothLog.v(String.format("connect %s", str));
        this.mClient.connect(str, bleConnectOptions, (BleConnectResponse) ProxyUtils.getUIProxy(bleConnectResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void disconnect(String str) {
        BluetoothLog.v(String.format("disconnect %s", str));
        this.mClient.disconnect(str);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void read(String str, UUID uuid, UUID uuid2, BleReadResponse bleReadResponse) {
        BluetoothLog.v(String.format("read character for %s: service = %s, character = %s", str, uuid, uuid2));
        this.mClient.read(str, uuid, uuid2, (BleReadResponse) ProxyUtils.getUIProxy(bleReadResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void write(String str, UUID uuid, UUID uuid2, byte[] bArr, BleWriteResponse bleWriteResponse) {
        BluetoothLog.v(String.format("write character for %s: service = %s, character = %s, value = %s", str, uuid, uuid2, ByteUtils.byteToString(bArr)));
        this.mClient.write(str, uuid, uuid2, bArr, (BleWriteResponse) ProxyUtils.getUIProxy(bleWriteResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void readDescriptor(String str, UUID uuid, UUID uuid2, UUID uuid3, BleReadResponse bleReadResponse) {
        BluetoothLog.v(String.format("readDescriptor for %s: service = %s, character = %s", str, uuid, uuid2));
        this.mClient.readDescriptor(str, uuid, uuid2, uuid3, (BleReadResponse) ProxyUtils.getUIProxy(bleReadResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void writeDescriptor(String str, UUID uuid, UUID uuid2, UUID uuid3, byte[] bArr, BleWriteResponse bleWriteResponse) {
        BluetoothLog.v(String.format("writeDescriptor for %s: service = %s, character = %s", str, uuid, uuid2));
        this.mClient.writeDescriptor(str, uuid, uuid2, uuid3, bArr, (BleWriteResponse) ProxyUtils.getUIProxy(bleWriteResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void writeNoRsp(String str, UUID uuid, UUID uuid2, byte[] bArr, BleWriteResponse bleWriteResponse) {
        BluetoothLog.v(String.format("writeNoRsp %s: service = %s, character = %s, value = %s", str, uuid, uuid2, ByteUtils.byteToString(bArr)));
        this.mClient.writeNoRsp(str, uuid, uuid2, bArr, (BleWriteResponse) ProxyUtils.getUIProxy(bleWriteResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void notify(String str, UUID uuid, UUID uuid2, BleNotifyResponse bleNotifyResponse) {
        BluetoothLog.v(String.format("notify %s: service = %s, character = %s", str, uuid, uuid2));
        this.mClient.notify(str, uuid, uuid2, (BleNotifyResponse) ProxyUtils.getUIProxy(bleNotifyResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unnotify(String str, UUID uuid, UUID uuid2, BleUnnotifyResponse bleUnnotifyResponse) {
        BluetoothLog.v(String.format("unnotify %s: service = %s, character = %s", str, uuid, uuid2));
        this.mClient.unnotify(str, uuid, uuid2, (BleUnnotifyResponse) ProxyUtils.getUIProxy(bleUnnotifyResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void indicate(String str, UUID uuid, UUID uuid2, BleNotifyResponse bleNotifyResponse) {
        BluetoothLog.v(String.format("indicate %s: service = %s, character = %s", str, uuid, uuid2));
        this.mClient.indicate(str, uuid, uuid2, (BleNotifyResponse) ProxyUtils.getUIProxy(bleNotifyResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unindicate(String str, UUID uuid, UUID uuid2, BleUnnotifyResponse bleUnnotifyResponse) {
        BluetoothLog.v(String.format("indicate %s: service = %s, character = %s", str, uuid, uuid2));
        unindicate(str, uuid, uuid2, (BleUnnotifyResponse) ProxyUtils.getUIProxy(bleUnnotifyResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void readRssi(String str, BleReadRssiResponse bleReadRssiResponse) {
        BluetoothLog.v(String.format("readRssi %s", str));
        this.mClient.readRssi(str, (BleReadRssiResponse) ProxyUtils.getUIProxy(bleReadRssiResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void search(SearchRequest searchRequest, SearchResponse searchResponse) {
        BluetoothLog.v(String.format("search %s", searchRequest));
        this.mClient.search(searchRequest, (SearchResponse) ProxyUtils.getUIProxy(searchResponse));
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void stopSearch() {
        BluetoothLog.v(String.format("stopSearch", new Object[0]));
        this.mClient.stopSearch();
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void registerConnectStatusListener(String str, BleConnectStatusListener bleConnectStatusListener) {
        this.mClient.registerConnectStatusListener(str, bleConnectStatusListener);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unregisterConnectStatusListener(String str, BleConnectStatusListener bleConnectStatusListener) {
        this.mClient.unregisterConnectStatusListener(str, bleConnectStatusListener);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void registerBluetoothStateListener(BluetoothStateListener bluetoothStateListener) {
        this.mClient.registerBluetoothStateListener(bluetoothStateListener);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unregisterBluetoothStateListener(BluetoothStateListener bluetoothStateListener) {
        this.mClient.unregisterBluetoothStateListener(bluetoothStateListener);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void registerBluetoothBondListener(BluetoothBondListener bluetoothBondListener) {
        this.mClient.registerBluetoothBondListener(bluetoothBondListener);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unregisterBluetoothBondListener(BluetoothBondListener bluetoothBondListener) {
        this.mClient.unregisterBluetoothBondListener(bluetoothBondListener);
    }

    public int getConnectStatus(String str) {
        return BluetoothUtils.getConnectStatus(str);
    }

    public boolean isBluetoothOpened() {
        return BluetoothUtils.isBluetoothEnabled();
    }

    public boolean openBluetooth() {
        return BluetoothUtils.openBluetooth();
    }

    public boolean closeBluetooth() {
        return BluetoothUtils.closeBluetooth();
    }

    public boolean isBleSupported() {
        return BluetoothUtils.isBleSupported();
    }

    public int getBondState(String str) {
        return BluetoothUtils.getBondState(str);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void clearRequest(String str, int i) {
        this.mClient.clearRequest(str, i);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void refreshCache(String str) {
        this.mClient.refreshCache(str);
    }
}
