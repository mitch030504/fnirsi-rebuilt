package com.uct.utlis

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

object BlePermissionHelper {
    @JvmStatic
    fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            else -> emptyArray()
        }
    }

    @JvmStatic
    fun hasRequiredPermissions(activity: Activity): Boolean {
        return getRequiredPermissions().all { permission ->
            ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    @JvmStatic
    fun requestRequiredPermissions(activity: Activity, requestCode: Int) {
        val missingPermissions = getRequiredPermissions().filter { permission ->
            ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, missingPermissions.toTypedArray(), requestCode)
        }
    }
}
