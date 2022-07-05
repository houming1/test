package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.CenterPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopAssignChargingProfilePickerBinding
import com.powercore.hydrahome.databinding.PopBaseContentPickerBinding

class PopBaseContentPicker(context: Context, var title: String,var content: String) :
    CenterPopupView(context) {
    private lateinit var bind: PopBaseContentPickerBinding
    private var mOkBlock: (String) -> Unit = {}
    override fun getImplLayoutId(): Int {
        return R.layout.pop_base_content_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }
    private fun initView() {
        bind.title.text = title
        bind.content.text = content
        bind.apply {
            close.setOnClickListener {
                dismiss()
            }
            yes.setOnClickListener {
                mOkBlock.invoke("")
                dismiss()
            }
            no.setOnClickListener {
                dismiss()
            }
        }

    }

    fun setCallBack( okBlock: (String) -> Unit = {}) {
        mOkBlock = okBlock

    }
}