@file:JvmName("BleDetailChartSupport")

package com.uct

import android.content.Context
import android.graphics.Color
import androidx.core.internal.view.SupportMenu
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.officialwebsite.R
import com.uct.entity.CurrentVoltageEntity
import com.uct.utlis.mMarkerView
import java.text.DecimalFormat
import kotlin.math.abs

fun renderChart(
    list: List<CurrentVoltageEntity>,
    lineChart: LineChart,
    dpDmMode: Boolean,
    maxVoltage: Int,
    minVoltage: Int,
    maxCurrent: Int,
    minCurrent: Int,
    showCurrentSeries: Boolean,
    showVoltageSeries: Boolean,
    decimalFormat: DecimalFormat,
    context: Context,
) {
    lineChart.setDrawBorders(true)
    lineChart.setVisibleXRangeMaximum(150.0f)
    lineChart.setVisibleXRangeMinimum(150.0f)
    val voltageSpan = (((maxVoltage / 100) + 1) * 100) - ((minVoltage / 100) * 100)
    val currentSpan = (((maxCurrent / 100) + 1) * 100) - ((minCurrent / 100) * 100)
    val ratio = voltageSpan.toFloat() / currentSpan.toFloat()
    val currentEntries = ArrayList<Entry>()
    val voltageEntries = ArrayList<Entry>()
    val dataSets = ArrayList<ILineDataSet>()
    if (list.isNotEmpty()) {
        lineChart.visibility = android.view.View.VISIBLE
        list.forEachIndexed { index, item ->
            if (!dpDmMode) {
                val adjustedCurrent =
                    ((minVoltage / 100) * 100) + ((item.current.toFloat() - ((minCurrent / 100) * 100)) * ratio)
                currentEntries.add(Entry(index.toFloat(), adjustedCurrent))
            } else {
                currentEntries.add(Entry(index.toFloat(), (item.current - 5000).toFloat()))
            }
            voltageEntries.add(Entry(index.toFloat(), item.voltage.toFloat()))
        }
    } else {
        currentEntries.add(Entry(0.0f, 0.0f))
        voltageEntries.add(Entry(0.0f, 0.0f))
    }

    val currentDataSet =
        LineDataSet(currentEntries, "次数").apply {
            highLightColor = SupportMenu.CATEGORY_MASK
            setDrawCircles(false)
            setDrawHighlightIndicators(true)
            isHighlightEnabled = true
            setDrawValues(false)
            setDrawCircleHole(false)
            color = Color.parseColor("#0098FE")
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
    val voltageDataSet =
        LineDataSet(voltageEntries, "次数").apply {
            highLightColor = SupportMenu.CATEGORY_MASK
            setDrawCircles(false)
            setDrawHighlightIndicators(true)
            isHighlightEnabled = true
            setDrawValues(false)
            setDrawCircleHole(false)
            color = Color.parseColor("#FFFF00")
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

    lineChart.setTouchEnabled(false)
    lineChart.animateY(0)
    lineChart.setGridBackgroundColor(Color.parseColor("#3C3A3A"))
    lineChart.setBorderColor(Color.parseColor("#3C3A3A"))
    lineChart.legend.isEnabled = false
    lineChart.description = Description().apply { isEnabled = false }

    if (showCurrentSeries) {
        dataSets.add(currentDataSet)
    }
    if (showVoltageSeries) {
        dataSets.add(voltageDataSet)
    }
    lineChart.data = LineData(dataSets)
    mMarkerView(list, context, R.layout.content_marker_view)

    lineChart.xAxis.apply {
        isEnabled = true
        position = XAxis.XAxisPosition.BOTTOM
        gridColor = Color.parseColor("#3C3A3A")
        setDrawAxisLine(false)
        valueFormatter = EmptyXAxisFormatter()
        setAvoidFirstLastClipping(false)
        labelCount =
            when {
                currentEntries.size < 3 -> 2
                currentEntries.size < 6 -> 4
                currentEntries.size < 8 -> 6
                else -> 6
            }
    }
    lineChart.setScaleEnabled(false)

    lineChart.axisRight.apply {
        isEnabled = true
        enableGridDashedLine(10.0f, 2.0f, 0.0f)
        gridColor = Color.parseColor("#3C3A3A")
        textColor = Color.parseColor("#0097E9")
        setDrawZeroLine(false)
        valueFormatter = RightAxisFormatter(decimalFormat)
    }
    lineChart.axisLeft.apply {
        enableGridDashedLine(10.0f, 2.0f, 0.0f)
        gridColor = Color.parseColor("#3C3A3A")
        textColor = Color.parseColor("#FFFF00")
        setDrawZeroLine(false)
        if (dpDmMode) {
            lineChart.axisRight.axisMinimum = 0.0f
            lineChart.axisRight.axisMaximum = 10000.0f
            axisMinimum = -5000.0f
            axisMaximum = 5000.0f
        } else {
            lineChart.axisRight.axisMinimum = ((minCurrent / 100) * 100).toFloat()
            lineChart.axisRight.axisMaximum = (((maxCurrent / 100) + 1) * 100).toFloat()
            axisMinimum = ((minVoltage / 100) * 100).toFloat()
            axisMaximum = (((maxVoltage / 100) + 1) * 100).toFloat()
        }
        valueFormatter = LeftAxisFormatter(decimalFormat)
    }
}

private class LeftAxisFormatter(private val decimalFormat: DecimalFormat) : ValueFormatter() {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return try {
            decimalFormat.format(value / 1000.0)
        } catch (_: Exception) {
            ""
        }
    }
}

private class RightAxisFormatter(private val decimalFormat: DecimalFormat) : ValueFormatter() {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return try {
            decimalFormat.format(abs(value / 1000.0))
        } catch (_: Exception) {
            ""
        }
    }
}

private class EmptyXAxisFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String = ""
}
