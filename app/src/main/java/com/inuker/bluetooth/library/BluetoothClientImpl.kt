package com.inuker.bluetooth.library

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleConnectResponse
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleReadResponse
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.inuker.bluetooth.library.connect.response.BluetoothResponse
import com.inuker.bluetooth.library.model.BleGattProfile
import com.inuker.bluetooth.library.receiver.BluetoothReceiver
import com.inuker.bluetooth.library.receiver.listener.BleCharacterChangeListener
import com.inuker.bluetooth.library.receiver.listener.BleConnectStatusChangeListener
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondStateChangeListener
import com.inuker.bluetooth.library.receiver.listener.BluetoothStateChangeListener
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.inuker.bluetooth.library.utils.BluetoothLog
import com.inuker.bluetooth.library.utils.ListUtils
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils
import java.lang.reflect.Method
import java.util.LinkedList
import java.util.UUID
import java.util.concurrent.CountDownLatch

class BluetoothClientImpl private constructor(context: Context) : IBluetoothClient, ProxyInterceptor, Handler.Callback {
    private var bluetoothService: IBluetoothService? = null
    private val connectStatusListeners = HashMap<String?, MutableList<BleConnectStatusListener>>()
    private val bluetoothStateListeners = LinkedList<BluetoothStateListener>()
    private val bluetoothBondListeners = LinkedList<BluetoothBondListener>()
    private val notifyResponses = HashMap<String?, HashMap<String, MutableList<BleNotifyResponse>>>()
    private val context: Context = context.applicationContext
    private var countDownLatch: CountDownLatch? = null
    private val workerThread: HandlerThread = HandlerThread(TAG)
    private val workerHandler: Handler
    private val connection =
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                bluetoothService = IBluetoothService.Stub.asInterface(service)
                notifyBluetoothManagerReady()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                bluetoothService = null
            }
        }

    init {
        BluetoothContext.set(this.context)
        workerThread.start()
        workerHandler = Handler(workerThread.looper, this)
        workerHandler.obtainMessage(MSG_REG_RECEIVER).sendToTarget()
    }

    private fun getBluetoothService(): IBluetoothService? {
        if (bluetoothService == null) {
            bindServiceSync()
        }
        return bluetoothService
    }

    private fun bindServiceSync() {
        checkRuntime(true)
        countDownLatch = CountDownLatch(1)
        val intent = Intent(context, BluetoothService::class.java)
        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            waitBluetoothManagerReady()
        } else {
            bluetoothService = BluetoothServiceImpl.getInstance()
        }
    }

    override fun connect(address: String?, bleConnectOptions: BleConnectOptions?, bleConnectResponse: BleConnectResponse?) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putParcelable(Constants.EXTRA_OPTIONS, bleConnectOptions)
            }
        safeCallBluetoothApi(
            1,
            bundle,
            object : BluetoothResponse() {
                @Suppress("DEPRECATION")
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    if (bleConnectResponse != null) {
                        val safeBundle = bundle ?: Bundle()
                        safeBundle.classLoader = javaClass.classLoader
                        bleConnectResponse.onResponse(
                            code,
                            safeBundle.getParcelable(Constants.EXTRA_GATT_PROFILE) as? BleGattProfile,
                        )
                    }
                }
            },
        )
    }

    override fun disconnect(address: String?) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
            }
        safeCallBluetoothApi(2, bundle, null)
        clearNotifyListener(address)
    }

    override fun registerConnectStatusListener(address: String?, bleConnectStatusListener: BleConnectStatusListener?) {
        checkRuntime(true)
        val listeners =
            connectStatusListeners.getOrPut(address) {
                ArrayList()
            }
        if (bleConnectStatusListener == null || listeners.contains(bleConnectStatusListener)) {
            return
        }
        listeners.add(bleConnectStatusListener)
    }

    override fun unregisterConnectStatusListener(address: String?, bleConnectStatusListener: BleConnectStatusListener?) {
        checkRuntime(true)
        val listeners = connectStatusListeners[address]
        if (bleConnectStatusListener == null || ListUtils.isEmpty(listeners)) {
            return
        }
        listeners?.remove(bleConnectStatusListener)
    }

    override fun read(address: String?, service: UUID?, character: UUID?, bleReadResponse: BleReadResponse?) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putSerializable(Constants.EXTRA_SERVICE_UUID, service)
                putSerializable(Constants.EXTRA_CHARACTER_UUID, character)
            }
        safeCallBluetoothApi(
            3,
            bundle,
            object : BluetoothResponse() {
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    bleReadResponse?.onResponse(code, bundle?.getByteArray(Constants.EXTRA_BYTE_VALUE))
                }
            },
        )
    }

    override fun write(
        address: String?,
        service: UUID?,
        character: UUID?,
        value: ByteArray?,
        bleWriteResponse: BleWriteResponse?,
    ) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putSerializable(Constants.EXTRA_SERVICE_UUID, service)
                putSerializable(Constants.EXTRA_CHARACTER_UUID, character)
                putByteArray(Constants.EXTRA_BYTE_VALUE, value)
            }
        safeCallBluetoothApi(
            4,
            bundle,
            object : BluetoothResponse() {
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    bleWriteResponse?.onResponse(code)
                }
            },
        )
    }

    override fun readDescriptor(
        address: String?,
        service: UUID?,
        character: UUID?,
        descriptor: UUID?,
        bleReadResponse: BleReadResponse?,
    ) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putSerializable(Constants.EXTRA_SERVICE_UUID, service)
                putSerializable(Constants.EXTRA_CHARACTER_UUID, character)
                putSerializable(Constants.EXTRA_DESCRIPTOR_UUID, descriptor)
            }
        safeCallBluetoothApi(
            13,
            bundle,
            object : BluetoothResponse() {
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    bleReadResponse?.onResponse(code, bundle?.getByteArray(Constants.EXTRA_BYTE_VALUE))
                }
            },
        )
    }

    override fun writeDescriptor(
        address: String?,
        service: UUID?,
        character: UUID?,
        descriptor: UUID?,
        value: ByteArray?,
        bleWriteResponse: BleWriteResponse?,
    ) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putSerializable(Constants.EXTRA_SERVICE_UUID, service)
                putSerializable(Constants.EXTRA_CHARACTER_UUID, character)
                putSerializable(Constants.EXTRA_DESCRIPTOR_UUID, descriptor)
                putByteArray(Constants.EXTRA_BYTE_VALUE, value)
            }
        safeCallBluetoothApi(
            14,
            bundle,
            object : BluetoothResponse() {
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    bleWriteResponse?.onResponse(code)
                }
            },
        )
    }

    override fun writeNoRsp(
        address: String?,
        service: UUID?,
        character: UUID?,
        value: ByteArray?,
        bleWriteResponse: BleWriteResponse?,
    ) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putSerializable(Constants.EXTRA_SERVICE_UUID, service)
                putSerializable(Constants.EXTRA_CHARACTER_UUID, character)
                putByteArray(Constants.EXTRA_BYTE_VALUE, value)
            }
        safeCallBluetoothApi(
            5,
            bundle,
            object : BluetoothResponse() {
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    bleWriteResponse?.onResponse(code)
                }
            },
        )
    }

    private fun saveNotifyListener(address: String?, service: UUID?, character: UUID?, bleNotifyResponse: BleNotifyResponse) {
        checkRuntime(true)
        val addressResponses =
            notifyResponses.getOrPut(address) {
                HashMap()
            }
        val characterKey = generateCharacterKey(service, character)
        val notifyListeners =
            addressResponses.getOrPut(characterKey) {
                ArrayList()
            }
        notifyListeners.add(bleNotifyResponse)
    }

    private fun removeNotifyListener(address: String?, service: UUID?, character: UUID?) {
        checkRuntime(true)
        notifyResponses[address]?.remove(generateCharacterKey(service, character))
    }

    private fun clearNotifyListener(address: String?) {
        checkRuntime(true)
        notifyResponses.remove(address)
    }

    private fun generateCharacterKey(service: UUID?, character: UUID?): String = String.format("%s_%s", service, character)

    override fun notify(address: String?, service: UUID?, character: UUID?, bleNotifyResponse: BleNotifyResponse?) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putSerializable(Constants.EXTRA_SERVICE_UUID, service)
                putSerializable(Constants.EXTRA_CHARACTER_UUID, character)
            }
        safeCallBluetoothApi(
            6,
            bundle,
            object : BluetoothResponse() {
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    if (bleNotifyResponse != null) {
                        if (code == Constants.REQUEST_SUCCESS) {
                            saveNotifyListener(address, service, character, bleNotifyResponse)
                        }
                        bleNotifyResponse.onResponse(code)
                    }
                }
            },
        )
    }

    override fun unnotify(address: String?, service: UUID?, character: UUID?, bleUnnotifyResponse: BleUnnotifyResponse?) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putSerializable(Constants.EXTRA_SERVICE_UUID, service)
                putSerializable(Constants.EXTRA_CHARACTER_UUID, character)
            }
        safeCallBluetoothApi(
            7,
            bundle,
            object : BluetoothResponse() {
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    removeNotifyListener(address, service, character)
                    bleUnnotifyResponse?.onResponse(code)
                }
            },
        )
    }

    override fun indicate(address: String?, service: UUID?, character: UUID?, bleNotifyResponse: BleNotifyResponse?) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putSerializable(Constants.EXTRA_SERVICE_UUID, service)
                putSerializable(Constants.EXTRA_CHARACTER_UUID, character)
            }
        safeCallBluetoothApi(
            10,
            bundle,
            object : BluetoothResponse() {
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    if (bleNotifyResponse != null) {
                        if (code == Constants.REQUEST_SUCCESS) {
                            saveNotifyListener(address, service, character, bleNotifyResponse)
                        }
                        bleNotifyResponse.onResponse(code)
                    }
                }
            },
        )
    }

    override fun unindicate(address: String?, service: UUID?, character: UUID?, bleUnnotifyResponse: BleUnnotifyResponse?) {
        unnotify(address, service, character, bleUnnotifyResponse)
    }

    override fun readRssi(address: String?, bleReadRssiResponse: BleReadRssiResponse?) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
            }
        safeCallBluetoothApi(
            8,
            bundle,
            object : BluetoothResponse() {
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    bleReadRssiResponse?.onResponse(code, bundle?.getInt(Constants.EXTRA_RSSI, 0))
                }
            },
        )
    }

    override fun search(searchRequest: SearchRequest?, searchResponse: SearchResponse?) {
        if (searchRequest == null) {
            return
        }
        val bundle =
            Bundle().apply {
                putParcelable(Constants.EXTRA_REQUEST, searchRequest)
            }
        safeCallBluetoothApi(
            11,
            bundle,
            object : BluetoothResponse() {
                @Suppress("DEPRECATION")
                override fun onAsyncResponse(code: Int, bundle: Bundle?) {
                    checkRuntime(true)
                    if (searchResponse == null) {
                        return
                    }
                    val safeBundle = bundle ?: Bundle()
                    safeBundle.classLoader = javaClass.classLoader
                    when (code) {
                        1 -> searchResponse.onSearchStarted()
                        2 -> searchResponse.onSearchStopped()
                        3 -> searchResponse.onSearchCanceled()
                        4 -> {
                            val result = safeBundle.getParcelable(Constants.EXTRA_SEARCH_RESULT) as? SearchResult ?: return
                            searchResponse.onDeviceFounded(result)
                        }

                        else -> throw IllegalStateException("unknown code")
                    }
                }
            },
        )
    }

    override fun stopSearch() {
        safeCallBluetoothApi(12, null, null)
    }

    override fun registerBluetoothStateListener(bluetoothStateListener: BluetoothStateListener?) {
        checkRuntime(true)
        if (bluetoothStateListener == null || bluetoothStateListeners.contains(bluetoothStateListener)) {
            return
        }
        bluetoothStateListeners.add(bluetoothStateListener)
    }

    override fun unregisterBluetoothStateListener(bluetoothStateListener: BluetoothStateListener?) {
        checkRuntime(true)
        if (bluetoothStateListener != null) {
            bluetoothStateListeners.remove(bluetoothStateListener)
        }
    }

    override fun registerBluetoothBondListener(bluetoothBondListener: BluetoothBondListener?) {
        checkRuntime(true)
        if (bluetoothBondListener == null || bluetoothBondListeners.contains(bluetoothBondListener)) {
            return
        }
        bluetoothBondListeners.add(bluetoothBondListener)
    }

    override fun unregisterBluetoothBondListener(bluetoothBondListener: BluetoothBondListener?) {
        checkRuntime(true)
        if (bluetoothBondListener != null) {
            bluetoothBondListeners.remove(bluetoothBondListener)
        }
    }

    override fun clearRequest(address: String?, type: Int) {
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
                putInt(Constants.EXTRA_TYPE, type)
            }
        safeCallBluetoothApi(20, bundle, null)
    }

    override fun refreshCache(address: String?) {
        checkRuntime(true)
        val bundle =
            Bundle().apply {
                putString(Constants.EXTRA_MAC, address)
            }
        safeCallBluetoothApi(21, bundle, null)
    }

    private fun safeCallBluetoothApi(type: Int, bundle: Bundle?, bluetoothResponse: BluetoothResponse?) {
        checkRuntime(true)
        try {
            val service = getBluetoothService()
            if (service != null) {
                service.callBluetoothApi(type, bundle ?: Bundle(), bluetoothResponse)
            } else {
                bluetoothResponse?.onResponse(Constants.SERVICE_UNREADY, null)
            }
        } catch (throwable: Throwable) {
            BluetoothLog.e(throwable)
        }
    }

    override fun onIntercept(obj: Any?, method: Method, objArr: Array<Any?>?): Boolean {
        workerHandler.obtainMessage(MSG_INVOKE_PROXY, ProxyBulk(obj, method, objArr)).sendToTarget()
        return true
    }

    private fun notifyBluetoothManagerReady() {
        countDownLatch?.countDown()
        countDownLatch = null
    }

    private fun waitBluetoothManagerReady() {
        try {
            countDownLatch?.await()
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }
    }

    override fun handleMessage(message: Message): Boolean {
        when (message.what) {
            MSG_INVOKE_PROXY -> ProxyBulk.safeInvoke(message.obj)
            MSG_REG_RECEIVER -> registerBluetoothReceiver()
        }
        return true
    }

    private fun registerBluetoothReceiver() {
        checkRuntime(true)
        BluetoothReceiver.getInstance().register(
            object : BluetoothStateChangeListener() {
                override fun onBluetoothStateChanged(previousState: Int, state: Int) {
                    checkRuntime(true)
                    dispatchBluetoothStateChanged(state)
                }
            },
        )
        BluetoothReceiver.getInstance().register(
            object : BluetoothBondStateChangeListener() {
                override fun onBondStateChanged(address: String?, bondState: Int) {
                    checkRuntime(true)
                    dispatchBondStateChanged(address, bondState)
                }
            },
        )
        BluetoothReceiver.getInstance().register(
            object : BleConnectStatusChangeListener() {
                override fun onConnectStatusChanged(address: String?, status: Int) {
                    checkRuntime(true)
                    if (status == Constants.STATUS_DISCONNECTED) {
                        clearNotifyListener(address)
                    }
                    dispatchConnectionStatus(address, status)
                }
            },
        )
        BluetoothReceiver.getInstance().register(
            object : BleCharacterChangeListener() {
                override fun onCharacterChanged(address: String?, service: UUID?, character: UUID?, value: ByteArray?) {
                    checkRuntime(true)
                    dispatchCharacterNotify(address, service, character, value)
                }
            },
        )
    }

    private fun dispatchCharacterNotify(address: String?, service: UUID?, character: UUID?, value: ByteArray?) {
        checkRuntime(true)
        if (service == null || character == null || value == null) {
            return
        }
        val listeners = notifyResponses[address]?.get(generateCharacterKey(service, character)) ?: return
        for (listener in listeners) {
            listener.onNotify(service, character, value)
        }
    }

    private fun dispatchConnectionStatus(address: String?, status: Int) {
        checkRuntime(true)
        if (address == null) {
            return
        }
        val listeners = connectStatusListeners[address]
        if (ListUtils.isEmpty(listeners)) {
            return
        }
        listeners?.forEach { listener ->
            listener.invokeSync(address, status)
        }
    }

    private fun dispatchBluetoothStateChanged(state: Int) {
        checkRuntime(true)
        if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_ON) {
            for (listener in bluetoothStateListeners) {
                listener.invokeSync(state == BluetoothAdapter.STATE_ON)
            }
        }
    }

    private fun dispatchBondStateChanged(address: String?, bondState: Int) {
        checkRuntime(true)
        for (listener in bluetoothBondListeners) {
            listener.invokeSync(address, bondState)
        }
    }

    private fun checkRuntime(workerThread: Boolean) {
        val expectedLooper = if (workerThread) workerHandler.looper else Looper.getMainLooper()
        if (Looper.myLooper() != expectedLooper) {
            throw RuntimeException()
        }
    }

    companion object {
        private const val MSG_INVOKE_PROXY = 1
        private const val MSG_REG_RECEIVER = 2
        private val TAG = BluetoothClientImpl::class.java.simpleName

        @Volatile
        private var instance: IBluetoothClient? = null

        @JvmStatic
        fun getInstance(context: Context?): IBluetoothClient {
            val existing = instance
            if (existing != null) {
                return existing
            }
            return synchronized(BluetoothClientImpl::class.java) {
                instance
                    ?: run {
                        val safeContext = context ?: throw IllegalArgumentException("Context required before initialization")
                        val client = BluetoothClientImpl(safeContext)
                        @Suppress("UNCHECKED_CAST")
                        (ProxyUtils.getProxy(client, IBluetoothClient::class.java, client) as IBluetoothClient).also {
                            instance = it
                        }
                    }
            }
        }
    }
}
