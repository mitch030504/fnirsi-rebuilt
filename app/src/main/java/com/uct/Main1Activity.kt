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
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
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
import com.uct.adapter.QuickAdapter
import com.uct.base.BaseActivity
import com.uct.utlis.BlePermissionHelper
import com.uct.utlis.Global
import java.util.ArrayList
import java.util.Timer
import kotlin.concurrent.schedule

class Main1Activity : BaseActivity() {
    private lateinit var adapter: QuickAdapter
    private lateinit var btnSs: Button
    private lateinit var progressBar: ProgressBar
    private var progressDialog: ProgressDialog? = null
    private var timer: Timer? = null
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

    override fun getRootLayout(): Int = R.layout.activity_main1

    override fun menu() {
        finish()
    }

    override fun process(bundle: Bundle?) {
        menuImage.visibility = View.GONE
        setupRecyclerView()
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

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.lv)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.VERTICAL
        }
        adapter = QuickAdapter()
        recyclerView.adapter = adapter
        val divider = DividerItemDecoration(this, RecyclerView.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.recyclerview_fgx)?.let(divider::setDrawable)
        recyclerView.addItemDecoration(divider)
        adapter.addHeaderView(View(this))
        adapter.setOnItemChildClickListener(OnItemChildClickListener { baseQuickAdapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
            Log.e(LOG_TAG, "onItemChildClick: $position")
            @Suppress("UNCHECKED_CAST")
            val result = baseQuickAdapter.data[position] as SearchResult
            onItemChildClick(view, result)
        })
    }

    private fun onItemChildClick(view: View, searchResult: SearchResult) {
        when (view.id) {
            R.id.btn_dk -> {
                AlertDialog.Builder(selfContext)
                    .setTitle(R.string.tips)
                    .setMessage(R.string.Confirm_disconnection)
                    .setPositiveButton(getString(R.string.ok)) { _: DialogInterface, _: Int ->
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
        for (searchResult in adapter.data) {
            if (App.getBle().getConnectStatus(searchResult.address) == CONNECTED_STATUS) {
                connectedDevices.add(searchResult)
            }
        }
        adapter.setNewData(connectedDevices)
        ble()
    }

    private fun disconnect(searchResult: SearchResult) {
        App.getBle().disconnect(searchResult.address)
        Timer().schedule(DISCONNECT_RETRY_DELAY_MS) {
            App.getBle().disconnect(searchResult.address)
        }
        Timer().schedule(DISCONNECT_REFRESH_DELAY_MS) {
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
                timer?.cancel()
                timer = Timer().apply {
                    schedule(1_000L, 1_500L) {
                        runOnUiThread { adapter.notifyDataSetChanged() }
                    }
                }
            }

            override fun onDeviceFounded(searchResult: SearchResult) {
                val type = searchResult.device?.type ?: return
                val name = searchResult.name
                if (name == "NULL" || type == DEVICE_TYPE_CLASSIC || type == DEVICE_TYPE_DUAL || searchResult.rssi == 0) {
                    return
                }
                val beacon = Beacon(searchResult.scanRecord)
                Log.e(LOG_TAG, "onDeviceFounded: $beacon")
                val existing = adapter.data.firstOrNull { it.address == searchResult.address }
                if (existing == null) {
                    adapter.data.add(searchResult)
                    adapter.notifyDataSetChanged()
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
        timer?.cancel()
        timer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        App.getBle().unregisterBluetoothStateListener(bluetoothStateListener)
        dismissProgress()
        timer?.cancel()
        timer = null
        for (searchResult in adapter.data) {
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
