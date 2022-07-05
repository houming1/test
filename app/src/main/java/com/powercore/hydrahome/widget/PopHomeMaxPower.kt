package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.getString
import com.fdf.base.ext.toast
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopHomeMaxPowerBinding


/**
 *    Created by Administrator on 2021/12/16.
 *    Desc :
 */
class PopHomeMaxPower(context: Context) : BottomPopupView(context) {


    private lateinit var bind: PopHomeMaxPowerBinding
    private var inputEndBlock: (String) -> Unit = {}
    private var value = ""

    override fun getImplLayoutId() = R.layout.pop_home_max_power


    fun setEndInputBlock(value: String, inputEndBlock: (String) -> Unit) {
        this.inputEndBlock = inputEndBlock
        this.value = value
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        bind.input.setText(value)
        bind.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                val power = input.text.toString().trim()
                if (power.isBlank()) {
                    "Please enter maximum home load power".toast()
                    return@setOnClickListener
                }
                if (power.toDouble() <= 0) {
                    "Maximum home load power must be greater than 0".toast()
                    return@setOnClickListener
                }
                if (power.toDouble() > 1000) {
                    " maximum power beyond the home is 1000".toast()
                    return@setOnClickListener
                }
                inputEndBlock(power)
                dismiss()
            }
        }
    }
}