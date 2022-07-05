package com.powercore.hydrahome.ui.activity.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doOnTextChanged
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.gyf.immersionbar.ImmersionBar
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityChargingRecordsBinding
import com.powercore.hydrahome.databinding.ActivityFeedbackBinding
import com.powercore.hydrahome.ui.activity.chargingrecords.ChargingRecordsViewModel

class FeedbackActivity : BaseDbVmActivity<ActivityFeedbackBinding, FeedbackViewModel>(
    title = "Feedback",
    titleBarColor = getColor(R.color.white),
) {
    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
        mViewBind.apply {
            firstName.doOnTextChanged { text, start, before, count ->
                checkSubmit()
            }
            surname.doOnTextChanged { text, start, before, count ->
                checkSubmit()
            }
            emailAddress.doOnTextChanged { text, start, before, count ->
                checkSubmit()
            }
            confirmChargePointModel.doOnTextChanged { text, start, before, count ->
                checkSubmit()
            }
            content.doOnTextChanged { text, start, before, count ->
                mViewBind.number.text= "${text.toString().length}/500"
                checkSubmit()
            }
        }
    }

    override fun initClick() {
        mViewBind.confrim.setOnClickListener {
            mViewModel.submitFeedback()
        }

    }

    override fun loadData() {

    }

    override fun initObserver() {

    }
    private fun checkSubmit(){
        mViewBind.confrim.isEnabled=false
        mViewBind.confrim.helper.backgroundColorNormal=resources.getColor(R.color.color_b7b9)

        mViewModel.apply {
            if (mViewBind.firstName.text.isNullOrBlank()){
                return
            }

            if (mViewBind.surname.text.isNullOrBlank()){
                return
            }

            if (mViewBind.emailAddress.text.isNullOrBlank()){
                return
            }

            if (mViewBind.confirmChargePointModel.text.isNullOrBlank()){
                return
            }

            if (mViewBind.content.text.isNullOrBlank()){
                return
            }

            mViewBind.confrim.isEnabled=true
            mViewBind.confrim.helper.backgroundColorNormal=resources.getColor(R.color.theme_color)
        }

    }
}