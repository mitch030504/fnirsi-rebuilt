package com.inuker.bluetooth.library.receiver

import android.content.Context
import android.content.Intent
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.receiver.listener.BleCharacterChangeListener
import java.util.UUID

class BleCharacterChangeReceiver(
    dispatcher: IReceiverDispatcher,
) : AbsBluetoothReceiver(dispatcher) {
    override fun getActions(): List<String> = listOf(Constants.ACTION_CHARACTER_CHANGED)

    override fun onReceive(context: Context, intent: Intent): Boolean {
        onCharacterChanged(
            intent.getStringExtra(Constants.EXTRA_MAC),
            intent.getSerializableExtra(Constants.EXTRA_SERVICE_UUID) as? UUID,
            intent.getSerializableExtra(Constants.EXTRA_CHARACTER_UUID) as? UUID,
            intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE),
        )
        return true
    }

    private fun onCharacterChanged(
        address: String?,
        service: UUID?,
        character: UUID?,
        value: ByteArray?,
    ) {
        getListeners(BleCharacterChangeListener::class.java).forEach {
            it.invoke(address, service, character, value)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(dispatcher: IReceiverDispatcher): BleCharacterChangeReceiver {
            return BleCharacterChangeReceiver(dispatcher)
        }
    }
}
