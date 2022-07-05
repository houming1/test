package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.log
import com.fdf.base.ext.toast
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopChargeTimePickerBinding

class PopChargeTimePicker constructor(context: Context) : BottomPopupView(context) {
    private lateinit var bind: PopChargeTimePickerBinding
    override fun getImplLayoutId() = R.layout.pop_charge_time_picker

    private var mOkBlock: (String) -> Unit = {}
    var hour = "02"
    var minute = "00"
    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {

        bind.apply {
            wheelHour.apply {
                refreshByNewDisplayedValues(arrayOfNulls<String>(100).apply {
                    for (i in 0..99) {
                        this[i] = if (i <= 9) "0${i}" else "$i"
                    }
                })

                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    hour = if (newVal <= 9) {
                        "0${newVal}"
                    } else {
                        "${newVal}"
                    }

                }

                value = displayedValues.indexOfFirst { it.toInt() == 2 }
            }

            wheelMinute.apply {
                refreshByNewDisplayedValues(arrayOfNulls<String>(60).apply {
                    for (i in 0..59) {
                        this[i] = if (i <= 9) "0${i}" else "$i"
                    }
                })
                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    minute = if (newVal <= 9) {
                        "0${newVal}"
                    } else {
                        "${newVal}"
                    }
                }

                value = displayedValues.indexOfFirst { it.toInt() == 0 }
            }

            tvOk.setOnClickListener {
                if (hour.isNullOrBlank() || minute.isNullOrBlank()) {
                    "Please select a time".toast()
                    return@setOnClickListener
                }
                mOkBlock.invoke("$hour:$minute")
                dismiss()
            }
            tvCancel.setOnClickListener { dismiss() }
        }
    }

    fun setCallBack(okBlock: (String) -> Unit = {}) {
        mOkBlock = okBlock

    }
}