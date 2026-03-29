package com.uct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

abstract class AbsBaseAdapter<T>(
    protected val context: Context,
    private val layoutResId: Int,
) : BaseAdapter() {
    private var items: MutableList<T> = ArrayList()

    abstract fun bindDatas(viewHolder: ViewHolder, item: T, position: Int)

    fun setDatas(list: List<T>) {
        items = ArrayList(list)
        notifyDataSetChanged()
    }

    fun getDatas(): MutableList<T> = items

    fun addDatas(list: List<T>) {
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): T = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val (itemView, viewHolder) = if (convertView != null) {
            convertView to (convertView.tag as ViewHolder)
        } else {
            val inflated = LayoutInflater.from(context).inflate(layoutResId, parent, false)
            val holder = ViewHolder(inflated)
            inflated.tag = holder
            inflated to holder
        }
        try {
            bindDatas(viewHolder, items[position], position)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return itemView
    }

    class ViewHolder(private val layoutView: View) {
        private val cacheMap = HashMap<Int, View>()

        @Suppress("UNCHECKED_CAST")
        fun <V : View> getView(viewId: Int): V {
            val cached = cacheMap[viewId]
            if (cached != null) {
                return cached as V
            }
            val view = layoutView.findViewById<View>(viewId)
            cacheMap[viewId] = view
            return view as V
        }

        fun bindTextView(viewId: Int, value: String?): ViewHolder {
            getView<TextView>(viewId).text = value
            return this
        }
    }
}
