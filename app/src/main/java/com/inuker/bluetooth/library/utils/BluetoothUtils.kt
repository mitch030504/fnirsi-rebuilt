package com.inuker.bluetooth.library.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import android.os.Handler
import android.os.Looper
import com.inuker.bluetooth.library.BluetoothContext

object BluetoothUtils {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothManager: BluetoothManager? = null
    private var handler: Handler? = null

    @JvmStatic
    fun getContext(): Context = BluetoothContext.get()

    private fun getHandler(): Handler {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        return handler!!
    }

    @JvmStatic
    fun post(runnable: Runnable) {
        getHandler().post(runnable)
    }

    @JvmStatic
    fun registerReceiver(broadcastReceiver: BroadcastReceiver, intentFilter: IntentFilter) {
        registerGlobalReceiver(broadcastReceiver, intentFilter)
    }

    private fun registerGlobalReceiver(broadcastReceiver: BroadcastReceiver, intentFilter: IntentFilter) {
        ContextCompat.registerReceiver(
            getContext(),
            broadcastReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    @JvmStatic
    fun unregisterReceiver(broadcastReceiver: BroadcastReceiver) {
        getContext().unregisterReceiver(broadcastReceiver)
    }

    @JvmStatic
    fun sendBroadcast(intent: Intent) {
        sendGlobalBroadcast(intent)
    }

    @JvmStatic
    fun sendBroadcast(action: String) {
        sendGlobalBroadcast(Intent(action))
    }

    private fun sendGlobalBroadcast(intent: Intent) {
        val context = getContext()
        intent.`package` = context.packageName
        context.sendBroadcast(intent)
    }

    @JvmStatic
    fun isBleSupported(): Boolean {
        val context = getContext()
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
            context.packageManager.hasSystemFeature("android.hardware.bluetooth_le")
    }

    @JvmStatic
    fun isBluetoothEnabled(): Boolean = getBluetoothState() == BluetoothAdapter.STATE_ON

    @JvmStatic
    fun getBluetoothState(): Int = getBluetoothAdapter()?.state ?: BluetoothAdapter.ERROR

    @JvmStatic
    fun openBluetooth(): Boolean = getBluetoothAdapter()?.enable() ?: false

    @JvmStatic
    fun closeBluetooth(): Boolean = getBluetoothAdapter()?.disable() ?: false

    @JvmStatic
    fun getBluetoothManager(): BluetoothManager? {
        if (!isBleSupported()) {
            return null
        }
        if (bluetoothManager == null) {
            bluetoothManager = getContext().getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        }
        return bluetoothManager
    }

    @JvmStatic
    fun getBluetoothAdapter(): BluetoothAdapter? {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        }
        return bluetoothAdapter
    }

    @JvmStatic
    fun getRemoteDevice(address: String?): BluetoothDevice? {
        if (address.isNullOrEmpty()) {
            return null
        }
        val adapter = getBluetoothAdapter() ?: return null
        return try {
            adapter.getRemoteDevice(address)
        } catch (throwable: Throwable) {
            BluetoothLog.e(throwable)
            null
        }
    }

    @JvmStatic
    fun getConnectedBluetoothLeDevices(): List<BluetoothDevice> {
        val devices = ArrayList<BluetoothDevice>()
        getBluetoothManager()?.let { devices.addAll(it.getConnectedDevices(BluetoothProfile.GATT)) }
        return devices
    }

    @JvmStatic
    fun getConnectStatus(address: String?): Int {
        val manager = getBluetoothManager() ?: return -1
        val device = getRemoteDevice(address) ?: return -1
        return try {
            manager.getConnectionState(device, BluetoothProfile.GATT)
        } catch (throwable: Throwable) {
            BluetoothLog.e(throwable)
            -1
        }
    }

    @JvmStatic
    fun getBondState(address: String?): Int {
        if (getBluetoothManager() == null) {
            return BluetoothDevice.BOND_NONE
        }
        return try {
            getRemoteDevice(address)?.bondState ?: BluetoothDevice.BOND_NONE
        } catch (throwable: Throwable) {
            BluetoothLog.e(throwable)
            BluetoothDevice.BOND_NONE
        }
    }

    @JvmStatic
    fun getBondedBluetoothClassicDevices(): List<BluetoothDevice> {
        val devices = ArrayList<BluetoothDevice>()
        val bondedDevices = getBluetoothAdapter()?.bondedDevices
        if (bondedDevices != null) {
            devices.addAll(bondedDevices)
        }
        return devices
    }

    @JvmStatic
    fun isDeviceConnected(address: String?): Boolean {
        if (address.isNullOrEmpty() || !isBleSupported()) {
            return false
        }
        val manager = getBluetoothManager() ?: return false
        val device = getRemoteDevice(address) ?: return false
        return manager.getConnectionState(device, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED
    }

    @JvmStatic
    fun checkMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

    @JvmStatic
    fun refreshGattCache(bluetoothGatt: BluetoothGatt?): Boolean {
        var refreshed = false
        if (bluetoothGatt != null) {
            try {
                val method = BluetoothGatt::class.java.getMethod("refresh")
                method.isAccessible = true
                refreshed = (method.invoke(bluetoothGatt) as? Boolean) == true
            } catch (exception: Exception) {
                BluetoothLog.e(exception)
            }
        }
        BluetoothLog.v(String.format("refreshDeviceCache return %b", refreshed))
        return refreshed
    }
}
