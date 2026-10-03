package com.inuker.bluetooth.library

import android.content.Context
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleConnectResponse
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleReadResponse
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.inuker.bluetooth.library.utils.BluetoothLog
import com.inuker.bluetooth.library.utils.BluetoothUtils
import com.inuker.bluetooth.library.utils.ByteUtils
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils
import java.util.UUID

class BluetoothClient(context: Context) : IBluetoothClient {
    private val client: IBluetoothClient

    init {
        requireNotNull(context) { "Context null" }
        client = BluetoothClientImpl.getInstance(context)
    }

    fun connect(address: String?, response: BleConnectResponse) {
        connect(address, null, response)
    }

    override fun connect(address: String?, bleConnectOptions: BleConnectOptions?, bleConnectResponse: BleConnectResponse?) {
        BluetoothLog.v(String.format("connect %s", address))
        client.connect(address, bleConnectOptions, uiProxyOrNull(bleConnectResponse))
    }

    override fun disconnect(address: String?) {
        BluetoothLog.v(String.format("disconnect %s", address))
        client.disconnect(address)
    }

    override fun read(address: String?, service: UUID?, character: UUID?, response: BleReadResponse?) {
        BluetoothLog.v(String.format("read character for %s: service = %s, character = %s", address, service, character))
        client.read(address, service, character, uiProxyOrNull(response))
    }

    override fun write(address: String?, service: UUID?, character: UUID?, value: ByteArray?, response: BleWriteResponse?) {
        BluetoothLog.v(
            String.format(
                "write character for %s: service = %s, character = %s, value = %s",
                address,
                service,
                character,
                ByteUtils.byteToString(value),
            ),
        )
        client.write(address, service, character, value, uiProxyOrNull(response))
    }

    override fun readDescriptor(address: String?, service: UUID?, character: UUID?, descriptor: UUID?, response: BleReadResponse?) {
        BluetoothLog.v(String.format("readDescriptor for %s: service = %s, character = %s", address, service, character))
        client.readDescriptor(address, service, character, descriptor, uiProxyOrNull(response))
    }

    override fun writeDescriptor(
        address: String?,
        service: UUID?,
        character: UUID?,
        descriptor: UUID?,
        value: ByteArray?,
        response: BleWriteResponse?,
    ) {
        BluetoothLog.v(String.format("writeDescriptor for %s: service = %s, character = %s", address, service, character))
        client.writeDescriptor(address, service, character, descriptor, value, uiProxyOrNull(response))
    }

    override fun writeNoRsp(address: String?, service: UUID?, character: UUID?, value: ByteArray?, response: BleWriteResponse?) {
        BluetoothLog.v(
            String.format(
                "writeNoRsp %s: service = %s, character = %s, value = %s",
                address,
                service,
                character,
                ByteUtils.byteToString(value),
            ),
        )
        client.writeNoRsp(address, service, character, value, uiProxyOrNull(response))
    }

    override fun notify(address: String?, service: UUID?, character: UUID?, response: BleNotifyResponse?) {
        BluetoothLog.v(String.format("notify %s: service = %s, character = %s", address, service, character))
        client.notify(address, service, character, uiProxyOrNull(response))
    }

    override fun unnotify(address: String?, service: UUID?, character: UUID?, response: BleUnnotifyResponse?) {
        BluetoothLog.v(String.format("unnotify %s: service = %s, character = %s", address, service, character))
        client.unnotify(address, service, character, uiProxyOrNull(response))
    }

    override fun indicate(address: String?, service: UUID?, character: UUID?, response: BleNotifyResponse?) {
        BluetoothLog.v(String.format("indicate %s: service = %s, character = %s", address, service, character))
        client.indicate(address, service, character, uiProxyOrNull(response))
    }

    override fun unindicate(address: String?, service: UUID?, character: UUID?, response: BleUnnotifyResponse?) {
        BluetoothLog.v(String.format("unindicate %s: service = %s, character = %s", address, service, character))
        client.unindicate(address, service, character, uiProxyOrNull(response))
    }

    override fun readRssi(address: String?, response: BleReadRssiResponse?) {
        BluetoothLog.v(String.format("readRssi %s", address))
        client.readRssi(address, uiProxyOrNull(response))
    }

    override fun search(searchRequest: SearchRequest?, searchResponse: SearchResponse?) {
        BluetoothLog.v(String.format("search %s", searchRequest))
        client.search(searchRequest, uiProxyOrNull(searchResponse))
    }

    override fun stopSearch() {
        BluetoothLog.v("stopSearch")
        client.stopSearch()
    }

    override fun registerConnectStatusListener(address: String?, listener: BleConnectStatusListener?) {
        client.registerConnectStatusListener(address, listener)
    }

    override fun unregisterConnectStatusListener(address: String?, listener: BleConnectStatusListener?) {
        client.unregisterConnectStatusListener(address, listener)
    }

    override fun registerBluetoothStateListener(listener: BluetoothStateListener?) {
        client.registerBluetoothStateListener(listener)
    }

    override fun unregisterBluetoothStateListener(listener: BluetoothStateListener?) {
        client.unregisterBluetoothStateListener(listener)
    }

    override fun registerBluetoothBondListener(listener: BluetoothBondListener?) {
        client.registerBluetoothBondListener(listener)
    }

    override fun unregisterBluetoothBondListener(listener: BluetoothBondListener?) {
        client.unregisterBluetoothBondListener(listener)
    }

    fun getConnectStatus(address: String?): Int = BluetoothUtils.getConnectStatus(address)

    fun isBluetoothOpened(): Boolean = BluetoothUtils.isBluetoothEnabled()

    fun openBluetooth(): Boolean = BluetoothUtils.openBluetooth()

    fun closeBluetooth(): Boolean = BluetoothUtils.closeBluetooth()

    fun isBleSupported(): Boolean = BluetoothUtils.isBleSupported()

    fun getBondState(address: String?): Int = BluetoothUtils.getBondState(address)

    override fun clearRequest(address: String?, type: Int) {
        client.clearRequest(address, type)
    }

    override fun refreshCache(address: String?) {
        client.refreshCache(address)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> uiProxy(value: T): T = ProxyUtils.getUIProxy(value) as T

    private fun <T : Any> uiProxyOrNull(value: T?): T? = value?.let(::uiProxy)
}
