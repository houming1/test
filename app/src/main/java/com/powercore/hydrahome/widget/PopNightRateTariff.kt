package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.toast
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopNightRateTariffBinding
import com.powercore.hydrahome.databinding.PopNightRateTariffBindingImpl
import com.powercore.hydrahome.databinding.PopTimePickerBinding

class PopNightRateTariff(context: Context) : BottomPopupView(context) {
    private lateinit var bind: PopNightRateTariffBinding
    override fun getImplLayoutId() = R.layout.pop_night_rate_tariff
    private var mOkBlock: (p: String, time: String) -> Unit = { p: String, time: String -> }
    var settime = ""
    var  nightRate=""
    var  nightStartTime=""
    var  nightStopTime=""
    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {
        bind.apply {
            input.setText(nightRate)
            settime="$nightStartTime - $nightStopTime"
            time.text= "$nightStartTime - $nightStopTime"
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
                    "Please enter Night rate tariff".toast()
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

    fun setCallBack(nightRate:String ,nightStartTime:String,nightStopTime:String ,okBlock: (p: String, time: String) -> Unit = { p: String, time: String -> }) {
        mOkBlock = okBlock
        this.nightRate=nightRate;
        this.nightStartTime=nightStartTime
        this.nightStopTime=nightStopTime
    }
}

