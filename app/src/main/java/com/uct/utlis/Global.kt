package com.uct.utlis

import com.inuker.bluetooth.library.model.BleGattProfile
import java.text.DecimalFormat

object Global {
    @JvmField
    val bleGattProfileMap: MutableMap<String, BleGattProfile> = HashMap()

    @JvmField
    val sbxyhb = arrayOf(
        "Unknown",
        "DCP 1.5A",
        "QC2.0 5V",
        "QC2.0 9V",
        "QC2.0 12V",
        "QC2.0 20V",
        "QC3.0",
        "APPLE 2.1A",
        "APPLE 2.4A",
        "SAMSUNG 2.OA",
        "USB2.0 FULL",
        "USB2.0 HIGH",
        "FCP AFC 9V",
        "FCP AFC 12V",
        "HUAWEI SCP",
        "PD MTK",
    )

    @JvmField
    val cfxyh = arrayOf("NONE", "QC2", "QC3", "FCP", "SCP", "AFC", "PD", "PD1", "VOOC", "SVOOC", "SVOOC")

    @JvmStatic
    fun getMinute(seconds: Long): String {
        val totalMillis = seconds * 1000
        val days = totalMillis / 86_400_000
        val remainingDayMillis = totalMillis % 86_400_000
        val hours = remainingDayMillis / 3_600_000
        val remainingHourMillis = remainingDayMillis % 3_600_000
        val minutes = remainingHourMillis / 60_000
        val secs = (remainingHourMillis % 60_000) / 1000
        val totalHours = days * 24 + hours
        return "%02d:%02d:%02d".format(totalHours, minutes, secs)
    }

    @JvmStatic
    fun getHMinute(seconds: Long): String {
        val totalMillis = seconds * 1000
        val days = totalMillis / 86_400_000
        val remainingDayMillis = totalMillis % 86_400_000
        val hours = remainingDayMillis / 3_600_000
        val remainingHourMillis = remainingDayMillis % 3_600_000
        val minutes = remainingHourMillis / 60_000
        val secs = (remainingHourMillis % 60_000) / 1000
        return "${days}day  %02d:%02d:%02d".format(hours, minutes, secs)
    }

    @JvmStatic
    fun get6Num(value: String): String = DecimalFormat(patternFor(value, 6)).format(value.toDouble())

    @JvmStatic
    fun get5Num(value: String): String = DecimalFormat(patternFor(value, 5)).format(value.toDouble())

    @JvmStatic
    fun pd(value: String?, integerDigits: Int, fractionalDigits: Int): Boolean {
        if (value.isNullOrEmpty()) {
            return false
        }
        if (!value.contains('.')) {
            return value.length <= integerDigits
        }
        val parts = value.split(".")
        return parts[0].length <= integerDigits && parts[1].length <= fractionalDigits
    }

    private fun patternFor(value: String, totalDigits: Int): String {
        val integerPart = value.substringBefore('.')
        val decimalDigits = (totalDigits - integerPart.length).coerceAtLeast(0)
        return buildString {
            append("0.")
            repeat(decimalDigits) { append('0') }
        }
    }
}
