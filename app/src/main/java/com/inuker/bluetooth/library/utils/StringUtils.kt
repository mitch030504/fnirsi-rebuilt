package com.inuker.bluetooth.library.utils

object StringUtils {
    @JvmStatic
    fun isNotBlank(charSequence: CharSequence?): Boolean = !isBlank(charSequence)

    @JvmStatic
    fun isBlank(charSequence: CharSequence?): Boolean {
        if (charSequence == null || charSequence.isEmpty()) {
            return true
        }
        for (index in 0 until charSequence.length) {
            if (!Character.isWhitespace(charSequence[index])) {
                return false
            }
        }
        return true
    }
}
