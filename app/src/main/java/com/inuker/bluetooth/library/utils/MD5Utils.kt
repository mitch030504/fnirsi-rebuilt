package com.inuker.bluetooth.library.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

object MD5Utils {
    @JvmStatic
    fun MD5_12(source: String): ByteArray? {
        return try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(source.toByteArray(), 0, source.length)
            val digest = messageDigest.digest()
            if (digest.size >= 12) {
                val middle = digest.size / 2
                Arrays.copyOfRange(digest, middle - 6, middle + 6)
            } else {
                ByteUtils.EMPTY_BYTES
            }
        } catch (_: NoSuchAlgorithmException) {
            null
        }
    }
}
