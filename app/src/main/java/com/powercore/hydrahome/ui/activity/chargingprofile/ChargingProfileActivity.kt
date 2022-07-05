package com.powercore.hydrahome.ui.activity.chargingprofile

import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.fdf.base.startActivity
import com.fdf.base.utils.startActivity
import com.gk.net.utils.toJson
import com.gyf.immersionbar.ImmersionBar
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityChargingProfileBinding
import com.powercore.hydrahome.net.entity.rsp.BleBean
import com.powercore.hydrahome.net.entity.rsp.ProfileItemRsp
import com.powercore.hydrahome.net.entity.rsp.ProfileListRsp

class ChargingProfileActivity :
    BaseDbVmActivity<ActivityChargingProfileBinding, ChargingProfileViewModel>(
        title = "Charging Profile",
        titleBarColor = getColor(R.color.white),
    ) {

    override fun onResume() {
        super.onResume()
        mViewModel.getList()
    }

    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
    }

    override fun initClick() {
        mViewBind.apply {
            addNewChargingProfile.setOnClickListener {
                startActivity<CreateAProfileActivity>(Pair("type", 0))
            }
        }
        mViewModel.apply {
            chargingProfileRvConfig.getAdapter().addChildClickViewIds(R.id.edit)
            chargingProfileRvConfig.getAdapter().addChildClickViewIds(R.id.detele)
            chargingProfileRvConfig.getAdapter()
                .setOnItemChildClickListener { adapter, view, position ->
                    var datas = adapter.data as MutableList<ProfileItemRsp>
                    when (view.id) {
                        R.id.edit -> {
                            startActivity<CreateAProfileActivity>(
                                Pair("data", datas[position].toJson()),
                                Pair("type", 1)
                            )
                        }
                        R.id.detele->{
                            mViewModel.deleteProfile(datas[position].hydraHomeHouseholdProfilePk!!)
                        }
                    }
                }
        }

    }

    override fun loadData() {
        mViewModel.getList()
    }

    override fun initObserver() {

    }
}