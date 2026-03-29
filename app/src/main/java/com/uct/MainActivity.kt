package com.uct

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Toast
import com.inuker.bluetooth.library.beacon.Beacon
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleConnectResponse
import com.inuker.bluetooth.library.model.BleGattProfile
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.officialwebsite.R
import com.uct.adapter.BleListAdapter
import com.uct.base.BaseActivity
import com.uct.utlis.BlePermissionHelper
import com.uct.utlis.Global
import com.uct.utlis.LanguageType
import com.uct.utlis.SpUtil
import java.util.ArrayList
import kotlin.concurrent.schedule

class MainActivity : BaseActivity() {
    private lateinit var adapter: BleListAdapter
    private lateinit var btnSs: Button
    private lateinit var progressBar: ProgressBar
    private var progressDialog: ProgressDialog? = null
    private var searching = false

    private val bluetoothStateListener = object : BluetoothStateListener() {
        override fun onBluetoothStateChanged(enabled: Boolean) {
            if (enabled && requestPermission()) {
                initBle()
            }
        }
    }

    private val bleConnectStatusListener = object : BleConnectStatusListener() {
        override fun onConnectStatusChanged(mac: String, status: Int) {
            Log.e(LOG_TAG, "onConnectStatusChanged: $mac--------$status")
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }

    override fun getRootLayout(): Int = R.layout.activity_main

    override fun menu() {
        finish()
    }

    override fun process(bundle: Bundle?) {
        menuImage.visibility = View.GONE
        adapter = BleListAdapter(this) { view, result -> onItemChildClick(view, result) }
        findViewById<ListView>(R.id.lv).adapter = adapter
        adapter.setDatas(ArrayList())
        ble()

        btnSs = fv(R.id.btn_ss)
        btnSs.setOnClickListener {
            if (searching) {
                App.getBle().stopSearch()
            } else {
                refreshConnectedDevices()
            }
        }
        progressBar = findViewById(R.id.pb_s)
        findViewById<ImageView>(R.id.tv_menu).setOnClickListener { showLanguageDialog() }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun right() {
        App.getBle().stopSearch()
    }

    override fun initView() {
        rightText.setText(R.string.stop_it)
    }

    fun listzkjdj(view: View, searchResult: SearchResult) {
        onItemChildClick(view, searchResult)
    }

    private fun showLanguageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_lan, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.rglan)
        radioGroup.check(selectedLanguageId())
        AlertDialog.Builder(selfContext)
            .setTitle(R.string.select_lan)
            .setView(dialogView)
            .setPositiveButton(R.string.ok) { dialog: DialogInterface, _: Int ->
                applySelectedLanguage(radioGroup)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun selectedLanguageId(): Int {
        val currentLanguage = SpUtil.getInstance(applicationContext).getString(SpUtil.LANGUAGE)
        return when (currentLanguage) {
            LanguageType.DY.language -> R.id.rb_10
            LanguageType.FT.language -> R.id.rb_2
            LanguageType.ENGLISH.language -> R.id.rb_3
            LanguageType.RU.language -> R.id.rb_4
            LanguageType.HW.language -> R.id.rb_5
            LanguageType.JA.language -> R.id.rb_6
            LanguageType.PT.language -> R.id.rb_7
            LanguageType.FY.language -> R.id.rb_8
            LanguageType.XBY.language -> R.id.rb_9
            else -> R.id.rb_1
        }
    }

    private fun applySelectedLanguage(radioGroup: RadioGroup) {
        when (radioGroup.checkedRadioButtonId) {
            R.id.rb_1 -> changeLanguage(LanguageType.CHINESE.language, this)
            R.id.rb_10 -> changeLanguage(LanguageType.DY.language, this)
            R.id.rb_2 -> changeLanguage(LanguageType.FT.language, this)
            R.id.rb_3 -> changeLanguage(LanguageType.ENGLISH.language, this)
            R.id.rb_4 -> changeLanguage(LanguageType.RU.language, this)
            R.id.rb_5 -> changeLanguage(LanguageType.HW.language, this)
            R.id.rb_6 -> changeLanguage(LanguageType.JA.language, this)
            R.id.rb_7 -> changeLanguage(LanguageType.PT.language, this)
            R.id.rb_8 -> changeLanguage(LanguageType.FY.language, this)
            R.id.rb_9 -> changeLanguage(LanguageType.XBY.language, this)
        }
    }

    private fun onItemChildClick(view: View, searchResult: SearchResult) {
        when (view.id) {
            R.id.btn_dk -> {
                AlertDialog.Builder(selfContext)
                    .setTitle(R.string.tips)
                    .setMessage(R.string.Confirm_disconnection)
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        disconnect(searchResult)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }

            R.id.btn_jr -> {
                val intent = Intent(this, BleDetailActivity::class.java).apply {
                    putExtra("NAME", searchResult.name)
                    putExtra("MAC", searchResult.address)
                }
                startActivityForResult(intent, 0)
            }

            R.id.btn_lj -> conn(searchResult.address)
        }
        App.getBle().stopSearch()
    }

    private fun refreshConnectedDevices() {
        val connectedDevices = ArrayList<SearchResult>()
        for (searchResult in adapter.getDatas()) {
            if (App.getBle().getConnectStatus(searchResult.address) == CONNECTED_STATUS) {
                connectedDevices.add(searchResult)
            }
        }
        adapter.setDatas(connectedDevices)
        ble()
    }

    private fun disconnect(searchResult: SearchResult) {
        App.getBle().disconnect(searchResult.address)
        java.util.Timer().schedule(DISCONNECT_RETRY_DELAY_MS) {
            App.getBle().disconnect(searchResult.address)
        }
        java.util.Timer().schedule(DISCONNECT_REFRESH_DELAY_MS) {
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }

    private fun ble() {
        if (!requestPermission()) {
            return
        }
        val bluetoothOpened = App.getBle().isBluetoothOpened()
        Log.e(LOG_TAG, "initView: bluetoothOpened$bluetoothOpened")
        if (!bluetoothOpened) {
            App.getBle().openBluetooth()
            App.getBle().registerBluetoothStateListener(bluetoothStateListener)
            return
        }
        initBle()
    }

    private fun requestPermission(): Boolean {
        if (BlePermissionHelper.hasRequiredPermissions(this)) {
            return true
        }
        BlePermissionHelper.requestRequiredPermissions(this, REQUEST_PERMISSION_ACCESS_LOCATION)
        Log.e(LOG_TAG, "requestPermission: ")
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_PERMISSION_ACCESS_LOCATION) {
            return
        }
        if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, R.string.isnull, Toast.LENGTH_SHORT).show()
            return
        }
        ble()
    }

    private fun initBle() {
        val request = SearchRequest.Builder()
            .searchBluetoothLeDevice(SEARCH_TIMEOUT_MS, SEARCH_REPEAT_COUNT)
            .build()
        App.getBle().search(request, object : SearchResponse {
            override fun onSearchStarted() {
                btnSs.setText(R.string.stopsousuo)
                searching = true
                rightText.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
            }

            override fun onDeviceFounded(searchResult: SearchResult) {
                val type = searchResult.device?.type ?: return
                val name = searchResult.name
                if (name == "NULL" || type == DEVICE_TYPE_CLASSIC || type == DEVICE_TYPE_DUAL || searchResult.rssi == 0) {
                    return
                }
                Beacon(searchResult.scanRecord)
                Log.e(LOG_TAG, "onDeviceFounded: ${searchResult.address}   name：$name")
                val existing = adapter.getDatas().firstOrNull { it.address == searchResult.address }
                if (existing == null) {
                    adapter.addDatas(listOf(searchResult))
                    App.getBle().registerConnectStatusListener(searchResult.address, bleConnectStatusListener)
                    return
                }
                existing.rssi = searchResult.rssi
            }

            override fun onSearchStopped() {
                resetSearchUi()
            }

            override fun onSearchCanceled() {
                resetSearchUi()
            }
        })
    }

    fun conn(address: String) {
        val connectOptions = BleConnectOptions.Builder()
            .setConnectRetry(3)
            .setConnectTimeout(30_000)
            .setServiceDiscoverRetry(3)
            .setServiceDiscoverTimeout(20_000)
            .build()
        showProgress("${getString(R.string.start_conn)}:$address")
        App.getBle().connect(address, connectOptions, object : BleConnectResponse {
            override fun onResponse(code: Int, bleGattProfile: BleGattProfile?) {
                dismissProgress()
                if (code != 0 || bleGattProfile == null) {
                    return
                }
                Global.bleGattProfileMap[address] = bleGattProfile
                adapter.notifyDataSetChanged()
            }
        })
    }

    @Suppress("DEPRECATION")
    private fun showProgress(message: String) {
        val dialog = progressDialog
        if (dialog == null) {
            progressDialog = ProgressDialog(this).apply {
                setMessage(message)
                setCancelable(false)
                setCanceledOnTouchOutside(false)
                show()
            }
            return
        }
        dialog.setMessage(message)
    }

    private fun dismissProgress() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun resetSearchUi() {
        btnSs.setText(R.string.sousuo)
        searching = false
        rightText.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        App.getBle().unregisterBluetoothStateListener(bluetoothStateListener)
        dismissProgress()
        for (searchResult in adapter.getDatas()) {
            App.getBle().unregisterConnectStatusListener(searchResult.address, bleConnectStatusListener)
            if (App.getBle().getConnectStatus(searchResult.address) == CONNECTED_STATUS) {
                App.getBle().disconnect(searchResult.address)
            }
        }
    }

    private companion object {
        const val LOG_TAG = "TAG"
        const val REQUEST_PERMISSION_ACCESS_LOCATION = 1
        const val SEARCH_TIMEOUT_MS = 10_000
        const val SEARCH_REPEAT_COUNT = 2
        const val DISCONNECT_RETRY_DELAY_MS = 1_000L
        const val DISCONNECT_REFRESH_DELAY_MS = 2_000L
        const val CONNECTED_STATUS = 2
        const val DEVICE_TYPE_CLASSIC = 1
        const val DEVICE_TYPE_DUAL = 3
    }
}
