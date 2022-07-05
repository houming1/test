package com.powercore.hydrahome.ui.activity.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.fdf.base.getString
import com.gyf.immersionbar.ImmersionBar
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivitySettingBinding

class SettingActivity : BaseDbVmActivity<ActivitySettingBinding, SettingViewModel>(
    title = "Settings",
    titleBarColor = getColor(R.color.white)
) {


    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
    }

    override fun initClick() {
        mViewBind.apply {
            changePassword.setOnClickListener {

            }
            companyWebsite.setOnClickListener {

            }
            appGuide.setOnClickListener {

            }
            privacyAgreement.setOnClickListener {

            }

        }

    }

    override fun loadData() {

    }

    override fun initObserver() {
    }
}