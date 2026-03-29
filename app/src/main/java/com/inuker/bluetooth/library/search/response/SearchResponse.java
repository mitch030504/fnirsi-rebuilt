package com.inuker.bluetooth.library.search.response;

import com.inuker.bluetooth.library.search.SearchResult;

/* JADX INFO: loaded from: classes.dex */
public interface SearchResponse {
    void onDeviceFounded(SearchResult searchResult);

    void onSearchCanceled();

    void onSearchStarted();

    void onSearchStopped();
}
