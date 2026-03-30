package com.inuker.bluetooth.library.connect

import android.os.HandlerThread
import android.os.Looper
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import java.util.UUID

object BleConnectManager {
    private val tag = BleConnectManager::class.java.simpleName
    private val bleConnectMasters = HashMap<String, IBleConnectMaster>()
    private var workerThread: HandlerThread? = null

    private fun getWorkerLooper(): Looper {
        if (workerThread == null) {
            workerThread =
                HandlerThread(tag).apply {
                    start()
                }
        }
        return workerThread!!.looper
    }

    private fun getBleConnectMaster(address: String): IBleConnectMaster {
        return bleConnectMasters[address]
            ?: BleConnectMaster.newInstance(address, getWorkerLooper()).also {
                bleConnectMasters[address] = it
            }
    }

    @JvmStatic
    fun connect(address: String, options: BleConnectOptions?, response: BleGeneralResponse?) {
        getBleConnectMaster(address).connect(options, response)
    }

    @JvmStatic
    fun disconnect(address: String) {
        getBleConnectMaster(address).disconnect()
    }

    @JvmStatic
    fun read(address: String, service: UUID?, character: UUID?, response: BleGeneralResponse?) {
        getBleConnectMaster(address).read(service, character, response)
    }

    @JvmStatic
    fun write(address: String, service: UUID?, character: UUID?, value: ByteArray?, response: BleGeneralResponse?) {
        getBleConnectMaster(address).write(service, character, value, response)
    }

    @JvmStatic
    fun writeNoRsp(address: String, service: UUID?, character: UUID?, value: ByteArray?, response: BleGeneralResponse?) {
        getBleConnectMaster(address).writeNoRsp(service, character, value, response)
    }

    @JvmStatic
    fun readDescriptor(address: String, service: UUID?, character: UUID?, descriptor: UUID?, response: BleGeneralResponse?) {
        getBleConnectMaster(address).readDescriptor(service, character, descriptor, response)
    }

    @JvmStatic
    fun writeDescriptor(
        address: String,
        service: UUID?,
        character: UUID?,
        descriptor: UUID?,
        value: ByteArray?,
        response: BleGeneralResponse?,
    ) {
        getBleConnectMaster(address).writeDescriptor(service, character, descriptor, value, response)
    }

    @JvmStatic
    fun notify(address: String, service: UUID?, character: UUID?, response: BleGeneralResponse?) {
        getBleConnectMaster(address).notify(service, character, response)
    }

    @JvmStatic
    fun unnotify(address: String, service: UUID?, character: UUID?, response: BleGeneralResponse?) {
        getBleConnectMaster(address).unnotify(service, character, response)
    }

    @JvmStatic
    fun readRssi(address: String, response: BleGeneralResponse?) {
        getBleConnectMaster(address).readRssi(response)
    }

    @JvmStatic
    fun indicate(address: String, service: UUID?, character: UUID?, response: BleGeneralResponse?) {
        getBleConnectMaster(address).indicate(service, character, response)
    }

    @JvmStatic
    fun clearRequest(address: String, type: Int) {
        getBleConnectMaster(address).clearRequest(type)
    }

    @JvmStatic
    fun refreshCache(address: String) {
        getBleConnectMaster(address).refreshCache()
    }
}
