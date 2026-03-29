package com.inuker.bluetooth.library.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.RuntimeChecker;
import com.inuker.bluetooth.library.connect.listener.GattResponseListener;
import com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse;
import com.inuker.bluetooth.library.connect.listener.ReadCharacterListener;
import com.inuker.bluetooth.library.connect.listener.ReadDescriptorListener;
import com.inuker.bluetooth.library.connect.listener.ReadRssiListener;
import com.inuker.bluetooth.library.connect.listener.ServiceDiscoverListener;
import com.inuker.bluetooth.library.connect.listener.WriteCharacterListener;
import com.inuker.bluetooth.library.connect.listener.WriteDescriptorListener;
import com.inuker.bluetooth.library.connect.response.BluetoothGattResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.Version;
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk;
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleConnectWorker implements Handler.Callback, IBleConnectWorker, IBluetoothGattResponse, ProxyInterceptor, RuntimeChecker {
    private static final int MSG_GATT_RESPONSE = 288;
    private BleGattProfile mBleGattProfile;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private IBluetoothGattResponse mBluetoothGattResponse;
    private volatile int mConnectStatus;
    private Map<UUID, Map<UUID, BluetoothGattCharacteristic>> mDeviceProfile;
    private GattResponseListener mGattResponseListener;
    private RuntimeChecker mRuntimeChecker;
    private Handler mWorkerHandler;

    public BleConnectWorker(String str, RuntimeChecker runtimeChecker) {
        BluetoothAdapter bluetoothAdapter = BluetoothUtils.getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            this.mBluetoothDevice = bluetoothAdapter.getRemoteDevice(str);
            this.mRuntimeChecker = runtimeChecker;
            this.mWorkerHandler = new Handler(Looper.myLooper(), this);
            this.mDeviceProfile = new HashMap();
            this.mBluetoothGattResponse = (IBluetoothGattResponse) ProxyUtils.getProxy(this, IBluetoothGattResponse.class, this);
            return;
        }
        throw new IllegalStateException("ble adapter null");
    }

    private void refreshServiceProfile() {
        BluetoothLog.v(String.format("refreshServiceProfile for %s", this.mBluetoothDevice.getAddress()));
        List<BluetoothGattService> services = this.mBluetoothGatt.getServices();
        HashMap map = new HashMap();
        for (BluetoothGattService bluetoothGattService : services) {
            UUID uuid = bluetoothGattService.getUuid();
            Map map2 = (Map) map.get(uuid);
            if (map2 == null) {
                BluetoothLog.v("Service: " + uuid);
                map2 = new HashMap();
                map.put(bluetoothGattService.getUuid(), map2);
            }
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                BluetoothLog.v("character: uuid = " + bluetoothGattCharacteristic.getUuid());
                map2.put(bluetoothGattCharacteristic.getUuid(), bluetoothGattCharacteristic);
            }
        }
        this.mDeviceProfile.clear();
        this.mDeviceProfile.putAll(map);
        this.mBleGattProfile = new BleGattProfile(this.mDeviceProfile);
    }

    private BluetoothGattCharacteristic getCharacter(UUID uuid, UUID uuid2) {
        BluetoothGatt bluetoothGatt;
        BluetoothGattService service;
        Map<UUID, BluetoothGattCharacteristic> map;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = (uuid == null || uuid2 == null || (map = this.mDeviceProfile.get(uuid)) == null) ? null : map.get(uuid2);
        return (bluetoothGattCharacteristic != null || (bluetoothGatt = this.mBluetoothGatt) == null || (service = bluetoothGatt.getService(uuid)) == null) ? bluetoothGattCharacteristic : service.getCharacteristic(uuid2);
    }

    private void setConnectStatus(int i) {
        BluetoothLog.v(String.format("setConnectStatus status = %s", Constants.getStatusText(i)));
        this.mConnectStatus = i;
    }

    @Override // com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse
    public void onConnectionStateChange(int i, int i2) {
        checkRuntime();
        BluetoothLog.v(String.format("onConnectionStateChange for %s: status = %d, newState = %d", this.mBluetoothDevice.getAddress(), Integer.valueOf(i), Integer.valueOf(i2)));
        if (i == 0 && i2 == 2) {
            setConnectStatus(2);
            GattResponseListener gattResponseListener = this.mGattResponseListener;
            if (gattResponseListener != null) {
                gattResponseListener.onConnectStatusChanged(true);
                return;
            }
            return;
        }
        closeGatt();
    }

    @Override // com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse
    public void onServicesDiscovered(int i) {
        checkRuntime();
        BluetoothLog.v(String.format("onServicesDiscovered for %s: status = %d", this.mBluetoothDevice.getAddress(), Integer.valueOf(i)));
        if (i == 0) {
            setConnectStatus(19);
            broadcastConnectStatus(16);
            refreshServiceProfile();
        }
        GattResponseListener gattResponseListener = this.mGattResponseListener;
        if (gattResponseListener == null || !(gattResponseListener instanceof ServiceDiscoverListener)) {
            return;
        }
        ((ServiceDiscoverListener) gattResponseListener).onServicesDiscovered(i, this.mBleGattProfile);
    }

    @Override // com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse
    public void onCharacteristicRead(BluetoothGattCharacteristic bluetoothGattCharacteristic, int i, byte[] bArr) {
        checkRuntime();
        BluetoothLog.v(String.format("onCharacteristicRead for %s: status = %d, service = 0x%s, character = 0x%s, value = %s", this.mBluetoothDevice.getAddress(), Integer.valueOf(i), bluetoothGattCharacteristic.getService().getUuid(), bluetoothGattCharacteristic.getUuid(), ByteUtils.byteToString(bArr)));
        GattResponseListener gattResponseListener = this.mGattResponseListener;
        if (gattResponseListener == null || !(gattResponseListener instanceof ReadCharacterListener)) {
            return;
        }
        ((ReadCharacterListener) gattResponseListener).onCharacteristicRead(bluetoothGattCharacteristic, i, bArr);
    }

    @Override // com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse
    public void onCharacteristicWrite(BluetoothGattCharacteristic bluetoothGattCharacteristic, int i, byte[] bArr) {
        checkRuntime();
        BluetoothLog.v(String.format("onCharacteristicWrite for %s: status = %d, service = 0x%s, character = 0x%s, value = %s", this.mBluetoothDevice.getAddress(), Integer.valueOf(i), bluetoothGattCharacteristic.getService().getUuid(), bluetoothGattCharacteristic.getUuid(), ByteUtils.byteToString(bArr)));
        GattResponseListener gattResponseListener = this.mGattResponseListener;
        if (gattResponseListener == null || !(gattResponseListener instanceof WriteCharacterListener)) {
            return;
        }
        ((WriteCharacterListener) gattResponseListener).onCharacteristicWrite(bluetoothGattCharacteristic, i, bArr);
    }

    @Override // com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse
    public void onCharacteristicChanged(BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr) {
        checkRuntime();
        BluetoothLog.v(String.format("onCharacteristicChanged for %s: value = %s, service = 0x%s, character = 0x%s", this.mBluetoothDevice.getAddress(), ByteUtils.byteToString(bArr), bluetoothGattCharacteristic.getService().getUuid(), bluetoothGattCharacteristic.getUuid()));
        broadcastCharacterChanged(bluetoothGattCharacteristic.getService().getUuid(), bluetoothGattCharacteristic.getUuid(), bArr);
    }

    @Override // com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse
    public void onDescriptorRead(BluetoothGattDescriptor bluetoothGattDescriptor, int i, byte[] bArr) {
        checkRuntime();
        BluetoothLog.v(String.format("onDescriptorRead for %s: status = %d, service = 0x%s, character = 0x%s, descriptor = 0x%s", this.mBluetoothDevice.getAddress(), Integer.valueOf(i), bluetoothGattDescriptor.getCharacteristic().getService().getUuid(), bluetoothGattDescriptor.getCharacteristic().getUuid(), bluetoothGattDescriptor.getUuid()));
        GattResponseListener gattResponseListener = this.mGattResponseListener;
        if (gattResponseListener == null || !(gattResponseListener instanceof ReadDescriptorListener)) {
            return;
        }
        ((ReadDescriptorListener) gattResponseListener).onDescriptorRead(bluetoothGattDescriptor, i, bArr);
    }

    @Override // com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse
    public void onDescriptorWrite(BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        checkRuntime();
        BluetoothLog.v(String.format("onDescriptorWrite for %s: status = %d, service = 0x%s, character = 0x%s, descriptor = 0x%s", this.mBluetoothDevice.getAddress(), Integer.valueOf(i), bluetoothGattDescriptor.getCharacteristic().getService().getUuid(), bluetoothGattDescriptor.getCharacteristic().getUuid(), bluetoothGattDescriptor.getUuid()));
        GattResponseListener gattResponseListener = this.mGattResponseListener;
        if (gattResponseListener == null || !(gattResponseListener instanceof WriteDescriptorListener)) {
            return;
        }
        ((WriteDescriptorListener) gattResponseListener).onDescriptorWrite(bluetoothGattDescriptor, i);
    }

    @Override // com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse
    public void onReadRemoteRssi(int i, int i2) {
        checkRuntime();
        BluetoothLog.v(String.format("onReadRemoteRssi for %s, rssi = %d, status = %d", this.mBluetoothDevice.getAddress(), Integer.valueOf(i), Integer.valueOf(i2)));
        GattResponseListener gattResponseListener = this.mGattResponseListener;
        if (gattResponseListener == null || !(gattResponseListener instanceof ReadRssiListener)) {
            return;
        }
        ((ReadRssiListener) gattResponseListener).onReadRemoteRssi(i, i2);
    }

    private void broadcastConnectStatus(int i) {
        Intent intent = new Intent(Constants.ACTION_CONNECT_STATUS_CHANGED);
        intent.putExtra(Constants.EXTRA_MAC, this.mBluetoothDevice.getAddress());
        intent.putExtra(Constants.EXTRA_STATUS, i);
        BluetoothUtils.sendBroadcast(intent);
    }

    private void broadcastCharacterChanged(UUID uuid, UUID uuid2, byte[] bArr) {
        Intent intent = new Intent(Constants.ACTION_CHARACTER_CHANGED);
        intent.putExtra(Constants.EXTRA_MAC, this.mBluetoothDevice.getAddress());
        intent.putExtra(Constants.EXTRA_SERVICE_UUID, uuid);
        intent.putExtra(Constants.EXTRA_CHARACTER_UUID, uuid2);
        intent.putExtra(Constants.EXTRA_BYTE_VALUE, bArr);
        BluetoothUtils.sendBroadcast(intent);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean openGatt() {
        checkRuntime();
        BluetoothLog.v(String.format("openGatt for %s", getAddress()));
        if (this.mBluetoothGatt != null) {
            BluetoothLog.e(String.format("Previous gatt not closed", new Object[0]));
            return true;
        }
        Context context = BluetoothUtils.getContext();
        BluetoothGattResponse bluetoothGattResponse = new BluetoothGattResponse(this.mBluetoothGattResponse);
        if (Version.isMarshmallow()) {
            this.mBluetoothGatt = this.mBluetoothDevice.connectGatt(context, false, bluetoothGattResponse, 2);
        } else {
            this.mBluetoothGatt = this.mBluetoothDevice.connectGatt(context, false, bluetoothGattResponse);
        }
        if (this.mBluetoothGatt != null) {
            return true;
        }
        BluetoothLog.e(String.format("openGatt failed: connectGatt return null!", new Object[0]));
        return false;
    }

    private String getAddress() {
        return this.mBluetoothDevice.getAddress();
    }

    private boolean writeCharacteristicCompat(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr, int i) {
        if (Build.VERSION.SDK_INT >= 33) {
            int iWriteCharacteristic = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic, bArr, i);
            if (iWriteCharacteristic == 0) {
                return true;
            }
            BluetoothLog.e(String.format("writeCharacteristic returned %d", Integer.valueOf(iWriteCharacteristic)));
            return false;
        }
        bluetoothGattCharacteristic.setValue(bArr);
        bluetoothGattCharacteristic.setWriteType(i);
        return bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }

    private boolean writeDescriptorCompat(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, byte[] bArr) {
        if (Build.VERSION.SDK_INT >= 33) {
            int iWriteDescriptor = bluetoothGatt.writeDescriptor(bluetoothGattDescriptor, bArr);
            if (iWriteDescriptor == 0) {
                return true;
            }
            BluetoothLog.e(String.format("writeDescriptor returned %d", Integer.valueOf(iWriteDescriptor)));
            return false;
        }
        if (!bluetoothGattDescriptor.setValue(bArr)) {
            return false;
        }
        return bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public void closeGatt() {
        checkRuntime();
        BluetoothLog.v(String.format("closeGatt for %s", getAddress()));
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
        GattResponseListener gattResponseListener = this.mGattResponseListener;
        if (gattResponseListener != null) {
            gattResponseListener.onConnectStatusChanged(false);
        }
        setConnectStatus(0);
        broadcastConnectStatus(32);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean discoverService() {
        checkRuntime();
        BluetoothLog.v(String.format("discoverService for %s", getAddress()));
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            BluetoothLog.e(String.format("discoverService but gatt is null!", new Object[0]));
            return false;
        }
        if (bluetoothGatt.discoverServices()) {
            return true;
        }
        BluetoothLog.e(String.format("discoverServices failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public int getCurrentStatus() {
        checkRuntime();
        return this.mConnectStatus;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public void registerGattResponseListener(GattResponseListener gattResponseListener) {
        checkRuntime();
        this.mGattResponseListener = gattResponseListener;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public void clearGattResponseListener(GattResponseListener gattResponseListener) {
        checkRuntime();
        if (this.mGattResponseListener == gattResponseListener) {
            this.mGattResponseListener = null;
        }
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean refreshDeviceCache() {
        BluetoothLog.v(String.format("refreshDeviceCache for %s", getAddress()));
        checkRuntime();
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null", new Object[0]));
            return false;
        }
        if (BluetoothUtils.refreshGattCache(bluetoothGatt)) {
            return true;
        }
        BluetoothLog.e(String.format("refreshDeviceCache failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean readCharacteristic(UUID uuid, UUID uuid2) {
        BluetoothLog.v(String.format("readCharacteristic for %s: service = 0x%s, character = 0x%s", this.mBluetoothDevice.getAddress(), uuid, uuid2));
        checkRuntime();
        BluetoothGattCharacteristic character = getCharacter(uuid, uuid2);
        if (character == null) {
            BluetoothLog.e(String.format("characteristic not exist!", new Object[0]));
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null", new Object[0]));
            return false;
        }
        if (bluetoothGatt.readCharacteristic(character)) {
            return true;
        }
        BluetoothLog.e(String.format("readCharacteristic failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean writeCharacteristic(UUID uuid, UUID uuid2, byte[] bArr) {
        BluetoothLog.v(String.format("writeCharacteristic for %s: service = 0x%s, character = 0x%s, value = 0x%s", this.mBluetoothDevice.getAddress(), uuid, uuid2, ByteUtils.byteToString(bArr)));
        checkRuntime();
        BluetoothGattCharacteristic character = getCharacter(uuid, uuid2);
        if (character == null) {
            BluetoothLog.e(String.format("characteristic not exist!", new Object[0]));
            return false;
        }
        if (this.mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null", new Object[0]));
            return false;
        }
        if (bArr == null) {
            bArr = ByteUtils.EMPTY_BYTES;
        }
        if (writeCharacteristicCompat(this.mBluetoothGatt, character, bArr, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)) {
            return true;
        }
        BluetoothLog.e(String.format("writeCharacteristic failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean readDescriptor(UUID uuid, UUID uuid2, UUID uuid3) {
        BluetoothLog.v(String.format("readDescriptor for %s: service = 0x%s, character = 0x%s, descriptor = 0x%s", this.mBluetoothDevice.getAddress(), uuid, uuid2, uuid3));
        checkRuntime();
        BluetoothGattCharacteristic character = getCharacter(uuid, uuid2);
        if (character == null) {
            BluetoothLog.e(String.format("characteristic not exist!", new Object[0]));
            return false;
        }
        BluetoothGattDescriptor descriptor = character.getDescriptor(uuid3);
        if (descriptor == null) {
            BluetoothLog.e(String.format("descriptor not exist", new Object[0]));
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null", new Object[0]));
            return false;
        }
        if (bluetoothGatt.readDescriptor(descriptor)) {
            return true;
        }
        BluetoothLog.e(String.format("readDescriptor failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean writeDescriptor(UUID uuid, UUID uuid2, UUID uuid3, byte[] bArr) {
        BluetoothLog.v(String.format("writeDescriptor for %s: service = 0x%s, character = 0x%s, descriptor = 0x%s, value = 0x%s", this.mBluetoothDevice.getAddress(), uuid, uuid2, uuid3, ByteUtils.byteToString(bArr)));
        checkRuntime();
        BluetoothGattCharacteristic character = getCharacter(uuid, uuid2);
        if (character == null) {
            BluetoothLog.e(String.format("characteristic not exist!", new Object[0]));
            return false;
        }
        BluetoothGattDescriptor descriptor = character.getDescriptor(uuid3);
        if (descriptor == null) {
            BluetoothLog.e(String.format("descriptor not exist", new Object[0]));
            return false;
        }
        if (this.mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null", new Object[0]));
            return false;
        }
        if (bArr == null) {
            bArr = ByteUtils.EMPTY_BYTES;
        }
        if (writeDescriptorCompat(this.mBluetoothGatt, descriptor, bArr)) {
            return true;
        }
        BluetoothLog.e(String.format("writeDescriptor failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean writeCharacteristicWithNoRsp(UUID uuid, UUID uuid2, byte[] bArr) {
        BluetoothLog.v(String.format("writeCharacteristicWithNoRsp for %s: service = 0x%s, character = 0x%s, value = 0x%s", this.mBluetoothDevice.getAddress(), uuid, uuid2, ByteUtils.byteToString(bArr)));
        checkRuntime();
        BluetoothGattCharacteristic character = getCharacter(uuid, uuid2);
        if (character == null) {
            BluetoothLog.e(String.format("characteristic not exist!", new Object[0]));
            return false;
        }
        if (this.mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null", new Object[0]));
            return false;
        }
        if (bArr == null) {
            bArr = ByteUtils.EMPTY_BYTES;
        }
        if (writeCharacteristicCompat(this.mBluetoothGatt, character, bArr, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)) {
            return true;
        }
        BluetoothLog.e(String.format("writeCharacteristic failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean setCharacteristicNotification(UUID uuid, UUID uuid2, boolean z) {
        checkRuntime();
        BluetoothLog.v(String.format("setCharacteristicNotification for %s, service = %s, character = %s, enable = %b", getAddress(), uuid, uuid2, Boolean.valueOf(z)));
        BluetoothGattCharacteristic character = getCharacter(uuid, uuid2);
        if (character == null) {
            BluetoothLog.e(String.format("characteristic not exist!", new Object[0]));
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null", new Object[0]));
            return false;
        }
        if (!bluetoothGatt.setCharacteristicNotification(character, z)) {
            BluetoothLog.e(String.format("setCharacteristicNotification failed", new Object[0]));
            return false;
        }
        BluetoothGattDescriptor descriptor = character.getDescriptor(Constants.CLIENT_CHARACTERISTIC_CONFIG);
        if (descriptor == null) {
            BluetoothLog.e(String.format("getDescriptor for notify null!", new Object[0]));
            return false;
        }
        if (writeDescriptorCompat(this.mBluetoothGatt, descriptor, z ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)) {
            return true;
        }
        BluetoothLog.e(String.format("writeDescriptor for notify failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean setCharacteristicIndication(UUID uuid, UUID uuid2, boolean z) {
        checkRuntime();
        BluetoothLog.v(String.format("setCharacteristicIndication for %s, service = %s, character = %s, enable = %b", getAddress(), uuid, uuid2, Boolean.valueOf(z)));
        BluetoothGattCharacteristic character = getCharacter(uuid, uuid2);
        if (character == null) {
            BluetoothLog.e(String.format("characteristic not exist!", new Object[0]));
            return false;
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null", new Object[0]));
            return false;
        }
        if (!bluetoothGatt.setCharacteristicNotification(character, z)) {
            BluetoothLog.e(String.format("setCharacteristicIndication failed", new Object[0]));
            return false;
        }
        BluetoothGattDescriptor descriptor = character.getDescriptor(Constants.CLIENT_CHARACTERISTIC_CONFIG);
        if (descriptor == null) {
            BluetoothLog.e(String.format("getDescriptor for indicate null!", new Object[0]));
            return false;
        }
        if (writeDescriptorCompat(this.mBluetoothGatt, descriptor, z ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)) {
            return true;
        }
        BluetoothLog.e(String.format("writeDescriptor for indicate failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public boolean readRemoteRssi() {
        checkRuntime();
        BluetoothLog.v(String.format("readRemoteRssi for %s", getAddress()));
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null", new Object[0]));
            return false;
        }
        if (bluetoothGatt.readRemoteRssi()) {
            return true;
        }
        BluetoothLog.e(String.format("readRemoteRssi failed", new Object[0]));
        return false;
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectWorker
    public BleGattProfile getGattProfile() {
        return this.mBleGattProfile;
    }

    private boolean isCharacteristicReadable(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return (bluetoothGattCharacteristic == null || (bluetoothGattCharacteristic.getProperties() & 2) == 0) ? false : true;
    }

    private boolean isCharacteristicWritable(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return (bluetoothGattCharacteristic == null || (bluetoothGattCharacteristic.getProperties() & 8) == 0) ? false : true;
    }

    private boolean isCharacteristicNoRspWritable(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return (bluetoothGattCharacteristic == null || (bluetoothGattCharacteristic.getProperties() & 4) == 0) ? false : true;
    }

    private boolean isCharacteristicNotifyable(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return (bluetoothGattCharacteristic == null || (bluetoothGattCharacteristic.getProperties() & 16) == 0) ? false : true;
    }

    private boolean isCharacteristicIndicatable(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return (bluetoothGattCharacteristic == null || (bluetoothGattCharacteristic.getProperties() & 32) == 0) ? false : true;
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what != MSG_GATT_RESPONSE) {
            return true;
        }
        ProxyBulk.safeInvoke(message.obj);
        return true;
    }

    @Override // com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor
    public boolean onIntercept(Object obj, Method method, Object[] objArr) {
        this.mWorkerHandler.obtainMessage(MSG_GATT_RESPONSE, new ProxyBulk(obj, method, objArr)).sendToTarget();
        return true;
    }

    @Override // com.inuker.bluetooth.library.RuntimeChecker
    public void checkRuntime() {
        this.mRuntimeChecker.checkRuntime();
    }
}
