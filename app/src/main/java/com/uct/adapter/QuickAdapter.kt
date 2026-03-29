package com.uct.adapter

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inuker.bluetooth.library.search.SearchResult
import com.officialwebsite.R
import com.uct.App

class QuickAdapter : BaseQuickAdapter<SearchResult, BaseViewHolder>(R.layout.item_device) {
    init {
        addChildClickViewIds(R.id.btn_lj, R.id.btn_dk, R.id.btn_jr)
    }

    override fun convert(holder: BaseViewHolder, item: SearchResult) {
        val connectStatus = App.getBle().getConnectStatus(item.address)
        val actionLayout = holder.getView<LinearLayout>(R.id.ll_lj)
        val connectButton = holder.getView<Button>(R.id.btn_lj)
        if (connectStatus == CONNECTED_STATUS) {
            actionLayout.visibility = View.VISIBLE
            connectButton.visibility = View.GONE
        } else {
            actionLayout.visibility = View.GONE
            connectButton.visibility = View.VISIBLE
        }
        holder.setText(R.id.deviceName, item.name)
            .setText(R.id.deviceMac, item.address)
            .setText(R.id.deviceRssi, "${item.rssi}dBm")
    }

    private companion object {
        const val CONNECTED_STATUS = 2
    }
}
