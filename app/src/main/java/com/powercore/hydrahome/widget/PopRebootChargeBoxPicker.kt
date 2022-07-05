package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.lxj.xpopup.core.CenterPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopRebootChargeBoxPickerBinding

class PopRebootChargeBoxPicker(context: Context) : CenterPopupView(context) {
    private lateinit var bind: PopRebootChargeBoxPickerBinding
    private var mOkBlock: (String) -> Unit = {}
    var hydraHomeHouseholdProfilePk = ""
    override fun getImplLayoutId(): Int {
        return R.layout.pop_reboot_charge_box_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {
        bind.apply {
            close.setOnClickListener {
                dismiss()
            }
            yes.setOnClickListener {
                mOkBlock.invoke(hydraHomeHouseholdProfilePk)
                dismiss()
            }
            no.setOnClickListener {
                dismiss()
            }
        }

    }

    fun setCallBack(hydraHomeHouseholdProfilePk: String, okBlock: (String) -> Unit = {}) {
        mOkBlock = okBlock
        this.hydraHomeHouseholdProfilePk = hydraHomeHouseholdProfilePk
    }
}