package com.inuker.bluetooth.library.search

import android.os.Parcel
import android.os.Parcelable
import com.inuker.bluetooth.library.utils.BluetoothUtils

class SearchRequest() : Parcelable {
    private var tasksInternal: MutableList<SearchTask>? = null

    private constructor(parcel: Parcel) : this() {
        val result = ArrayList<SearchTask>()
        parcel.readTypedList(result, SearchTask.CREATOR)
        tasksInternal = result
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(getTasks())
    }

    fun getTasks(): MutableList<SearchTask> {
        if (tasksInternal == null) {
            tasksInternal = ArrayList()
        }
        return tasksInternal!!
    }

    fun setTasks(value: MutableList<SearchTask>?) {
        tasksInternal = value
    }

    class Builder {
        private val tasks = ArrayList<SearchTask>()

        fun searchBluetoothLeDevice(duration: Int): Builder {
            if (BluetoothUtils.isBleSupported()) {
                SearchTask().also {
                    it.setSearchType(2)
                    it.setSearchDuration(duration)
                    tasks.add(it)
                }
            }
            return this
        }

        fun searchBluetoothLeDevice(duration: Int, times: Int): Builder {
            repeat(times) { searchBluetoothLeDevice(duration) }
            return this
        }

        fun searchBluetoothClassicDevice(duration: Int): Builder {
            SearchTask().also {
                it.setSearchType(1)
                it.setSearchDuration(duration)
                tasks.add(it)
            }
            return this
        }

        fun searchBluetoothClassicDevice(duration: Int, times: Int): Builder {
            repeat(times) { searchBluetoothClassicDevice(duration) }
            return this
        }

        fun build(): SearchRequest = SearchRequest().apply { setTasks(tasks) }
    }

    companion object CREATOR : Parcelable.Creator<SearchRequest> {
        override fun createFromParcel(parcel: Parcel): SearchRequest = SearchRequest(parcel)

        override fun newArray(size: Int): Array<SearchRequest?> = arrayOfNulls(size)
    }
}
