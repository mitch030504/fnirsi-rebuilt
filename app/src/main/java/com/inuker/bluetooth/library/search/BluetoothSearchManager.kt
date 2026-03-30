package com.inuker.bluetooth.library.search

import android.os.Bundle
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse

object BluetoothSearchManager {
    @JvmStatic
    fun search(searchRequest: SearchRequest, bleGeneralResponse: BleGeneralResponse) {
        BluetoothSearchHelper.getInstance().startSearch(
            BluetoothSearchRequest(searchRequest),
            object : BluetoothSearchResponse {
                override fun onSearchStarted() {
                    bleGeneralResponse.onResponse(1, null)
                }

                override fun onDeviceFounded(searchResult: SearchResult) {
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.EXTRA_SEARCH_RESULT, searchResult)
                    bleGeneralResponse.onResponse(4, bundle)
                }

                override fun onSearchStopped() {
                    bleGeneralResponse.onResponse(2, null)
                }

                override fun onSearchCanceled() {
                    bleGeneralResponse.onResponse(3, null)
                }
            },
        )
    }

    @JvmStatic
    fun stopSearch() {
        BluetoothSearchHelper.getInstance().stopSearch()
    }
}
