package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.log
import com.fdf.base.ext.toast
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopProfileTimePickerBinding
import com.powercore.hydrahome.utils.DateUtil

class PopProfileTimePicker(context: Context) : BottomPopupView(context) {
    private lateinit var bind: PopProfileTimePickerBinding
    private var mCurrHour: Int = DateUtil.getCurrDate("HH").toInt()
    private var mCurrMinute: Int = DateUtil.getCurrDate("mm").toInt()
    var mHour = ""
    var mMinute = ""
    private var mOkBlock: (String) -> Unit = {}
    override fun getImplLayoutId(): Int {
        return R.layout.pop_profile_time_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {

        bind.apply {

            wheelHour.apply {

                refreshByNewDisplayedValues(arrayOfNulls<String>(24).apply {
                    for (i in 0..23) {
                        this[i] = if (i <= 9) "0${i}" else "${i}"
                    }
                })

                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    "year->${displayedValues[newVal]}".log()
                    mHour = if (newVal <= 9) {
                        "0${newVal}"
                    } else {
                        "${newVal}"
                    }

                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrHour }
            }

            wheelMinute.apply {
                refreshByNewDisplayedValues(arrayOfNulls<String>(60).apply {
                    for (i in 0..59) {
                        this[i] = if (i <= 9) "0${i}" else "${i}"
                    }
                })
                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    "month->${displayedValues[newVal]}".log()
                    mMinute = if (newVal <= 9) {
                        "0${newVal}"
                    } else {
                        "${newVal}"
                    }

                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrMinute }
            }


            tvOk.setOnClickListener {
                if (mHour.isNullOrBlank() || mMinute.isNullOrBlank()) {
                    "Please select a time".toast()
                    return@setOnClickListener
                }

                var time = "$mHour:$mMinute"
                mOkBlock.invoke(time)
                dismiss()

            }
            tvCancel.setOnClickListener { dismiss() }
        }
    }

    fun setCallBack(okBlock: (String) -> Unit = {}) {
        mOkBlock = okBlock


    }
}