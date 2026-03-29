package com.uct

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.media.SoundPool
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.github.mikephil.charting.charts.LineChart
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleConnectResponse
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.inuker.bluetooth.library.model.BleGattProfile
import com.inuker.bluetooth.library.model.BleGattService
import com.officialwebsite.R
import com.uct.adapter.RljsAdapter
import com.uct.base.BaseActivity
import com.uct.entity.CmdEntity
import com.uct.entity.CurrentVoltageEntity
import com.uct.entity.RljsEntity
import com.uct.protocol.FnirsiProtocol
import com.uct.utlis.Global
import com.uct.utlis.PreferenceUtil
import com.uct.utlis.byteToHexString
import com.uct.utlis.byteToHexStringAddSpace
import com.uct.utlis.byteToInt2
import com.uct.utlis.bytesToInt
import com.uct.utlis.getCmd
import com.uct.utlis.hexString2Bytes
import com.uct.utlis.intToByte2LH
import com.uct.weight.ShadowContainer
import java.text.DecimalFormat
import java.util.Date
import java.util.UUID

@Suppress("DEPRECATION")
class BleDetailActivity : BaseActivity(), View.OnClickListener {
    private var NAME: String? = null
    private var mac: String? = null

    private lateinit var btn_ch1: Button
    private lateinit var btn_ch2: Button
    private lateinit var btn_yx: Button
    private lateinit var cb_dcdl: CheckBox
    private lateinit var cb_xl: CheckBox
    private lateinit var fv: LineChart
    private lateinit var ll_rljs: LinearLayout
    private lateinit var ll_xykz: LinearLayout
    private lateinit var ll_zxt: LinearLayout
    private lateinit var rg_select: RadioGroup
    private lateinit var rg_zykz: RadioGroup
    private lateinit var rljsAdapter: RljsAdapter
    private lateinit var sb_dy: SeekBar
    private lateinit var sc_start: ShadowContainer
    private lateinit var tv_amax: TextView
    private lateinit var tv_amin: TextView
    private lateinit var tv_av: TextView
    private lateinit var tv_cfzt: TextView
    private lateinit var tv_dev_name: TextView
    private lateinit var tv_djdy: TextView
    private lateinit var tv_djjdy: TextView
    private lateinit var tv_dljl: TextView
    private lateinit var tv_dxnz: TextView
    private lateinit var tv_fw: TextView
    private lateinit var tv_gl: TextView
    private lateinit var tv_group_no: TextView
    private lateinit var tv_nljl: TextView
    private lateinit var tv_pv: TextView
    private lateinit var tv_run: TextView
    private lateinit var tv_sbkjsj: TextView
    private lateinit var tv_sbwd: TextView
    private lateinit var tv_sj: TextView
    private lateinit var tv_sjjl: TextView
    private lateinit var tv_sn: TextView
    private lateinit var tv_start: TextView
    private lateinit var tv_unit: TextView
    private lateinit var tv_vmax: TextView
    private lateinit var tv_vmin: TextView
    private lateinit var tv_xyh1: TextView
    private lateinit var tv_xyh2: TextView
    private lateinit var tv_zdcdy: TextView
    private lateinit var tv_zxl: TextView

    private var dlsx = 0.0
    private var dydyfalg = false
    private var dysx = 0.0
    private var dyxx = 0.0
    private var groupMax = 0
    private var progressDialog: ProgressDialog? = null
    private var readUUID: UUID? = null
    private var redserviceUUID: UUID? = null
    private var sendUUID: UUID? = null
    private var sendserviceUUID: UUID? = null
    private var soundId = 0
    private var sp: SoundPool? = null
    private var detailReady = false
    private var zdfalg = false
    private var sessionHandshakeSent = false
    private var deviceInfoReceived = false
    private var protocolInfoReceived = false
    private var missingInfoRefreshSent = false

    private var sj = 1
    private var vaDjjFalg = true
    private var zykzFalg = 0
    private var ch1Falg = true
    private var ch2Falg = true
    private var startFalg = false

    private val cves: MutableList<CurrentVoltageEntity> = ArrayList()
    private val djjs: MutableList<CurrentVoltageEntity> = ArrayList()
    private val nowcves: MutableList<CurrentVoltageEntity> = ArrayList()
    private var mina = Int.MAX_VALUE
    private var maxa = Int.MIN_VALUE
    private var minv = Int.MAX_VALUE
    private var maxv = Int.MIN_VALUE
    private var djmin = Int.MAX_VALUE
    private var djmax = Int.MIN_VALUE
    private var djjmin = Int.MAX_VALUE
    private var djjmax = Int.MIN_VALUE
    private var tem = Double.MIN_VALUE
    private val beishu = 10000

    private val mDecimalFormat = DecimalFormat("0.00")
    private val mDecimalFormat3 = DecimalFormat("0.000")
    private val mDecimalFormat4 = DecimalFormat("0.0000")
    private val wdFormat = DecimalFormat("0.0")

    private val bleNotifyResponse = object : BleNotifyResponse {
        override fun onNotify(service: UUID, character: UUID, value: ByteArray) {
            Log.e("onNotify", "onNotify: ${byteToHexStringAddSpace(value)}")
            for (cmdEntity in FnirsiProtocol.extractCommands(value)) {
                handle(cmdEntity)
            }
        }

        override fun onResponse(code: Int) {
            Log.e(TAG, "notify onResponse: $code")
            if (code == 0) {
                setDetailReady(true)
            } else {
                Toast.makeText(this@BleDetailActivity, "Enable notify failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val bleConnectStatusListener = object : BleConnectStatusListener() {
        override fun onConnectStatusChanged(address: String, status: Int) {
            dismisspg()
            if (status == 32) {
                Toast.makeText(this@BleDetailActivity, getString(R.string.conn_dis), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun getRootLayout(): Int = R.layout.activity_bledetail

    override fun process(bundle: Bundle?) {
        mac = intent.getStringExtra("MAC")
        NAME = intent.getStringExtra("NAME")
        showpg("")
        initBle()
    }

    override fun initView() {
        rightText.setText(R.string.start_up)
        rightText.visibility = View.VISIBLE

        tv_av = fv(R.id.tv_av)
        tv_pv = fv(R.id.tv_pv)
        tv_gl = fv(R.id.tv_gl)
        tv_dxnz = fv(R.id.tv_dxnz)
        tv_dxnz.text = "${getString(R.string.Equivalent_internal_resistance)}:5.0000Ω"
        tv_sbwd = fv(R.id.tv_sbwd)
        tv_sbwd.text = "${getString(R.string.dev_tem)}25.0"
        tv_djjdy = fv(R.id.tv_djjdy)
        tv_djdy = fv(R.id.tv_djdy)
        tv_xyh1 = fv(R.id.tv_xyh1)
        tv_xyh2 = fv(R.id.tv_xyh2)
        tv_sjjl = fv(R.id.tv_sjjl)
        tv_sjjl.text = "${getString(R.string.Time_record)}000:00:00"
        tv_dljl = fv(R.id.tv_dljl)
        tv_dljl.text = "${getString(R.string.Electricity_record)}0.00000 Ah"
        tv_nljl = fv(R.id.tv_nljl)
        tv_nljl.text = "${getString(R.string.Capacity_recording)}0.00000 Wh"
        tv_group_no = fv(R.id.tv_group_no)
        tv_group_no.setOnClickListener(this)
        fv<Button>(R.id.btn_qc).setOnClickListener(this)
        ll_rljs = findView(R.id.ll_rljs)
        tv_sn = fv(R.id.tv_sn)
        tv_run = fv(R.id.tv_run)
        tv_fw = fv(R.id.tv_fw)
        tv_sbkjsj = fv(R.id.tv_sbkjsj)

        val dydy = PreferenceUtil.getInstance(this).getDydy("4,24,5")
        Log.e(TAG, "initView: dydy$dydy")
        val thresholds = dydy.split(",")
        dyxx = thresholds.getOrNull(0)?.toDoubleOrNull() ?: 4.0
        dysx = thresholds.getOrNull(1)?.toDoubleOrNull() ?: 24.0
        dlsx = thresholds.getOrNull(2)?.toDoubleOrNull() ?: 5.0
        dydyfalg = PreferenceUtil.getInstance(this).getDydyfalg(false)
        zdfalg = PreferenceUtil.getInstance(this).getZdfalg(false)
        Log.e(TAG, "initView: dydy$dydy  dydyfalg:$dydyfalg")
        fv<Button>(R.id.btn_gyglbj).setOnClickListener(this)

        fv = fv(R.id.lc_av)
        rg_select = fv(R.id.rg_select)
        rg_select.setOnCheckedChangeListener { _, checkedId ->
            vaDjjFalg = checkedId == R.id.rb_1
        }
        sc_start = fv(R.id.sc_start)
        tv_start = fv(R.id.tv_start)
        tv_start.setOnClickListener { start() }

        tv_dev_name = findView(R.id.tv_dev_name)
        if (!NAME.isNullOrBlank()) {
            tv_dev_name.text = NAME
        }
        tv_amin = fv(R.id.tv_amin)
        tv_amax = fv(R.id.tv_amax)
        tv_vmin = fv(R.id.tv_vmin)
        tv_vmax = fv(R.id.tv_vmax)
        tv_sj = fv(R.id.tv_sj)
        tv_sj.text = "$sj s / div"
        fv<View>(R.id.btn_sj_jj).setOnClickListener(this)
        fv<View>(R.id.btn_sj_j).setOnClickListener(this)
        fv<View>(R.id.btn_xykz_help).setOnClickListener { helpAlert(getString(R.string.xylz_hep)) }

        btn_yx = fv(R.id.btn_yx)
        btn_yx.tag = false
        btn_yx.setOnClickListener(this)
        ll_zxt = fv(R.id.ll_zxt)
        ll_xykz = fv(R.id.ll_xykz)
        fv<View>(R.id.btn_kz).setOnClickListener(this)
        fv<View>(R.id.btn_fhzxt).setOnClickListener(this)
        sb_dy = fv(R.id.sb_dy)
        rg_zykz = fv(R.id.rg_zykz)
        val radioButton: RadioButton = fv(R.id.rb_qc1)
        rg_zykz.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_afc -> {
                    zykzFalg = 3
                    sb_dy.max = 2
                    sb_dy.progress = 0
                }

                R.id.rb_fcp -> {
                    zykzFalg = 2
                    sb_dy.max = 2
                    sb_dy.progress = 0
                }

                R.id.rb_qc1 -> {
                    zykzFalg = 0
                    sb_dy.max = 3
                    sb_dy.progress = 0
                }

                R.id.rb_qc2 -> {
                    zykzFalg = 1
                    sb_dy.max = 85
                    sb_dy.progress = 10
                }
            }
        }
        radioButton.isChecked = true
        fv<View>(R.id.btn_dy_jian).setOnClickListener(this)
        fv<View>(R.id.btn_dy_jia).setOnClickListener(this)
        val tv_cfdyxykz: TextView = fv(R.id.tv_cfdyxykz)
        tv_cfdyxykz.tag = "5"
        sb_dy.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val label =
                        when (zykzFalg) {
                            0 -> when (progress) {
                                0 -> "5"
                                1 -> "9"
                                2 -> "12"
                                3 -> "20"
                                else -> "0"
                            }

                            1 -> mDecimalFormat.format((progress * 0.2) + 3.0)
                            else -> when (progress) {
                                0 -> "5"
                                1 -> "9"
                                2 -> "12"
                                else -> "0"
                            }
                        }
                    tv_cfdyxykz.text = "${label}V"
                    tv_cfdyxykz.tag = label
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

                override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
            },
        )
        fv<View>(R.id.btn_tzcf).setOnClickListener {
            showCapacityStopDialog(this) {
                tv_cfzt.text = "${resources.getText(R.string.now_state)}${resources.getString(R.string.cfsfz)}"
                sendCmd(getCmd((-121).toByte(), null))
            }
        }
        fv<View>(R.id.btn_cf).setOnClickListener {
            showCapacityStartDialog(this) {
                tv_cfzt.text = "${resources.getText(R.string.now_state)}${resources.getString(R.string.cfqqz)}"
                sendCmd(buildCapacityStartCommand(getSelected(), tv_cfdyxykz.tag.toString()))
            }
        }
        tv_cfzt = fv(R.id.tv_cfzt)
        fv<View>(R.id.btn_rljs_help).setOnClickListener { helpAlert(getString(R.string.rljs_hep)) }
        fv<View>(R.id.btn_rljs).setOnClickListener(this)
        fv<View>(R.id.btn_fhzxtrl).setOnClickListener {
            ll_rljs.visibility = View.GONE
            ll_zxt.visibility = View.VISIBLE
        }

        val recyclerView: RecyclerView = fv(R.id.rv_rljs)
        recyclerView.layoutManager = LinearLayoutManager(this).apply { orientation = RecyclerView.VERTICAL }
        rljsAdapter = RljsAdapter()
        recyclerView.adapter = rljsAdapter
        rljsAdapter.setOnItemClickListener(
            OnItemClickListener { _: BaseQuickAdapter<*, *>, _: View, position: Int ->
                val rljsEntity = rljsAdapter.data[position]
                showRljsEditDialog(this, rljsEntity) { dcdy, efficiencyPercent ->
                    rljsEntity.dcdy = dcdy
                    rljsEntity.xl = efficiencyPercent / 100.0
                    rljsAdapter.notifyDataSetChanged()
                }
            },
        )

        tv_zdcdy = fv(R.id.tv_zdcdy)
        tv_zdcdy.text = "3.7"
        tv_zxl = fv(R.id.tv_zxl)
        tv_zxl.text = "90"
        tv_zdcdy.setOnClickListener { setzxldcdy() }
        tv_zxl.setOnClickListener { setzxldcdy() }
        cb_xl = fv(R.id.cb_xl)
        cb_dcdl = fv(R.id.cb_dcdy)
        cb_xl.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            val efficiencyPercent = tv_zxl.text.toString().trim().toDoubleOrNull() ?: return@setOnCheckedChangeListener
            Log.e(TAG, "setOnCheckedChangeListener:cb_xl: ${cb_xl.isChecked}   $efficiencyPercent")
            if (cb_xl.isChecked) {
                rljsxkdygg(-1.0, efficiencyPercent / 100.0)
            }
        }
        cb_dcdl.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            val dcdy = tv_zdcdy.text.toString().trim().toDoubleOrNull() ?: return@setOnCheckedChangeListener
            if (cb_dcdl.isChecked) {
                rljsxkdygg(dcdy, -1.0)
            }
        }

        btn_ch1 = fv(R.id.btn_ch1)
        btn_ch1.setOnClickListener(this)
        btn_ch2 = fv(R.id.btn_ch2)
        btn_ch2.setOnClickListener(this)
        tv_unit = fv(R.id.tv_unit)
        tv_unit.tag = false
        tv_unit.setOnClickListener(this)
        sp = SoundPool(5, 3, 0)
        soundId = sp?.load(this, R.raw.duka3, 1) ?: 0

        zxt(emptyList(), fv, true)
        setDetailReady(detailReady)
    }

    private fun rljs() {
        ll_rljs.visibility = View.VISIBLE
        ll_zxt.visibility = View.GONE
        ll_xykz.visibility = View.GONE
        rljsAdapter.setNewData(ArrayList())
        sendCmd(getCmd((-118).toByte(), null))
    }

    private fun wdunit() {
        val fahrenheit = !((tv_unit.tag as? Boolean) ?: false)
        tv_unit.tag = fahrenheit
        tv_unit.text = if (fahrenheit) "℉" else "℃"
        if (tem != Double.MIN_VALUE) {
            tv_sbwd.text = "${getString(R.string.dev_tem)}:${getTem(tem)}"
        }
    }

    private fun ch2(button: Button) {
        if (ch2Falg) {
            button.background = ContextCompat.getDrawable(this, R.drawable.tab_label_normal)
            button.setTextColor(ViewCompat.MEASURED_STATE_MASK)
        } else {
            button.background = ContextCompat.getDrawable(this, R.drawable.tab_label_pressed)
            button.setTextColor(-1)
        }
        ch2Falg = !ch2Falg
    }

    private fun ch1(button: Button) {
        if (ch1Falg) {
            button.background = ContextCompat.getDrawable(this, R.drawable.tab_label_normal)
            button.setTextColor(ViewCompat.MEASURED_STATE_MASK)
        } else {
            button.background = ContextCompat.getDrawable(this, R.drawable.tab_label_pressed)
            button.setTextColor(-1)
        }
        ch1Falg = !ch1Falg
    }

    private fun kz(linearLayout: LinearLayout, linearLayout2: LinearLayout, linearLayout3: LinearLayout) {
        linearLayout3.visibility = View.VISIBLE
        linearLayout2.visibility = View.GONE
        linearLayout.visibility = View.GONE
        tv_cfzt.text = "${resources.getText(R.string.now_state)}${resources.getString(R.string.state_getstate)}"
        sendCmd(getCmd((-123).toByte(), null))
    }

    private fun yx() {
        val currentState = (btn_yx.tag as? Boolean) ?: false
        btn_yx.text =
            if (currentState) {
                getString(R.string.function)
            } else {
                getString(R.string.stop_it)
            }
        btn_yx.tag = !currentState
    }

    private fun disableRadioGroup(radioGroup: RadioGroup, enabled: Boolean) {
        for (index in 0 until radioGroup.childCount) {
            radioGroup.getChildAt(index).isEnabled = enabled
        }
    }

    private fun setzxldcdy() {
        showDefaultRljsDialog(this, tv_zxl.text.toString().trim(), tv_zdcdy.text.toString().trim()) { dcdy, efficiencyPercent ->
            tv_zdcdy.text = dcdy.toString()
            tv_zxl.text = efficiencyPercent.toString()
            if (cb_xl.isChecked) {
                rljsxkdygg(-1.0, efficiencyPercent / 100.0)
            }
            if (cb_dcdl.isChecked) {
                rljsxkdygg(dcdy, -1.0)
            }
        }
    }

    private fun rljsxkdygg(dcdy: Double, efficiency: Double) {
        for (rljsEntity in rljsAdapter.data) {
            if (dcdy != -1.0) {
                rljsEntity.dcdy = dcdy
            }
            if (efficiency != -1.0) {
                rljsEntity.xl = efficiency
            }
        }
        rljsAdapter.notifyDataSetChanged()
    }

    private fun getSelected(): Int {
        val selected =
            (0 until rg_zykz.childCount)
                .map { rg_zykz.getChildAt(it) as RadioButton }
                .firstOrNull { it.isChecked }
                ?: return 0
        return when (selected.id) {
            R.id.rb_afc -> 3
            R.id.rb_fcp -> 2
            R.id.rb_qc2 -> 1
            else -> 0
        }
    }

    override fun menu() {
        finish()
    }

    override fun right() {
        start()
    }

    private fun start() {
        if (!detailReady) {
            Toast.makeText(this, "Device is still connecting", Toast.LENGTH_SHORT).show()
            return
        }
        if (!startFalg) {
            disableRadioGroup(rg_select, true)
            sendCmd(getCmd((-126).toByte(), null))
            rightText.text = getString(R.string.stop_it)
            tv_start.text = getString(R.string.stop_it)
            sc_start.setcolor(ContextCompat.getColor(this, R.color.red))
            tv_start.setTextColor(ContextCompat.getColor(this, R.color.red))
        } else {
            disableRadioGroup(rg_select, false)
            sendCmd(getCmd((-124).toByte(), null))
            rightText.text = getString(R.string.start_up)
            tv_start.text = getString(R.string.start_up)
            tv_start.setTextColor(ContextCompat.getColor(this, R.color.start_color))
            sc_start.setcolor(ContextCompat.getColor(this, R.color.start_color))
        }
        startFalg = !startFalg
    }

    private fun setDetailReady(ready: Boolean) {
        detailReady = ready
        rightText.isEnabled = ready
        rightText.alpha = if (ready) 1.0f else 0.5f
        if (::tv_start.isInitialized) {
            tv_start.isEnabled = ready
            tv_start.alpha = if (ready) 1.0f else 0.5f
        }
        if (::sc_start.isInitialized) {
            sc_start.isEnabled = ready
            sc_start.alpha = if (ready) 1.0f else 0.5f
        }
    }

    private fun initBle() {
        Log.e(TAG, "initBle: ")
        sessionHandshakeSent = false
        deviceInfoReceived = false
        protocolInfoReceived = false
        missingInfoRefreshSent = false
        val cachedProfile = mac?.let { Global.bleGattProfileMap[it] }
        val address = mac ?: return
        val connectStatus = App.getBle().getConnectStatus(address)
        Log.e(TAG, "initBle: status=$connectStatus cachedProfile=${cachedProfile != null} mac=$address")
        if (connectStatus == 2) {
            Log.e(TAG, "initBle: forcing reconnect for detail session")
            App.getBle().clearRequest(address, 0)
            App.getBle().disconnect(address)
            window.decorView.postDelayed({ connectDetailDevice(cachedProfile) }, 400L)
            return
        }
        connectDetailDevice(cachedProfile)
    }

    private fun connectDetailDevice(cachedProfile: BleGattProfile?) {
        val address = mac ?: return
        val connectOptions =
            BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(30_000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(20_000)
                .build()
        Log.e(TAG, "connectDetailDevice: 开始连接")
        App.getBle().connect(
            address,
            connectOptions,
            object : BleConnectResponse {
                override fun onResponse(code: Int, bleGattProfile: BleGattProfile?) {
                    Log.e(TAG, "connect onResponse: $code profile=${bleGattProfile != null} cachedFallback=${cachedProfile != null}")
                    val resolvedProfile = bleGattProfile ?: cachedProfile
                    if (code == 0 && resolvedProfile != null) {
                        getMessage(resolvedProfile)
                        Global.bleGattProfileMap[address] = resolvedProfile
                        return
                    }
                    Toast.makeText(this@BleDetailActivity, R.string.conn_dis, Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
        )
    }

    private fun getMessage(bleGattProfile: BleGattProfile?) {
        dismisspg()
        Log.e(TAG, "getMessage: profile=${bleGattProfile != null}")
        var notifyService: BleGattService? = null
        var writeService: BleGattService? = null
        if (bleGattProfile != null) {
            notifyService = bleGattProfile.getService(UUID.fromString(FNIRSI_NOTIFY_SERVICE_UUID))
            writeService = bleGattProfile.getService(UUID.fromString(FNIRSI_WRITE_SERVICE_UUID))
            Log.e(
                TAG,
                "getMessage: notifyService=${notifyService != null} writeService=${writeService != null} serviceCount=${bleGattProfile.services.size}",
            )
        }
        if (notifyService == null || writeService == null) {
            Log.e(TAG, "getMessage: falling back to fixed FNIRSI UUIDs")
        }
        redserviceUUID = UUID.fromString(FNIRSI_NOTIFY_SERVICE_UUID)
        sendserviceUUID = UUID.fromString(FNIRSI_WRITE_SERVICE_UUID)
        sendUUID = UUID.fromString(FNIRSI_WRITE_CHAR_UUID)
        readUUID = UUID.fromString(FNIRSI_NOTIFY_CHAR_UUID)
        Log.e(TAG, "getMessage: notify $redserviceUUID / $readUUID")
        App.getBle().notify(mac, redserviceUUID, readUUID, bleNotifyResponse)
        if (!sessionHandshakeSent) {
            sessionHandshakeSent = true
            setDetailReady(true)
            sendCmd(getCmd((-127).toByte(), null))
        }
        App.getBle().registerConnectStatusListener(mac, bleConnectStatusListener)
    }

    private fun handle(date: CmdEntity) {
        Log.e(TAG, "handle: $date")
        when (date.cmd.toInt() and 0xFF) {
            1 -> analysis01(date.date)
            2 -> analysis02(date.date)
            3 -> analysis03(date.date)
            4 -> analysis04(date.date)
            5 -> analysis05(date.date)
            6 -> analysis06(date.date)
            7 -> analysis07(date.date)
            8 -> analysis08(date.date)
            9 -> analysis09(date.date)
            10 -> analysis0A(date.date)
        }
    }

    private fun analysis01(bytes: ByteArray) {
        val value = bytes[0]
        if (value == (-122).toByte() || value == (-121).toByte()) {
            sendCmd(getCmd((-123).toByte(), null))
        }
    }

    private fun analysis02(bytes: ByteArray) {
        val value = bytes[0]
        if (value == (-122).toByte() || value == (-121).toByte()) {
            sendCmd(getCmd((-123).toByte(), null))
        }
    }

    private fun analysis0A(bytes: ByteArray) {
        val group = bytes[0].toInt() and 0xFF
        val resistanceBytes = bytes.copyOfRange(1, 5)
        val energyBytes = bytes.copyOfRange(5, 9)
        rljsAdapter.addData(RljsEntity(group, bytesToInt(resistanceBytes, 0), bytesToInt(energyBytes, 0), 0, 0))
    }

    private fun analysis09(bytes: ByteArray) {
        val state = bytes[0].toInt() and 0xFF
        val label = Global.cfxyh.getOrElse(state) { Global.cfxyh[0] }
        Log.e(TAG, "analysis09: $label")
        tv_cfzt.text =
            if (state > 0) {
                "${resources.getText(R.string.now_state)}${resources.getString(R.string.state_runing)}"
            } else {
                "${resources.getText(R.string.now_state)}${resources.getString(R.string.state_kx)}"
            }
    }

    private fun analysis08(bytes: ByteArray) {
        Log.e("analysis08", "analysis08: ${byteToHexString(bytes)}")
        val group = bytes[0].toInt() and 0xFF
        val nljBytes = bytes.copyOfRange(1, 5)
        val dljBytes = bytes.copyOfRange(5, 9)
        val recordTimeBytes = bytes.copyOfRange(9, 13)
        val powerOnBytes = bytes.copyOfRange(13, 17)
        val nlj = bytesToInt(nljBytes, 0)
        val dlj = bytesToInt(dljBytes, 0)
        val recordTime = bytesToInt(recordTimeBytes, 0)
        val powerOnTime = bytesToInt(powerOnBytes, 0)

        tv_group_no.text = "$group/$groupMax"
        tv_group_no.tag = group
        tv_nljl.text = "${getString(R.string.Capacity_recording)} ${Global.get6Num((nlj / 100000.0).toString())}Wh"
        tv_dljl.text = "${getString(R.string.Electricity_record)} ${Global.get6Num((dlj / 100000.0).toString())}Ah"
        tv_sjjl.text = "${getString(R.string.Time_record)} ${Global.getMinute(recordTime.toLong())}"
        tv_sbkjsj.text = "${getString(R.string.Power_on_time_of_capacity_equipment)} ${Global.getHMinute(powerOnTime.toLong())}"
        Log.e("analysis08", "analysis08: sbkjsji:$recordTime   $powerOnTime")
    }

    private fun analysis07(bytes: ByteArray) {
        val voltage = byteToInt2(bytes.copyOfRange(0, 2))
        val current = byteToInt2(bytes.copyOfRange(2, 4))
        Log.e("qIBusi", "analysis07: $current  ${byteToHexString(bytes.copyOfRange(2, 4))}")
        cves.add(CurrentVoltageEntity(current, voltage, Date()))
        if (((btn_yx.tag as? Boolean) == true) && vaDjjFalg) {
            val windowSize = sj * 150
            val visibleValues = cves.subList(if (cves.size > windowSize) cves.size - windowSize else 0, cves.size)
            mina = Int.MAX_VALUE
            maxa = Int.MIN_VALUE
            minv = Int.MAX_VALUE
            maxv = Int.MIN_VALUE
            for (item in visibleValues) {
                val itemVoltage = item.voltage
                val itemCurrent = item.current
                if (itemVoltage < minv) minv = itemVoltage
                if (itemVoltage > maxv) maxv = itemVoltage
                if (itemCurrent < mina) mina = itemCurrent
                if (itemCurrent > maxa) maxa = itemCurrent
            }
            tv_amin.text = getdouNkxjs(mina / 1000.0)
            tv_amax.text = getdouNkxjs(maxa / 1000.0)
            tv_vmin.text = getdouNkxjs(minv / 1000.0)
            tv_vmax.text = getdouNkxjs(maxv / 1000.0)
            nowcves.clear()
            if (cves.size % sj == 0) {
                var index = 0
                while (index < visibleValues.size) {
                    nowcves.add(visibleValues[index])
                    index += sj
                }
                zxt(nowcves, fv, false)
            }
        }
    }

    private fun analysis06(bytes: ByteArray) {
        protocolInfoReceived = true
        val djdy = byteToInt2(bytes.copyOfRange(0, 2)).toDouble()
        val djjdy = byteToInt2(bytes.copyOfRange(2, 4)).toDouble()
        val protocol1 = bytes[4].toInt() and 0xFF
        val protocol2 = bytes[5].toInt() and 0xFF
        tv_djdy.text = "${mDecimalFormat3.format(djdy / 1000.0)}V"
        tv_djjdy.text = "${mDecimalFormat3.format(djjdy / 1000.0)}V"
        tv_xyh1.text = Global.sbxyhb.getOrElse(protocol1) { Global.sbxyhb[0] }
        tv_xyh2.text = Global.sbxyhb.getOrElse(protocol2) { Global.sbxyhb[0] }
        djjs.add(CurrentVoltageEntity(byteToInt2(bytes.copyOfRange(2, 4)), byteToInt2(bytes.copyOfRange(0, 2)), Date()))
        if (((btn_yx.tag as? Boolean) == true) && !vaDjjFalg) {
            val visibleValues = djjs.subList(if (djjs.size > 150) djjs.size - 150 else 0, djjs.size)
            for (item in visibleValues) {
                val itemVoltage = item.voltage
                val itemCurrent = item.current
                if (itemVoltage < djmin) djmin = itemVoltage
                if (itemVoltage > djmax) djmax = itemVoltage
                if (itemCurrent < djjmin) djjmin = itemCurrent
                if (itemCurrent > djjmax) djjmax = itemCurrent
            }
            tv_vmin.text = getdouNkxjs(djmin / 1000.0)
            tv_vmax.text = getdouNkxjs(djmax / 1000.0)
            tv_amin.text = getdouNkxjs(djjmin / 1000.0)
            tv_amax.text = getdouNkxjs(djjmax / 1000.0)
            nowcves.clear()
            nowcves.addAll(visibleValues)
            if (djjs.size % sj == 0) {
                var index = 0
                while (index < visibleValues.size) {
                    nowcves.add(visibleValues[index])
                    index += sj
                }
                zxt(nowcves, fv, true)
            }
        }
    }

    private fun analysis05(bytes: ByteArray) {
        val resistance = bytesToInt(bytes.copyOfRange(0, 4), 0).toDouble()
        val sign = if (bytes[4] > 0) 1 else -1
        val temperatureRaw = (sign * byteToInt2(bytes.copyOfRange(5, 7))).toDouble()
        tv_dxnz.text = "${getString(R.string.Equivalent_internal_resistance)}:${resistance / beishu}Ω"
        tv_sbwd.text = "${getString(R.string.dev_tem)}:${getTem(temperatureRaw / 10.0)}"
    }

    private fun getTem(value: Double): String {
        tem = value
        val fahrenheit = (tv_unit.tag as? Boolean) ?: false
        val fahrenheitValue = ((9.0 * value) / 5.0) + 32.0
        Log.e(TAG, "getTem: $fahrenheit  $fahrenheitValue")
        return if (fahrenheit) wdFormat.format(fahrenheitValue) else wdFormat.format(value)
    }

    private fun analysis04(bytes: ByteArray) {
        refreshMissingInfoIfNeeded()
        val voltage = bytesToInt(bytes.copyOfRange(0, 4), 0).toDouble()
        val current = bytesToInt(bytes.copyOfRange(4, 8), 0).toDouble()
        val power = bytesToInt(bytes.copyOfRange(8, 12), 0).toDouble()
        Log.e("analysis04", "analysis04: $current   ${byteToHexString(bytes.copyOfRange(4, 8))}")
        tv_pv.text = "${getdouNkxjs(voltage / beishu)}V"
        tv_av.text = "${getdouNkxjs(current / beishu)}A"
        tv_gl.text = "${getdouNkxjs(power / beishu)}W"
        if (dyxx > voltage / beishu || dysx < voltage / beishu || dlsx < current / beishu) {
            if (dydyfalg) {
                sp?.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
            }
            if (zdfalg) {
                (getSystemService(VIBRATOR_SERVICE) as? Vibrator)?.vibrate(200L)
            }
        }
    }

    private fun getdouNkxjs(value: Double): String = mDecimalFormat4.format(value)

    private fun analysis03(bytes: ByteArray) {
        deviceInfoReceived = true
        val modelBytes = bytes.copyOfRange(0, 2)
        val versionBytes = bytes.copyOfRange(2, 4)
        val serialBytes = bytes.copyOfRange(4, 8)
        val runBytes = bytes.copyOfRange(8, 12)
        groupMax = bytes[12].toInt() and 0xFF
        val currentGroup = bytes[13].toInt() and 0xFF
        tv_group_no.text = "$currentGroup/$groupMax"
        val serial = "SN:${String.format("%06d", bytesToInt(serialBytes, 0))}"
        Log.e("analysis03", "analysis03: $serial   run: ${byteToHexString(runBytes)}   SN: ${byteToHexString(serialBytes)}")
        tv_sn.text = serial
        tv_run.text = "Run:${bytesToInt(runBytes, 0)}"
        tv_fw.text = "Fw:V${mDecimalFormat.format(byteToInt2(versionBytes) / 100.0)}"
        when (byteToInt2(modelBytes)) {
            9 -> tv_dev_name.text = "FNIRSI-C1"
            38 -> tv_dev_name.text = "FNB38"
            48 -> tv_dev_name.text = "FNB48"
        }
    }

    private fun refreshMissingInfoIfNeeded() {
        if ((deviceInfoReceived && protocolInfoReceived) || missingInfoRefreshSent) {
            return
        }
        missingInfoRefreshSent = true
        Log.e(TAG, "refreshMissingInfoIfNeeded: requesting device info refresh")
        sendCmd(getCmd((-123).toByte(), null))
    }

    override fun onDestroy() {
        super.onDestroy()
        sendCmd(getCmd((-124).toByte(), null))
        App.getBle().unnotify(
            mac,
            redserviceUUID,
            readUUID,
            object : BleUnnotifyResponse {
                override fun onResponse(code: Int) {
                    Log.e(TAG, "onResponse: code  $code")
                }
            },
        )
        App.getBle().unregisterConnectStatusListener(mac, bleConnectStatusListener)
    }

    private fun sendCmd(value: String) {
        sendCmd(hexString2Bytes(value))
    }

    private fun sendCmd(value: ByteArray) {
        val serviceUuid = sendserviceUUID
        val characteristicUuid = sendUUID
        val address = mac
        if (serviceUuid == null || characteristicUuid == null || address == null) {
            Log.e(TAG, "sendCmd skipped: GATT not ready")
            return
        }
        Log.e("sendCmd", "sendCmd: ${byteToHexString(value)}")
        App.getBle().write(
            address,
            serviceUuid,
            characteristicUuid,
            value,
            object : BleWriteResponse {
                override fun onResponse(code: Int) {
                    Log.e(TAG, "write onResponse: $code")
                }
            },
        )
    }

    private fun showpg(message: String) {
        val dialog = progressDialog
        if (dialog == null) {
            progressDialog =
                ProgressDialog(this).apply {
                    setMessage(message)
                    show()
                }
            return
        }
        dialog.setMessage(message)
    }

    private fun dismisspg() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun zxt(list: List<CurrentVoltageEntity>, lineChart: LineChart, dpDmMode: Boolean) {
        renderChart(list, lineChart, dpDmMode, maxv, minv, maxa, mina, ch1Falg, ch2Falg, mDecimalFormat, this)
    }

    override fun onClick(view: View) {
        if (!startFalg) {
            Toast.makeText(this, R.string.wqdti, Toast.LENGTH_SHORT).show()
        }
        when (view.id) {
            R.id.btn_ch1 -> ch1(btn_ch1)
            R.id.btn_ch2 -> ch2(btn_ch2)
            R.id.btn_dy_jia -> sb_dy.progress = sb_dy.progress + 1
            R.id.btn_dy_jian -> sb_dy.progress = sb_dy.progress - 1
            R.id.btn_fhzxt -> {
                ll_xykz.visibility = View.GONE
                ll_zxt.visibility = View.VISIBLE
            }

            R.id.btn_gyglbj -> gyglbj()
            R.id.btn_kz -> kz(ll_rljs, ll_zxt, ll_xykz)
            R.id.btn_qc -> qc()
            R.id.btn_rljs -> rljs()
            R.id.btn_sj_j -> sj_jj(sj + 1)
            R.id.btn_sj_jj -> sj_jj(sj - 1)
            R.id.btn_yx -> yx()
            R.id.tv_group_no -> {
                val tag = tv_group_no.tag as? Int
                if (tag != null) {
                    val nextGroup = if (tag >= groupMax) 1 else tag + 1
                    tv_group_no.text = "$nextGroup/$groupMax"
                    tv_group_no.tag = nextGroup
                    sendCmd(getCmd((-120).toByte(), byteArrayOf(nextGroup.toByte())))
                }
            }

            R.id.tv_unit -> wdunit()
        }
    }

    private fun sj_jj(value: Int) {
        if (value >= 6 || value <= 0) {
            return
        }
        sj = value
        tv_sj.text = "$sj s / div"
    }

    private fun gyglbj() {
        showAlarmConfigDialog(this, dyxx, dysx, dlsx, dydyfalg, zdfalg) { result ->
            PreferenceUtil.getInstance(this).setDydy("${result.lowVoltage},${result.highVoltage},${result.highCurrent}")
            dyxx = result.lowVoltage
            dysx = result.highVoltage
            dlsx = result.highCurrent
            PreferenceUtil.getInstance(this).setDydyfalg(result.beepEnabled)
            PreferenceUtil.getInstance(this).setZdfalg(result.vibrateEnabled)
            Log.e(TAG, "init111111111View: dydy${result.lowVoltage},${result.highVoltage},${result.highCurrent}  dydyfalg:${result.beepEnabled}")
            dydyfalg = result.beepEnabled
            zdfalg = result.vibrateEnabled
        }
    }

    private fun qc() {
        val group = tv_group_no.tag as? Int ?: return
        Log.e(TAG, "initView: $group")
        showClearGroupDialog(this, group) {
            sendCmd(buildClearGroupCommand(group))
        }
    }

    private fun helpAlert(message: String) {
        showHelpDialog(this, message)
    }

    private companion object {
        private const val TAG = "TAG"
        private const val FNIRSI_NOTIFY_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb"
        private const val FNIRSI_WRITE_SERVICE_UUID = "0000ffe5-0000-1000-8000-00805f9b34fb"
        private const val FNIRSI_NOTIFY_CHAR_UUID = "0000ffe4-0000-1000-8000-00805f9b34fb"
        private const val FNIRSI_WRITE_CHAR_UUID = "0000ffe9-0000-1000-8000-00805f9b34fb"
    }
}
