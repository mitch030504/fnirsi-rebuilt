@file:JvmName("BleDetailDialogs")

package com.uct

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.officialwebsite.R
import com.uct.entity.RljsEntity
import com.uct.utlis.Global
import com.uct.utlis.getCmd
import com.uct.utlis.intToByte2LH

data class AlarmConfigResult(
    val lowVoltage: Double,
    val highVoltage: Double,
    val highCurrent: Double,
    val beepEnabled: Boolean,
    val vibrateEnabled: Boolean,
)

fun interface VoltageEfficiencyCallback {
    fun onApply(dcdy: Double, efficiencyPercent: Double)
}

fun interface AlarmConfigCallback {
    fun onApply(result: AlarmConfigResult)
}

fun showCapacityStopDialog(context: Context, onConfirmed: Runnable) {
    AlertDialog.Builder(context)
        .setTitle(R.string.tzcf)
        .setPositiveButton(R.string.ok) { _, _ -> onConfirmed.run() }
        .setNegativeButton(R.string.cancel, null)
        .show()
}

fun showCapacityStartDialog(context: Context, onConfirmed: Runnable) {
    AlertDialog.Builder(context)
        .setTitle(R.string.cf)
        .setPositiveButton(R.string.ok) { _, _ -> onConfirmed.run() }
        .setNegativeButton(R.string.cancel, null)
        .show()
}

fun showRljsEditDialog(
    context: Context,
    entity: RljsEntity,
    onApply: VoltageEfficiencyCallback,
) {
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_rljs_edit, null as ViewGroup?)
    val efficiencyInput = view.findViewById<EditText>(R.id.tv_xl)
    val voltageInput = view.findViewById<EditText>(R.id.tv_dcdy)
    efficiencyInput.setText((entity.xl * 100.0).toString())
    voltageInput.setText(entity.dcdy.toString())
    showVoltageEfficiencyEditor(context, view, efficiencyInput, voltageInput, onApply)
}

fun showDefaultRljsDialog(
    context: Context,
    efficiencyText: String,
    voltageText: String,
    onApply: VoltageEfficiencyCallback,
) {
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_rljs_edit, null as ViewGroup?)
    val efficiencyInput = view.findViewById<EditText>(R.id.tv_xl)
    val voltageInput = view.findViewById<EditText>(R.id.tv_dcdy)
    efficiencyInput.setText(efficiencyText)
    voltageInput.setText(voltageText)
    showVoltageEfficiencyEditor(context, view, efficiencyInput, voltageInput, onApply)
}

fun showAlarmConfigDialog(
    context: Context,
    lowVoltage: Double,
    highVoltage: Double,
    highCurrent: Double,
    beepEnabled: Boolean,
    vibrateEnabled: Boolean,
    onApply: AlarmConfigCallback,
) {
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_gyglbj, null as ViewGroup?)
    val lowInput = view.findViewById<EditText>(R.id.ed_dyxx)
    val highInput = view.findViewById<EditText>(R.id.ed_dysx)
    val currentInput = view.findViewById<EditText>(R.id.ed_dlsx)
    val beepSwitch = view.findViewById<Switch>(R.id.sw_kq)
    val vibrateSwitch = view.findViewById<Switch>(R.id.sw_zd)

    lowInput.setText(lowVoltage.toString())
    highInput.setText(highVoltage.toString())
    currentInput.setText(highCurrent.toString())
    beepSwitch.isChecked = beepEnabled
    vibrateSwitch.isChecked = vibrateEnabled

    val dialog =
        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
    dialog.setOnShowListener {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val low = lowInput.text.toString().trim()
            val high = highInput.text.toString().trim()
            val current = currentInput.text.toString().trim()
            if (!Global.pd(low, 2, 1) || !Global.pd(high, 2, 1) || !Global.pd(current, 2, 1)) {
                showCenteredToast(context, context.getString(R.string.ccxz))
                return@setOnClickListener
            }
            onApply.onApply(
                AlarmConfigResult(
                    low.toDouble(),
                    high.toDouble(),
                    current.toDouble(),
                    beepSwitch.isChecked,
                    vibrateSwitch.isChecked,
                ),
            )
            dialog.dismiss()
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener { dialog.dismiss() }
    }
    dialog.show()
}

fun showClearGroupDialog(
    context: Context,
    groupNumber: Int,
    onConfirmed: Runnable,
) {
    AlertDialog.Builder(context)
        .setTitle(R.string.tips)
        .setMessage(context.getString(R.string.okqc) + groupNumber + "?")
        .setPositiveButton(R.string.ok) { _, _ -> onConfirmed.run() }
        .setNegativeButton(R.string.cancel, null)
        .show()
}

fun showHelpDialog(context: Context, message: String) {
    AlertDialog.Builder(context)
        .setTitle(R.string.help)
        .setMessage(message)
        .setPositiveButton(R.string.ok, null)
        .show()
}

fun buildCapacityStartCommand(selected: Int, voltageTag: String): ByteArray {
    val voltageBytes = intToByte2LH((voltageTag.toDouble() * 1000.0).toInt())
    val payload = byteArrayOf(selected.toByte(), 0, 0)
    System.arraycopy(voltageBytes, 0, payload, 1, voltageBytes.size)
    return getCmd((-122).toByte(), payload)
}

fun buildClearGroupCommand(groupNumber: Int): ByteArray = getCmd((-119).toByte(), byteArrayOf(groupNumber.toByte()))

private fun showVoltageEfficiencyEditor(
    context: Context,
    view: View,
    efficiencyInput: EditText,
    voltageInput: EditText,
    onApply: VoltageEfficiencyCallback,
) {
    val dialog =
        AlertDialog.Builder(context)
            .setTitle(R.string.csxg)
            .setView(view)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
    dialog.setOnShowListener {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val efficiency = efficiencyInput.text.toString().trim()
            val voltage = voltageInput.text.toString().trim()
            if (efficiency.isEmpty() || voltage.isEmpty()) {
                Toast.makeText(context, R.string.isnull, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val efficiencyValue = efficiency.toDouble()
            val voltageValue = voltage.toDouble()
            if (efficiencyValue < 80.0 || efficiencyValue > 100.0) {
                showCenteredToast(
                    context,
                    context.getString(R.string.zxlti) + "\n" + context.getString(R.string.zdcdyti),
                )
                return@setOnClickListener
            }
            if (voltageValue < 3.0 || voltageValue > 5.0) {
                showCenteredToast(
                    context,
                    context.getString(R.string.zxlti) + "\n" + context.getString(R.string.zdcdyti),
                )
                return@setOnClickListener
            }
            onApply.onApply(voltageValue, efficiencyValue)
            dialog.dismiss()
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener { dialog.dismiss() }
    }
    dialog.show()
}

private fun showCenteredToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).apply {
        setGravity(17, 0, 0)
        show()
    }
}
