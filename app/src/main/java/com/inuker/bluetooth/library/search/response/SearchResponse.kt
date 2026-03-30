package com.inuker.bluetooth.library.search.response

import com.inuker.bluetooth.library.search.SearchResult

interface SearchResponse {
    fun onDeviceFounded(searchResult: SearchResult)

    fun onSearchCanceled()

    fun onSearchStarted()

    fun onSearchStopped()
}
