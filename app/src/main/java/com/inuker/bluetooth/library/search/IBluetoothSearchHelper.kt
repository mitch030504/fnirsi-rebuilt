package com.inuker.bluetooth.library.search

import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse

interface IBluetoothSearchHelper {
    fun startSearch(bluetoothSearchRequest: BluetoothSearchRequest, bluetoothSearchResponse: BluetoothSearchResponse)

    fun stopSearch()
}
