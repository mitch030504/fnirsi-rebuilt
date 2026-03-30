package com.inuker.bluetooth.library.search

import android.os.Parcel
import android.os.Parcelable

class SearchTask() : Parcelable {
    private var searchType = 0
    private var searchDuration = 0

    private constructor(parcel: Parcel) : this() {
        searchType = parcel.readInt()
        searchDuration = parcel.readInt()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(searchType)
        parcel.writeInt(searchDuration)
    }

    fun getSearchType(): Int = searchType

    fun setSearchType(value: Int) {
        searchType = value
    }

    fun getSearchDuration(): Int = searchDuration

    fun setSearchDuration(value: Int) {
        searchDuration = value
    }

    companion object CREATOR : Parcelable.Creator<SearchTask> {
        override fun createFromParcel(parcel: Parcel): SearchTask = SearchTask(parcel)

        override fun newArray(size: Int): Array<SearchTask?> = arrayOfNulls(size)
    }
}
