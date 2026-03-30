package com.inuker.bluetooth.library.search

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
import com.inuker.bluetooth.library.utils.BluetoothUtils
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils
import java.lang.reflect.Method

class BluetoothSearchHelper private constructor() : IBluetoothSearchHelper, ProxyInterceptor, Handler.Callback {
    private var currentRequest: BluetoothSearchRequest? = null
    private val handler = Handler(Looper.getMainLooper(), this)

    override fun startSearch(bluetoothSearchRequest: BluetoothSearchRequest, bluetoothSearchResponse: BluetoothSearchResponse) {
        bluetoothSearchRequest.setSearchResponse(BluetoothSearchResponseImpl(bluetoothSearchResponse))
        if (!BluetoothUtils.isBluetoothEnabled()) {
            bluetoothSearchRequest.cancel()
            return
        }
        stopSearch()
        if (currentRequest == null) {
            currentRequest = bluetoothSearchRequest
            bluetoothSearchRequest.start()
        }
    }

    override fun stopSearch() {
        currentRequest?.cancel()
        currentRequest = null
    }

    override fun onIntercept(obj: Any?, method: Method, objArr: Array<out Any?>?): Boolean {
        handler.obtainMessage(0, ProxyBulk(obj, method, objArr)).sendToTarget()
        return true
    }

    override fun handleMessage(message: Message): Boolean {
        ProxyBulk.safeInvoke(message.obj)
        return true
    }

    private inner class BluetoothSearchResponseImpl(
        private val response: BluetoothSearchResponse,
    ) : BluetoothSearchResponse {
        override fun onSearchStarted() {
            response.onSearchStarted()
        }

        override fun onDeviceFounded(searchResult: SearchResult) {
            response.onDeviceFounded(searchResult)
        }

        override fun onSearchStopped() {
            response.onSearchStopped()
            currentRequest = null
        }

        override fun onSearchCanceled() {
            response.onSearchCanceled()
            currentRequest = null
        }
    }

    companion object {
        @Volatile
        private var instance: IBluetoothSearchHelper? = null

        @JvmStatic
        fun getInstance(): IBluetoothSearchHelper {
            return instance ?: synchronized(this) {
                instance ?: run {
                    val helper = BluetoothSearchHelper()
                    val proxy = ProxyUtils.getProxy(helper, IBluetoothSearchHelper::class.java, helper) as IBluetoothSearchHelper
                    instance = proxy
                    proxy
                }
            }
        }
    }
}
