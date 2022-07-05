package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.toast
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopDayRateTariffBinding
import com.powercore.hydrahome.databinding.PopNightRateTariffBinding

class PopDayRateTariff(context: Context) : BottomPopupView(context) {
    private lateinit var bind: PopDayRateTariffBinding
    override fun getImplLayoutId() = R.layout.pop_day_rate_tariff
    private var mOkBlock: (p: String, time: String) -> Unit = { p: String, time: String -> }
    var settime = ""
    var  dayRate=""
    var  dayStartTime=""
    var  dayStopTime=""
    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {
        bind.apply {
            input.setText(dayRate)
            settime="$dayStartTime - $dayStopTime"
            time.text= "$dayStartTime - $dayStopTime"
        }

        bind.apply {
            time.setOnClickListener {
                XPopup.Builder(context).asCustom(
                    PopTimePicker(context).apply {
                        setCallBack(okBlock = {
                            settime = it
                            time.text = it
                        })
                    }).show()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                if (input.text.toString().isNullOrBlank()) {
                    "Please enter Day rate tariff".toast()
                    return@setOnClickListener
                }
                if (settime.isNullOrBlank()||settime.equals(" - ")) {
                    "Please select a time".toast()
                    return@setOnClickListener
                }
                mOkBlock.invoke(input.text.toString(), settime)
                dismiss()

            }
        }
    }

    fun setCallBack(dayRate:String ,dayStartTime:String,dayStopTime:String ,okBlock: (p: String, time: String) -> Unit = { p: String, time: String -> }) {
        mOkBlock = okBlock
        this.dayRate=dayRate;
        this.dayStartTime=dayStartTime
        this.dayStopTime=dayStopTime
    }
}