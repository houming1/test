package com.powercore.hydrahome.ui.activity.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.fdf.base.ext.log
import com.fdf.base.ext.toast
import com.fdf.base.getString
import com.fdf.base.startActivity
import com.gyf.immersionbar.ImmersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityChargeWhiteListBinding
import com.powercore.hydrahome.ext.hiddenLoading
import com.powercore.hydrahome.ext.showLoading
import com.powercore.hydrahome.net.entity.rsp.BleBean
import com.powercore.hydrahome.net.entity.rsp.ChargingBean
import com.powercore.hydrahome.ui.adapter.BlueListAdapter
import com.powercore.hydrahome.ui.adapter.ChargePointAdapter
import com.powercore.hydrahome.utils.BlueUtils
import com.powercore.hydrahome.utils.BlufiUtils
import com.ruffian.library.widget.RTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ChargeWhiteListActivity :
    BaseDbVmActivity<ActivityChargeWhiteListBinding, ChargeWhiteListViewModel>(
        title = getString(R.string.txt_bluetooth_connect_device),
        titleBarColor = getColor(R.color.white)
    ) {
    var adapter: BlueListAdapter? = null
    private val mBlufiFilter = "BLUFI_"
    private var selectItem: ScanResult? = null

    private val locationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private var mLastScanTime = 0L

    override fun init() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.white)
            .autoDarkModeEnable(true)
            .init()
        mViewBind.vm = mViewModel
        mViewBind.refreshLayout.setEnableLoadMore(false)
        initRv()
        initWhiteList()
        mViewBind.refreshLayout.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.Main) {
                delay(2000)
                mViewBind.refreshLayout.finishRefresh()
            }
        }


    }

    override fun initClick() {

        mViewBind.btnNext.setOnClickListener {
            plus()
        }
    }

    override fun loadData() {
        startScan()
    }

    override fun initObserver() {
        LiveEventBus.get<Boolean>(LiveDataBusKeys.BLU_DISCONNECT).observe(this, {
            if (it) {
                startScan()
            }
        })
    }


    private fun initRv() {
        adapter = BlueListAdapter(this)
        mViewBind.rvChargePoint.layoutManager = LinearLayoutManager(this)
        mViewBind.rvChargePoint.adapter = adapter
        adapter!!.setOnItemClickListener { adapter1, view, position ->
            val data = adapter1.data as MutableList<BleBean>
            data.forEachIndexed { index, bleBean ->
                bleBean.checked = index == position
            }
            for (i in 0 until data.size) {
                data[i].checked = i == position
            }
            adapter!!.setList(data)
        }
        val emptyView = layoutInflater.inflate(R.layout.empty_blue_device_list, null)
        emptyView.findViewById<RTextView>(R.id.btn_refresh).setOnClickListener {
            mViewBind.refreshLayout.autoRefresh()
        }
        adapter!!.setEmptyView(emptyView)
        adapter!!.isUseEmpty = true
    }

    private fun startScan() {
        "开始扫描".log()
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
            mViewBind.refreshLayout.setEnableRefresh(true)
            BlufiUtils.scan(locationManager,
                scanFinish = {
                    "scanFinish".log()
                    mViewBind.refreshLayout.finishRefresh()
                },
                onLeScan = {
                    "onLeScan".log()
                    onLeScan(it)
                },
                scanFailed = {
                    "scanFailed".log()
                    mViewBind.refreshLayout.finishRefresh()
                }
            )
        }
    }

    fun plus() {
        val data = adapter!!.data as MutableList<BleBean>
        data.filter { it.checked }.forEach {
            selectItem = it.scanResult
        }
        "selectItem == null${selectItem == null}".log()
        if (selectItem == null)
            BlufiUtils.updateMessage("Please select a valid device", true)
        else {
            selectItem?.let { scanResult ->
                val connectAddrList =
                    BlueUtils.showBondedDevice(BluetoothAdapter.getDefaultAdapter())
                if (connectAddrList.isNullOrEmpty() || connectAddrList.none { it == scanResult.device.address }) {
                    showLoading(this@ChargeWhiteListActivity, "Connecting...")
                    mViewModel.validBle(scanResult.device.name.replace("BLUFI_", ""), {
                        "验证成功".log()
                        BlufiUtils.connect(scanResult.device,
                            onGattConnected = {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    hiddenLoading()
                                    /* LiveEventBus.get<Int>(LiveDataBusKeys.BLU_CONFIG_WIFI_STATUS)
                                         .post(1)*/
                                    startActivity<ChargeWhiteListEditActivity>()
                                    this@ChargeWhiteListActivity.finish()
                                }
                            },
                            onGattDisconnected = {
                                LiveEventBus.get<Boolean>(LiveDataBusKeys.BLU_DISCONNECT).post(true)
                                lifecycleScope.launch(Dispatchers.Main) {
                                    "Connection failed".toast()
                                    hiddenLoading()
                                }
                            })
                    }, {
                        "验证失败".log()
                        hiddenLoading()
                    })
                } else {
                    hiddenLoading()
                    startActivity<ChargeWhiteListEditActivity>()
                }

            }
        }
    }

    private fun onLeScan(scanResult: ScanResult) {
        val data = adapter!!.data
        launch {
            val name = scanResult.device.name
            if (name.isNullOrBlank() || !name.startsWith(mBlufiFilter))
                return@launch
            if (data.any { it.scanResult?.device?.address == scanResult.device.address })
                return@launch

            flow { emit(scanResult) }
                .flowOn(Dispatchers.IO)
                .filter { !data.any { it.scanResult?.device?.address == scanResult.device.address } }
                .collect {

                    launch(Dispatchers.Main) {
                        adapter!!.addData(BleBean(false, it))
                        initWhiteList()
                    }
                }
        }


    }

    private fun initWhiteList() {
        val bleList = adapter!!.data
        adapter!!.isUseEmpty = !bleList.isNotEmpty()
    }
}