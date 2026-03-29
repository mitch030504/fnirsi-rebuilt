package com.chad.library.adapter.base.listener;

import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;

public interface OnItemChildClickListener {
    void onItemChildClick(BaseQuickAdapter<?, ?> baseQuickAdapter, View view, int position);
}
