package com.powercore.hydrahome.widget

import android.content.Context
import android.view.View
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.toast
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopAdjustPowerPickerBinding

class PopAdjustPowerPicker(context: Context, var supportLimitUnits: String) :
    BottomPopupView(context) {
    private lateinit var bind: PopAdjustPowerPickerBinding
    var type = 0//默认选中 Amps 1是选中kw
    override fun getImplLayoutId() = R.layout.pop_adjust_power_picker
    private var mOkBlock: (value: String, type: Int) -> Unit = { value: String, type: Int -> }
    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {
        if (!supportLimitUnits.isNullOrBlank()) {
            var limits = supportLimitUnits.split(",")
            if (limits.size == 1) {
                if (limits[0].equals("Amps")) {
                    bind.kwLl.visibility = View.GONE
                    bind.AmpsSel.setImageResource(R.drawable.icon_sel)
                    bind.AmpsSel.isEnabled = false
                    type = 0
                } else {
                    bind.AmpsLl.visibility = View.GONE
                    bind.kwSel.setImageResource(R.drawable.icon_sel)
                    bind.kwSel.isEnabled = false
                    type = 1
                }
            } else {
                bind.AmpsSel.setImageResource(R.drawable.icon_sel)
                type = 0
            }
        }
        bind.AmpsSel.setOnClickListener {

            bind.AmpsSel.setImageResource(R.drawable.icon_sel)
            bind.kwSel.setImageResource(R.drawable.icon_unsel)
            type = 0

        }
        bind.kwSel.setOnClickListener {
            bind.kwSel.setImageResource(R.drawable.icon_sel)
            bind.AmpsSel.setImageResource(R.drawable.icon_unsel)
            type = 1
        }
        bind.btnCancel.setOnClickListener {
            dismiss()
        }
        bind.btnConfirm.setOnClickListener {
            var value = bind.input.text.toString()
            if (value.isNullOrBlank()) {
                "Please enter parameters".toast()
                return@setOnClickListener
            }
            mOkBlock.invoke(value, type)

        }
    }

    fun setCallBack(okBlock: (value: String, type: Int) -> Unit = { value: String, type: Int -> }) {
        mOkBlock = okBlock
    }
}