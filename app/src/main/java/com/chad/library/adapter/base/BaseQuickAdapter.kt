package com.chad.library.adapter.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.util.LinkedHashSet

abstract class BaseQuickAdapter<T, VH : BaseViewHolder>(
    @LayoutRes private val layoutResId: Int,
) : RecyclerView.Adapter<VH>() {
    private val items = ArrayList<T>()
    private val childClickViewIds = LinkedHashSet<Int>()
    private var headerView: View? = null
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemChildClickListener: OnItemChildClickListener? = null

    val data: MutableList<T>
        get() = items

    protected abstract fun convert(holder: VH, item: T)

    fun addHeaderView(view: View): Int {
        headerView = view
        notifyDataSetChanged()
        return 0
    }

    fun addChildClickViewIds(vararg viewIds: Int) {
        viewIds.forEach(childClickViewIds::add)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    fun setOnItemChildClickListener(listener: OnItemChildClickListener?) {
        onItemChildClickListener = listener
    }

    fun setNewData(newData: List<T>?) {
        items.clear()
        if (newData != null) {
            items.addAll(newData)
        }
        notifyDataSetChanged()
    }

    fun addData(item: T) {
        items.add(item)
        notifyItemInserted((if (hasHeader()) 1 else 0) + items.size - 1)
    }

    override fun getItemCount(): Int = items.size + if (hasHeader()) 1 else 0

    override fun getItemViewType(position: Int): Int = if (hasHeader() && position == 0) TYPE_HEADER else 0

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        if (viewType == TYPE_HEADER) {
            return BaseViewHolder(requireNotNull(headerView)) as VH
        }
        val itemView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return BaseViewHolder(itemView) as VH
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return
        }
        val dataPosition = toDataPosition(position)
        convert(holder, items[dataPosition])
        bindClicks(holder, dataPosition)
    }

    private fun bindClicks(holder: VH, dataPosition: Int) {
        holder.itemView.setOnClickListener(null)
        onItemClickListener?.let { listener ->
            holder.itemView.setOnClickListener { view ->
                listener.onItemClick(this, view, dataPosition)
            }
        }
        for (viewId in childClickViewIds) {
            holder.getView<View>(viewId)?.apply {
                setOnClickListener(null)
                onItemChildClickListener?.let { listener ->
                    setOnClickListener { view ->
                        listener.onItemChildClick(this@BaseQuickAdapter, view, dataPosition)
                    }
                }
            }
        }
    }

    private fun hasHeader(): Boolean = headerView != null

    private fun toDataPosition(adapterPosition: Int): Int = if (hasHeader()) adapterPosition - 1 else adapterPosition

    private companion object {
        const val TYPE_HEADER: Int = Int.MIN_VALUE
    }
}
