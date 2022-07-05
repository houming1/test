package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.toast
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopRandomisedDelayBinding

class PopRandomisedDelay (context: Context) : BottomPopupView(context) {
    private lateinit var bind: PopRandomisedDelayBinding
    override fun getImplLayoutId() = R.layout.pop_randomised_delay
    private var mOkBlock: (String) -> Unit = {}
    private var value = ""
    fun setCallBack(value: String, mOkBlock: (String) -> Unit) {
        this.mOkBlock = mOkBlock
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
                val time = input.text.toString().trim()
                if (time.isBlank()) {
                    "Please enter Randomised delay".toast()
                    return@setOnClickListener
                }

                mOkBlock.invoke(time)
                dismiss()
            }
        }
    }
    }
