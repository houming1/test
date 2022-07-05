package com.powercore.hydrahome.ui.activity.chargingprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityAssignProfileBinding
import com.powercore.hydrahome.databinding.ActivityChargingProfileBinding
import com.powercore.hydrahome.net.entity.rsp.ProfileListRsp
import com.powercore.hydrahome.widget.PopAssignChargingProfilePicker
import com.powercore.hydrahome.widget.PopProfileTimePicker

class AssignProfileActivity :
    BaseDbVmActivity<ActivityAssignProfileBinding, AssignProfileViewModel>(
        title = "Assign Charging Profile",
        titleBarColor = getColor(R.color.white),
    ) {
    var hydraHomeHouseholdChargeBoxPk = ""
    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
        hydraHomeHouseholdChargeBoxPk =
            intent.getStringExtra("hydraHomeHouseholdChargeBoxPk").toString()
    }

    override fun initClick() {
        mViewModel.apply {
            profileRvConfig.getAdapter().addChildClickViewIds(R.id.bind)
            profileRvConfig.getAdapter().setOnItemChildClickListener { adapter, view, position ->
                var data = adapter.data as MutableList<ProfileListRsp>
                XPopup.Builder(this@AssignProfileActivity).asCustom(
                    PopAssignChargingProfilePicker(this@AssignProfileActivity).apply {
                        setCallBack(data[position].hydraHomeHouseholdProfilePk!!, okBlock = {
                            mViewModel.assignChargeBoxProfile(hydraHomeHouseholdChargeBoxPk, it)
                        })
                    }).show()
            }
        }

    }

    override fun loadData() {
        mViewModel.getHouseholdProfileList()
    }

    override fun initObserver() {

    }

}