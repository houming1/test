package com.powercore.hydrahome.widget

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil

import com.lxj.xpopup.core.CenterPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopOptionsPickerBinding
import com.powercore.hydrahome.net.entity.rsp.HomeChargeBox

import com.powercore.hydrahome.ui.activity.chargingprofile.AssignProfileActivity

class PopOptionsPicker(context: Context, var data: HomeChargeBox) : CenterPopupView(context) {
    private lateinit var bind: PopOptionsPickerBinding

    override fun getImplLayoutId(): Int {
        return R.layout.pop_options_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {
        bind.apply {
            connectToWifi.setOnClickListener {
                mConnectToWifiBlock.invoke(data)
                dismiss()
            }
            assignChargingProfile.setOnClickListener {
                var intent = Intent(context, AssignProfileActivity::class.java)
                intent.putExtra("hydraHomeHouseholdChargeBoxPk", data.hydraHomeHouseholdChargeBoxPk)
                context.startActivity(intent)
                dismiss()
            }
            deleteChargePoint.setOnClickListener {
                mDeleteChargePointBlock.invoke(data)
                dismiss()

            }
            rebootChargePoint.setOnClickListener {
                mRebootChargePointBlock.invoke(data)
                dismiss()

            }
        }

    }
    private var mConnectToWifiBlock: (HomeChargeBox) -> Unit = {}
    private var mDeleteChargePointBlock: (HomeChargeBox) -> Unit = {}
    private var mRebootChargePointBlock: (HomeChargeBox) -> Unit = {}
    fun setCallBack(
        connectToWifiBlock: (HomeChargeBox) -> Unit = {},
        deleteChargePointBlock: (HomeChargeBox) -> Unit = {},
        rebootChargeWifiBlock: (HomeChargeBox) -> Unit = {}
    ) {
        mConnectToWifiBlock = connectToWifiBlock
        mDeleteChargePointBlock = deleteChargePointBlock
        mRebootChargePointBlock = rebootChargeWifiBlock
    }
}