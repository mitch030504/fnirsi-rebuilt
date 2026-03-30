package com.inuker.bluetooth.library.connect

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.RuntimeChecker
import com.inuker.bluetooth.library.connect.listener.GattResponseListener
import com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse
import com.inuker.bluetooth.library.connect.listener.ReadCharacterListener
import com.inuker.bluetooth.library.connect.listener.ReadDescriptorListener
import com.inuker.bluetooth.library.connect.listener.ReadRssiListener
import com.inuker.bluetooth.library.connect.listener.ServiceDiscoverListener
import com.inuker.bluetooth.library.connect.listener.WriteCharacterListener
import com.inuker.bluetooth.library.connect.listener.WriteDescriptorListener
import com.inuker.bluetooth.library.connect.response.BluetoothGattResponse
import com.inuker.bluetooth.library.model.BleGattProfile
import com.inuker.bluetooth.library.utils.BluetoothLog
import com.inuker.bluetooth.library.utils.BluetoothUtils
import com.inuker.bluetooth.library.utils.ByteUtils
import com.inuker.bluetooth.library.utils.Version
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils
import java.lang.reflect.Method
import java.util.UUID

class BleConnectWorker(
    address: String,
    private val runtimeChecker: RuntimeChecker,
) : Handler.Callback, IBleConnectWorker, IBluetoothGattResponse, ProxyInterceptor, RuntimeChecker {
    private var bleGattProfile: BleGattProfile? = null
    private val bluetoothDevice: BluetoothDevice
    private var bluetoothGatt: BluetoothGatt? = null
    private val bluetoothGattResponse: IBluetoothGattResponse
    @Volatile
    private var connectStatus = 0
    private val deviceProfile = HashMap<UUID, MutableMap<UUID, BluetoothGattCharacteristic>>()
    private var gattResponseListener: GattResponseListener? = null
    private val workerHandler: Handler

    init {
        val bluetoothAdapter: BluetoothAdapter =
            BluetoothUtils.getBluetoothAdapter() ?: throw IllegalStateException("ble adapter null")
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
        workerHandler = Handler(checkNotNull(Looper.myLooper()), this)
        @Suppress("UNCHECKED_CAST")
        bluetoothGattResponse =
            ProxyUtils.getProxy(this, IBluetoothGattResponse::class.java, this) as IBluetoothGattResponse
    }

    private fun refreshServiceProfile() {
        BluetoothLog.v(String.format("refreshServiceProfile for %s", bluetoothDevice.address))
        val services = bluetoothGatt?.services ?: emptyList()
        val refreshedProfile = HashMap<UUID, MutableMap<UUID, BluetoothGattCharacteristic>>()
        for (service in services) {
            val serviceUuid = service.uuid
            val characters = refreshedProfile.getOrPut(serviceUuid) {
                BluetoothLog.v("Service: $serviceUuid")
                HashMap()
            }
            for (characteristic in service.characteristics) {
                BluetoothLog.v("character: uuid = ${characteristic.uuid}")
                characters[characteristic.uuid] = characteristic
            }
        }
        deviceProfile.clear()
        deviceProfile.putAll(refreshedProfile)
        bleGattProfile = BleGattProfile(deviceProfile)
    }

    private fun getCharacter(serviceUuid: UUID?, characterUuid: UUID?): BluetoothGattCharacteristic? {
        val cachedCharacteristic = if (serviceUuid == null || characterUuid == null) {
            null
        } else {
            deviceProfile[serviceUuid]?.get(characterUuid)
        }
        if (cachedCharacteristic != null) {
            return cachedCharacteristic
        }
        val gattService: BluetoothGattService = bluetoothGatt?.getService(serviceUuid) ?: return null
        return gattService.getCharacteristic(characterUuid)
    }

    private fun setConnectStatus(status: Int) {
        BluetoothLog.v(String.format("setConnectStatus status = %s", Constants.getStatusText(status)))
        connectStatus = status
    }

    override fun onConnectionStateChange(status: Int, newState: Int) {
        checkRuntime()
        BluetoothLog.v(
            String.format(
                "onConnectionStateChange for %s: status = %d, newState = %d",
                bluetoothDevice.address,
                status,
                newState,
            ),
        )
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfileCompat.STATE_CONNECTED) {
            setConnectStatus(Constants.STATUS_DEVICE_CONNECTED)
            gattResponseListener?.onConnectStatusChanged(true)
            return
        }
        closeGatt()
    }

    override fun onServicesDiscovered(status: Int) {
        checkRuntime()
        BluetoothLog.v(String.format("onServicesDiscovered for %s: status = %d", bluetoothDevice.address, status))
        if (status == BluetoothGatt.GATT_SUCCESS) {
            setConnectStatus(Constants.STATUS_DEVICE_SERVICE_READY)
            broadcastConnectStatus(Constants.STATUS_CONNECTED)
            refreshServiceProfile()
        }
        val listener = gattResponseListener
        if (listener is ServiceDiscoverListener) {
            listener.onServicesDiscovered(status, bleGattProfile ?: BleGattProfile(emptyMap()))
        }
    }

    override fun onCharacteristicRead(
        characteristic: BluetoothGattCharacteristic,
        status: Int,
        value: ByteArray,
    ) {
        checkRuntime()
        BluetoothLog.v(
            String.format(
                "onCharacteristicRead for %s: status = %d, service = 0x%s, character = 0x%s, value = %s",
                bluetoothDevice.address,
                status,
                characteristic.service.uuid,
                characteristic.uuid,
                ByteUtils.byteToString(value),
            ),
        )
        val listener = gattResponseListener
        if (listener is ReadCharacterListener) {
            listener.onCharacteristicRead(characteristic, status, value)
        }
    }

    override fun onCharacteristicWrite(
        characteristic: BluetoothGattCharacteristic,
        status: Int,
        value: ByteArray,
    ) {
        checkRuntime()
        BluetoothLog.v(
            String.format(
                "onCharacteristicWrite for %s: status = %d, service = 0x%s, character = 0x%s, value = %s",
                bluetoothDevice.address,
                status,
                characteristic.service.uuid,
                characteristic.uuid,
                ByteUtils.byteToString(value),
            ),
        )
        val listener = gattResponseListener
        if (listener is WriteCharacterListener) {
            listener.onCharacteristicWrite(characteristic, status, value)
        }
    }

    override fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
        checkRuntime()
        BluetoothLog.v(
            String.format(
                "onCharacteristicChanged for %s: value = %s, service = 0x%s, character = 0x%s",
                bluetoothDevice.address,
                ByteUtils.byteToString(value),
                characteristic.service.uuid,
                characteristic.uuid,
            ),
        )
        broadcastCharacterChanged(characteristic.service.uuid, characteristic.uuid, value)
    }

    override fun onDescriptorRead(descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray) {
        checkRuntime()
        BluetoothLog.v(
            String.format(
                "onDescriptorRead for %s: status = %d, service = 0x%s, character = 0x%s, descriptor = 0x%s",
                bluetoothDevice.address,
                status,
                descriptor.characteristic.service.uuid,
                descriptor.characteristic.uuid,
                descriptor.uuid,
            ),
        )
        val listener = gattResponseListener
        if (listener is ReadDescriptorListener) {
            listener.onDescriptorRead(descriptor, status, value)
        }
    }

    override fun onDescriptorWrite(descriptor: BluetoothGattDescriptor, status: Int) {
        checkRuntime()
        BluetoothLog.v(
            String.format(
                "onDescriptorWrite for %s: status = %d, service = 0x%s, character = 0x%s, descriptor = 0x%s",
                bluetoothDevice.address,
                status,
                descriptor.characteristic.service.uuid,
                descriptor.characteristic.uuid,
                descriptor.uuid,
            ),
        )
        val listener = gattResponseListener
        if (listener is WriteDescriptorListener) {
            listener.onDescriptorWrite(descriptor, status)
        }
    }

    override fun onReadRemoteRssi(rssi: Int, status: Int) {
        checkRuntime()
        BluetoothLog.v(
            String.format("onReadRemoteRssi for %s, rssi = %d, status = %d", bluetoothDevice.address, rssi, status),
        )
        val listener = gattResponseListener
        if (listener is ReadRssiListener) {
            listener.onReadRemoteRssi(rssi, status)
        }
    }

    private fun broadcastConnectStatus(status: Int) {
        val intent = Intent(Constants.ACTION_CONNECT_STATUS_CHANGED)
        intent.putExtra(Constants.EXTRA_MAC, bluetoothDevice.address)
        intent.putExtra(Constants.EXTRA_STATUS, status)
        BluetoothUtils.sendBroadcast(intent)
    }

    private fun broadcastCharacterChanged(serviceUuid: UUID, characterUuid: UUID, value: ByteArray) {
        val intent = Intent(Constants.ACTION_CHARACTER_CHANGED)
        intent.putExtra(Constants.EXTRA_MAC, bluetoothDevice.address)
        intent.putExtra(Constants.EXTRA_SERVICE_UUID, serviceUuid)
        intent.putExtra(Constants.EXTRA_CHARACTER_UUID, characterUuid)
        intent.putExtra(Constants.EXTRA_BYTE_VALUE, value)
        BluetoothUtils.sendBroadcast(intent)
    }

    override fun openGatt(): Boolean {
        checkRuntime()
        BluetoothLog.v(String.format("openGatt for %s", bluetoothDevice.address))
        if (bluetoothGatt != null) {
            BluetoothLog.e(String.format("Previous gatt not closed"))
            return true
        }
        val callback = BluetoothGattResponse(bluetoothGattResponse)
        val context = BluetoothUtils.getContext()
        bluetoothGatt =
            if (Version.isMarshmallow()) {
                bluetoothDevice.connectGatt(context, false, callback, BluetoothDevice.TRANSPORT_LE)
            } else {
                @Suppress("DEPRECATION")
                bluetoothDevice.connectGatt(context, false, callback)
            }
        if (bluetoothGatt != null) {
            return true
        }
        BluetoothLog.e(String.format("openGatt failed: connectGatt return null!"))
        return false
    }

    private fun writeCharacteristicCompat(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        writeType: Int,
    ): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            val result = gatt.writeCharacteristic(characteristic, value, writeType)
            if (result == BluetoothStatusCompat.SUCCESS) {
                return true
            }
            BluetoothLog.e(String.format("writeCharacteristic returned %d", result))
            return false
        }
        @Suppress("DEPRECATION")
        characteristic.value = value
        characteristic.writeType = writeType
        @Suppress("DEPRECATION")
        return gatt.writeCharacteristic(characteristic)
    }

    private fun writeDescriptorCompat(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        value: ByteArray,
    ): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            val result = gatt.writeDescriptor(descriptor, value)
            if (result == BluetoothStatusCompat.SUCCESS) {
                return true
            }
            BluetoothLog.e(String.format("writeDescriptor returned %d", result))
            return false
        }
        @Suppress("DEPRECATION")
        if (!descriptor.setValue(value)) {
            return false
        }
        @Suppress("DEPRECATION")
        return gatt.writeDescriptor(descriptor)
    }

    override fun closeGatt() {
        checkRuntime()
        BluetoothLog.v(String.format("closeGatt for %s", bluetoothDevice.address))
        bluetoothGatt?.close()
        bluetoothGatt = null
        gattResponseListener?.onConnectStatusChanged(false)
        setConnectStatus(Constants.STATUS_DEVICE_DISCONNECTED)
        broadcastConnectStatus(Constants.STATUS_DISCONNECTED)
    }

    override fun discoverService(): Boolean {
        checkRuntime()
        BluetoothLog.v(String.format("discoverService for %s", bluetoothDevice.address))
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("discoverService but gatt is null!"))
            return false
        }
        if (gatt.discoverServices()) {
            return true
        }
        BluetoothLog.e(String.format("discoverServices failed"))
        return false
    }

    override fun getCurrentStatus(): Int {
        checkRuntime()
        return connectStatus
    }

    override fun registerGattResponseListener(gattResponseListener: GattResponseListener?) {
        checkRuntime()
        this.gattResponseListener = gattResponseListener
    }

    override fun clearGattResponseListener(gattResponseListener: GattResponseListener?) {
        checkRuntime()
        if (this.gattResponseListener == gattResponseListener) {
            this.gattResponseListener = null
        }
    }

    override fun refreshDeviceCache(): Boolean {
        BluetoothLog.v(String.format("refreshDeviceCache for %s", bluetoothDevice.address))
        checkRuntime()
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("ble gatt null"))
            return false
        }
        if (BluetoothUtils.refreshGattCache(gatt)) {
            return true
        }
        BluetoothLog.e(String.format("refreshDeviceCache failed"))
        return false
    }

    override fun readCharacteristic(service: UUID?, character: UUID?): Boolean {
        BluetoothLog.v(
            String.format(
                "readCharacteristic for %s: service = 0x%s, character = 0x%s",
                bluetoothDevice.address,
                service,
                character,
            ),
        )
        checkRuntime()
        val targetCharacteristic = getCharacter(service, character)
        if (targetCharacteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"))
            return false
        }
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("ble gatt null"))
            return false
        }
        if (gatt.readCharacteristic(targetCharacteristic)) {
            return true
        }
        BluetoothLog.e(String.format("readCharacteristic failed"))
        return false
    }

    override fun writeCharacteristic(service: UUID?, character: UUID?, value: ByteArray?): Boolean {
        BluetoothLog.v(
            String.format(
                "writeCharacteristic for %s: service = 0x%s, character = 0x%s, value = 0x%s",
                bluetoothDevice.address,
                service,
                character,
                ByteUtils.byteToString(value),
            ),
        )
        checkRuntime()
        val targetCharacteristic = getCharacter(service, character)
        if (targetCharacteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"))
            return false
        }
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("ble gatt null"))
            return false
        }
        if (writeCharacteristicCompat(gatt, targetCharacteristic, value ?: ByteUtils.EMPTY_BYTES, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)) {
            return true
        }
        BluetoothLog.e(String.format("writeCharacteristic failed"))
        return false
    }

    override fun readDescriptor(service: UUID?, character: UUID?, descriptor: UUID?): Boolean {
        BluetoothLog.v(
            String.format(
                "readDescriptor for %s: service = 0x%s, character = 0x%s, descriptor = 0x%s",
                bluetoothDevice.address,
                service,
                character,
                descriptor,
            ),
        )
        checkRuntime()
        val targetCharacteristic = getCharacter(service, character)
        if (targetCharacteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"))
            return false
        }
        val targetDescriptor = targetCharacteristic.getDescriptor(descriptor)
        if (targetDescriptor == null) {
            BluetoothLog.e(String.format("descriptor not exist"))
            return false
        }
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("ble gatt null"))
            return false
        }
        if (gatt.readDescriptor(targetDescriptor)) {
            return true
        }
        BluetoothLog.e(String.format("readDescriptor failed"))
        return false
    }

    override fun writeDescriptor(service: UUID?, character: UUID?, descriptor: UUID?, value: ByteArray?): Boolean {
        BluetoothLog.v(
            String.format(
                "writeDescriptor for %s: service = 0x%s, character = 0x%s, descriptor = 0x%s, value = 0x%s",
                bluetoothDevice.address,
                service,
                character,
                descriptor,
                ByteUtils.byteToString(value),
            ),
        )
        checkRuntime()
        val targetCharacteristic = getCharacter(service, character)
        if (targetCharacteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"))
            return false
        }
        val targetDescriptor = targetCharacteristic.getDescriptor(descriptor)
        if (targetDescriptor == null) {
            BluetoothLog.e(String.format("descriptor not exist"))
            return false
        }
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("ble gatt null"))
            return false
        }
        if (writeDescriptorCompat(gatt, targetDescriptor, value ?: ByteUtils.EMPTY_BYTES)) {
            return true
        }
        BluetoothLog.e(String.format("writeDescriptor failed"))
        return false
    }

    override fun writeCharacteristicWithNoRsp(service: UUID?, character: UUID?, value: ByteArray?): Boolean {
        BluetoothLog.v(
            String.format(
                "writeCharacteristicWithNoRsp for %s: service = 0x%s, character = 0x%s, value = 0x%s",
                bluetoothDevice.address,
                service,
                character,
                ByteUtils.byteToString(value),
            ),
        )
        checkRuntime()
        val targetCharacteristic = getCharacter(service, character)
        if (targetCharacteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"))
            return false
        }
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("ble gatt null"))
            return false
        }
        if (
            writeCharacteristicCompat(
                gatt,
                targetCharacteristic,
                value ?: ByteUtils.EMPTY_BYTES,
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE,
            )
        ) {
            return true
        }
        BluetoothLog.e(String.format("writeCharacteristic failed"))
        return false
    }

    override fun setCharacteristicNotification(service: UUID?, character: UUID?, enable: Boolean): Boolean {
        checkRuntime()
        BluetoothLog.v(
            String.format(
                "setCharacteristicNotification for %s, service = %s, character = %s, enable = %b",
                bluetoothDevice.address,
                service,
                character,
                enable,
            ),
        )
        val targetCharacteristic = getCharacter(service, character)
        if (targetCharacteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"))
            return false
        }
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("ble gatt null"))
            return false
        }
        if (!gatt.setCharacteristicNotification(targetCharacteristic, enable)) {
            BluetoothLog.e(String.format("setCharacteristicNotification failed"))
            return false
        }
        val descriptor = targetCharacteristic.getDescriptor(Constants.CLIENT_CHARACTERISTIC_CONFIG)
        if (descriptor == null) {
            BluetoothLog.e(String.format("getDescriptor for notify null!"))
            return false
        }
        if (
            writeDescriptorCompat(
                gatt,
                descriptor,
                if (enable) {
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                } else {
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                },
            )
        ) {
            return true
        }
        BluetoothLog.e(String.format("writeDescriptor for notify failed"))
        return false
    }

    override fun setCharacteristicIndication(service: UUID?, character: UUID?, enable: Boolean): Boolean {
        checkRuntime()
        BluetoothLog.v(
            String.format(
                "setCharacteristicIndication for %s, service = %s, character = %s, enable = %b",
                bluetoothDevice.address,
                service,
                character,
                enable,
            ),
        )
        val targetCharacteristic = getCharacter(service, character)
        if (targetCharacteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"))
            return false
        }
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("ble gatt null"))
            return false
        }
        if (!gatt.setCharacteristicNotification(targetCharacteristic, enable)) {
            BluetoothLog.e(String.format("setCharacteristicIndication failed"))
            return false
        }
        val descriptor = targetCharacteristic.getDescriptor(Constants.CLIENT_CHARACTERISTIC_CONFIG)
        if (descriptor == null) {
            BluetoothLog.e(String.format("getDescriptor for indicate null!"))
            return false
        }
        if (
            writeDescriptorCompat(
                gatt,
                descriptor,
                if (enable) {
                    BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                } else {
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                },
            )
        ) {
            return true
        }
        BluetoothLog.e(String.format("writeDescriptor for indicate failed"))
        return false
    }

    override fun readRemoteRssi(): Boolean {
        checkRuntime()
        BluetoothLog.v(String.format("readRemoteRssi for %s", bluetoothDevice.address))
        val gatt = bluetoothGatt
        if (gatt == null) {
            BluetoothLog.e(String.format("ble gatt null"))
            return false
        }
        if (gatt.readRemoteRssi()) {
            return true
        }
        BluetoothLog.e(String.format("readRemoteRssi failed"))
        return false
    }

    override fun getGattProfile(): BleGattProfile? = bleGattProfile

    override fun handleMessage(message: Message): Boolean {
        if (message.what == MSG_GATT_RESPONSE) {
            ProxyBulk.safeInvoke(message.obj)
        }
        return true
    }

    override fun onIntercept(obj: Any?, method: Method, objArr: Array<Any?>?): Boolean {
        workerHandler.obtainMessage(MSG_GATT_RESPONSE, ProxyBulk(obj, method, objArr)).sendToTarget()
        return true
    }

    override fun checkRuntime() {
        runtimeChecker.checkRuntime()
    }

    private object BluetoothProfileCompat {
        const val STATE_CONNECTED = 2
    }

    private object BluetoothStatusCompat {
        const val SUCCESS = 0
    }

    companion object {
        private const val MSG_GATT_RESPONSE = 288
    }
}
