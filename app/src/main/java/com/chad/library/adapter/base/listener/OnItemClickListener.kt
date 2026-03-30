package com.chad.library.adapter.base.listener

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter

fun interface OnItemClickListener {
    fun onItemClick(baseQuickAdapter: BaseQuickAdapter<*, *>, view: View, position: Int)
}
