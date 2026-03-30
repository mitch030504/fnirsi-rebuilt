package com.inuker.bluetooth.library.connect.response

interface BleTResponse<T> {
    fun onResponse(code: Int, data: T?)
}
