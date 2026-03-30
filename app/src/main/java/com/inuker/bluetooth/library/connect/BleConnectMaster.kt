package com.inuker.bluetooth.library.connect

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils
import java.lang.reflect.Method
import java.util.UUID

class BleConnectMaster private constructor(
    private val address: String,
    looper: Looper,
) : IBleConnectMaster, ProxyInterceptor, Handler.Callback {
    private var bleConnectDispatcher: BleConnectDispatcher? = null
    private val handler = Handler(looper, this)

    private fun getConnectDispatcher(): BleConnectDispatcher {
        if (bleConnectDispatcher == null) {
            bleConnectDispatcher = BleConnectDispatcher.newInstance(address)
        }
        return bleConnectDispatcher!!
    }

    override fun connect(options: BleConnectOptions?, response: BleGeneralResponse?) {
        getConnectDispatcher().connect(options, response)
    }

    override fun disconnect() {
        getConnectDispatcher().disconnect()
    }

    override fun read(service: UUID?, character: UUID?, response: BleGeneralResponse?) {
        getConnectDispatcher().read(service, character, response)
    }

    override fun write(service: UUID?, character: UUID?, value: ByteArray?, response: BleGeneralResponse?) {
        getConnectDispatcher().write(service, character, value, response)
    }

    override fun writeNoRsp(service: UUID?, character: UUID?, value: ByteArray?, response: BleGeneralResponse?) {
        getConnectDispatcher().writeNoRsp(service, character, value, response)
    }

    override fun readDescriptor(service: UUID?, character: UUID?, descriptor: UUID?, response: BleGeneralResponse?) {
        getConnectDispatcher().readDescriptor(service, character, descriptor, response)
    }

    override fun writeDescriptor(
        service: UUID?,
        character: UUID?,
        descriptor: UUID?,
        value: ByteArray?,
        response: BleGeneralResponse?,
    ) {
        getConnectDispatcher().writeDescriptor(service, character, descriptor, value, response)
    }

    override fun notify(service: UUID?, character: UUID?, response: BleGeneralResponse?) {
        getConnectDispatcher().notify(service, character, response)
    }

    override fun unnotify(service: UUID?, character: UUID?, response: BleGeneralResponse?) {
        getConnectDispatcher().unnotify(service, character, response)
    }

    override fun readRssi(response: BleGeneralResponse?) {
        getConnectDispatcher().readRemoteRssi(response)
    }

    override fun indicate(service: UUID?, character: UUID?, response: BleGeneralResponse?) {
        getConnectDispatcher().indicate(service, character, response)
    }

    override fun clearRequest(type: Int) {
        getConnectDispatcher().clearRequest(type)
    }

    override fun refreshCache() {
        getConnectDispatcher().refreshCache()
    }

    override fun onIntercept(obj: Any?, method: Method, objArr: Array<Any?>?): Boolean {
        handler.obtainMessage(0, ProxyBulk(obj, method, objArr)).sendToTarget()
        return true
    }

    override fun handleMessage(message: Message): Boolean {
        ProxyBulk.safeInvoke(message.obj)
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance(address: String, looper: Looper): IBleConnectMaster {
            val master = BleConnectMaster(address, looper)
            return ProxyUtils.getProxy(master, IBleConnectMaster::class.java, master) as IBleConnectMaster
        }
    }
}
