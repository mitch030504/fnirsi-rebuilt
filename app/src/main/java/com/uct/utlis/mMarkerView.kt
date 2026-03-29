package com.uct.utlis

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.officialwebsite.R
import com.uct.entity.CurrentVoltageEntity

class mMarkerView : MarkerView {
    private val contentTv: TextView
    private val contentTvY: TextView
    private var query: List<CurrentVoltageEntity> = emptyList()

    constructor(context: Context, layoutResource: Int) : super(context, layoutResource) {
        contentTv = findViewById(R.id.tv_content_marker_view_x)
        contentTvY = findViewById(R.id.tv_content_marker_view_y)
    }

    constructor(
        query: List<CurrentVoltageEntity>,
        context: Context,
        layoutResource: Int,
    ) : super(context, layoutResource) {
        this.query = query
        contentTv = findViewById(R.id.tv_content_marker_view_x)
        contentTvY = findViewById(R.id.tv_content_marker_view_y)
    }

    override fun refreshContent(entry: Entry, highlight: Highlight) {
        val index = entry.x.toInt()
        if (index in query.indices) {
            contentTv.text = query[index].current.toString()
            contentTvY.text = query[index].voltage.toString()
        } else {
            contentTv.text = ""
            contentTvY.text = ""
        }
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        val offset = offset
        val width = width.toFloat()
        val height = height.toFloat()
        offset.y = if (posY <= height) height else -height
        offset.x =
            when {
                posX > width -> -width
                posX > width / 2.0f -> -(width / 2.0f)
                else -> 0.0f
            }
        return offset
    }
}
