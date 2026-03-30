package com.chad.library.adapter.base.viewholder

import android.util.SparseArray
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val views = SparseArray<View>()

    @Suppress("UNCHECKED_CAST")
    fun <T : View> getView(@IdRes viewId: Int): T {
        var view = views.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            requireNotNull(view) { "View ID $viewId not found in itemView" }
            views.put(viewId, view)
        }
        return view as T
    }

    fun setText(@IdRes viewId: Int, value: CharSequence?): BaseViewHolder {
        (getView<View>(viewId) as? TextView)?.text = value
        return this
    }
}
