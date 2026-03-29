package com.uct.adapter

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.inuker.bluetooth.library.search.SearchResult
import com.officialwebsite.R
import com.uct.App

class BleListAdapter(
    context: Context,
    private val onActionClick: (View, SearchResult) -> Unit,
) : AbsBaseAdapter<SearchResult>(context, R.layout.item_device) {
    override fun bindDatas(viewHolder: ViewHolder, item: SearchResult, position: Int) {
        val connectStatus = App.getBle().getConnectStatus(item.address)
        val actionLayout = viewHolder.getView<LinearLayout>(R.id.ll_lj)
        val connectButton = viewHolder.getView<Button>(R.id.btn_lj)
        if (connectStatus == CONNECTED_STATUS) {
            actionLayout.visibility = View.VISIBLE
            connectButton.visibility = View.GONE
        } else {
            actionLayout.visibility = View.GONE
            connectButton.visibility = View.VISIBLE
        }

        viewHolder
            .bindTextView(R.id.deviceName, item.name)
            .bindTextView(R.id.deviceMac, item.address)
            .bindTextView(R.id.deviceRssi, "${item.rssi}dBm")

        val disconnectButton = viewHolder.getView<Button>(R.id.btn_dk)
        val enterButton = viewHolder.getView<Button>(R.id.btn_jr)
        val disconnectLabel = context.getString(R.string.disconnect)

        connectButton.setOnClickListener { onActionClick(connectButton, item) }
        disconnectButton.setOnClickListener { onActionClick(disconnectButton, item) }
        enterButton.setOnClickListener { onActionClick(enterButton, item) }

        val textSize = if (disconnectLabel.length > 5) SMALL_BUTTON_TEXT_SIZE else DEFAULT_BUTTON_TEXT_SIZE
        disconnectButton.textSize = textSize
        enterButton.textSize = textSize
        connectButton.textSize = textSize
    }

    private companion object {
        const val CONNECTED_STATUS = 2
        const val DEFAULT_BUTTON_TEXT_SIZE = 14.0f
        const val SMALL_BUTTON_TEXT_SIZE = 12.0f
    }
}
