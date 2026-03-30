package com.inuker.bluetooth.library

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.RemoteException
import com.inuker.bluetooth.library.connect.BleConnectManager
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import com.inuker.bluetooth.library.search.BluetoothSearchManager
import com.inuker.bluetooth.library.search.SearchRequest
import java.util.UUID

class BluetoothServiceImpl private constructor() : IBluetoothService.Stub(), Handler.Callback {
    private val handler = Handler(Looper.getMainLooper(), this)

    @Throws(RemoteException::class)
    override fun callBluetoothApi(type: Int, bundle: Bundle?, response: IResponse?) {
        val requestBundle = bundle ?: Bundle()
        requestBundle.classLoader = javaClass.classLoader
        val message =
            handler.obtainMessage(
                type,
                object : BleGeneralResponse {
                    override fun onResponse(code: Int, data: Bundle?) {
                        if (response == null) {
                            return
                        }
                        val safeData = data ?: Bundle()
                        try {
                            response.onResponse(code, safeData)
                        } catch (exception: RemoteException) {
                            exception.printStackTrace()
                        }
                    }
                },
            )
        message.data = requestBundle
        message.sendToTarget()
    }

    override fun handleMessage(message: Message): Boolean {
        val data = message.data
        val address = data.getString(Constants.EXTRA_MAC)
        val serviceUuid = data.getSerializable(Constants.EXTRA_SERVICE_UUID) as? UUID
        val characterUuid = data.getSerializable(Constants.EXTRA_CHARACTER_UUID) as? UUID
        val descriptorUuid = data.getSerializable(Constants.EXTRA_DESCRIPTOR_UUID) as? UUID
        val value = data.getByteArray(Constants.EXTRA_BYTE_VALUE)
        val bleGeneralResponse = message.obj as? BleGeneralResponse
        when (message.what) {
            1 -> {
                val safeAddress = address ?: return true
                BleConnectManager.connect(
                    safeAddress,
                    data.getParcelable(Constants.EXTRA_OPTIONS) as? BleConnectOptions,
                    bleGeneralResponse,
                )
            }

            2 -> BleConnectManager.disconnect(address ?: return true)
            3 -> BleConnectManager.read(address ?: return true, serviceUuid, characterUuid, bleGeneralResponse)
            4 -> BleConnectManager.write(address ?: return true, serviceUuid, characterUuid, value, bleGeneralResponse)
            5 -> BleConnectManager.writeNoRsp(address ?: return true, serviceUuid, characterUuid, value, bleGeneralResponse)
            6 -> BleConnectManager.notify(address ?: return true, serviceUuid, characterUuid, bleGeneralResponse)
            7 -> BleConnectManager.unnotify(address ?: return true, serviceUuid, characterUuid, bleGeneralResponse)
            8 -> BleConnectManager.readRssi(address ?: return true, bleGeneralResponse)
            10 -> BleConnectManager.indicate(address ?: return true, serviceUuid, characterUuid, bleGeneralResponse)
            11 -> BluetoothSearchManager.search(
                data.getParcelable(Constants.EXTRA_REQUEST) as? SearchRequest ?: return true,
                bleGeneralResponse ?: return true,
            )

            12 -> BluetoothSearchManager.stopSearch()
            13 -> BleConnectManager.readDescriptor(
                address ?: return true,
                serviceUuid,
                characterUuid,
                descriptorUuid,
                bleGeneralResponse,
            )

            14 -> BleConnectManager.writeDescriptor(
                address ?: return true,
                serviceUuid,
                characterUuid,
                descriptorUuid,
                value,
                bleGeneralResponse,
            )

            20 -> BleConnectManager.clearRequest(
                address ?: return true,
                data.getInt(Constants.EXTRA_TYPE, 0),
            )

            21 -> BleConnectManager.refreshCache(address ?: return true)
        }
        return true
    }

    companion object {
        @Volatile
        private var instance: BluetoothServiceImpl? = null

        @JvmStatic
        fun getInstance(): BluetoothServiceImpl {
            if (instance == null) {
                synchronized(BluetoothServiceImpl::class.java) {
                    if (instance == null) {
                        instance = BluetoothServiceImpl()
                    }
                }
            }
            return instance!!
        }
    }
}
