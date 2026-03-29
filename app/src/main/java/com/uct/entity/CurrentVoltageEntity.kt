package com.uct.entity

import java.util.Date

class CurrentVoltageEntity @JvmOverloads constructor(
    var current: Int = 0,
    var voltage: Int = 0,
    var time: Date? = null,
)
