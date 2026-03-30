package com.inuker.bluetooth.library.utils

import android.os.Build

object Version {
    @JvmStatic
    fun isMarshmallow(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}
