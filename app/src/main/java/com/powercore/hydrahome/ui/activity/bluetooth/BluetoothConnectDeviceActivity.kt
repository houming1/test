package com.powercore.hydrahome.ui.activity.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.fdf.base.ext.log
import com.fdf.base.ext.toast
import com.fdf.base.getString
import com.fdf.base.startActivity
import com.gyf.immersionbar.ImmersionBar
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityBluetoothConnectDeviceBinding
import com.powercore.hydrahome.utils.BlueToothUtils
import com.powercore.hydrahome.utils.BlufiUtils

class BluetoothConnectDeviceActivity : BaseDbVmActivity<ActivityBluetoothConnectDeviceBinding, BluetoothConnectDeviceViewModel>(
    title = getString(R.string.txt_bluetooth_connect_device),
    titleBarColor = getColor(R.color.white)
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
    }

    override fun init() {

    }

    override fun initClick() {
        mViewBind.next.setOnClickListener {
            if (!BlueToothUtils.isOpen()) {
                "Please turn on bluetooth".toast()
                return@setOnClickListener
            }
            startActivity<ChargeWhiteListActivity>()
        }
        mViewBind.swb.setOnCheckedChangeListener { buttonView, isChecked ->
            requestPermission(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN) {
                BlueToothUtils.changeStatus(isChecked) {
                    "是否打开:${it}".log()
                    if (!it)
                        BluetoothAdapter.getDefaultAdapter().enable()
                }
            }
        }
    }

    override fun loadData() {

    }

    override fun initObserver() {

    }
    override fun onDestroy() {
        BlufiUtils.stopScan(
            onLeScan = {
//                onLeScan(it)
            }, scanFailed = {
//                mViewBind.refreshLayout.finishRefresh()
            })
//        BlufiUtils.closeClient()
        super.onDestroy()
    }
}