package com.chad.library.adapter.base.listener

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter

fun interface OnItemChildClickListener {
    fun onItemChildClick(baseQuickAdapter: BaseQuickAdapter<*, *>, view: View, position: Int)
}
