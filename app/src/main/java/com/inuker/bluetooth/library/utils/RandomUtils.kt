package com.inuker.bluetooth.library.utils

import java.util.Random

object RandomUtils {
    private var random: Random? = null

    @JvmStatic
    fun randFloat(): Double {
        if (random == null) {
            random =
                Random().apply {
                    setSeed(System.currentTimeMillis())
                }
        }
        return random!!.nextDouble()
    }
}
