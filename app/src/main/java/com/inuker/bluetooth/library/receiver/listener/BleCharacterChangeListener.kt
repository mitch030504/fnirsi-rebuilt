package com.inuker.bluetooth.library.receiver.listener

import java.util.UUID

abstract class BleCharacterChangeListener : BluetoothReceiverListener() {
    protected abstract fun onCharacterChanged(
        address: String?,
        service: UUID?,
        character: UUID?,
        value: ByteArray?,
    )

    override fun onInvoke(vararg objArr: Any?) {
        onCharacterChanged(
            objArr.getOrNull(0) as? String,
            objArr.getOrNull(1) as? UUID,
            objArr.getOrNull(2) as? UUID,
            objArr.getOrNull(3) as? ByteArray,
        )
    }

    override fun getName(): String = BleCharacterChangeListener::class.java.simpleName
}
