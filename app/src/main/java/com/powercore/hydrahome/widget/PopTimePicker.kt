package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.log
import com.fdf.base.ext.toast
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopTimePickerBinding
import com.powercore.hydrahome.utils.DateUtil


class PopTimePicker(context: Context) : BottomPopupView(context) {
    private lateinit var bind: PopTimePickerBinding
    private var mCurrHour: Int = DateUtil.getCurrDate("HH").toInt()
    private var mCurrMinute: Int = DateUtil.getCurrDate("mm").toInt()
    var type = 1;//1是开始时间、2是结束时间
    var startHour = ""
    var startMinute = ""
    var endHour = ""
    var endMinute = ""
    private var mOkBlock: (String) -> Unit = {}
    override fun getImplLayoutId(): Int {
        return R.layout.pop_time_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {
        startHour = if (mCurrHour <= 9) {
            "0$mCurrHour"
        } else {
            mCurrHour.toString()
        }
        startMinute = if (mCurrMinute <= 9) {
            "0$mCurrMinute"
        } else {
            mCurrMinute.toString()
        }
        bind.apply {
            startTime.helper.borderColorNormal = resources.getColor(R.color.theme_color)

            wheelHour.apply {

                refreshByNewDisplayedValues(arrayOfNulls<String>(24).apply {
                    for (i in 0..23) {
                        this[i] = if (i <= 9) "0${i}" else "${i}"
                    }
                })

                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    "year->${displayedValues[newVal]}".log()
                    if (type == 1) {
                        startHour = if (newVal <= 9) {
                            "0${newVal}"
                        } else {
                            "${newVal}"
                        }
                        if (startMinute.isNullOrBlank()) {
                            startMinute = "XX"
                        }
                        bind.startTime.setText(startHour + ":" + startMinute)
                    } else {
                        endHour = if (newVal <= 9) {
                            "0${newVal}"
                        } else {
                            "${newVal}"
                        }
                        if (endMinute.isNullOrBlank()) {
                            endMinute = "XX"
                        }
                        bind.endTime.setText(endHour + ":" + endMinute)
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
                    if (type == 1) {
                        startMinute = if (newVal <= 9) {
                            "0${newVal}"
                        } else {
                            "${newVal}"
                        }
                        if (startHour.isNullOrBlank()) {
                            startHour = "XX"
                        }
                        bind.startTime.setText(startHour + ":" + startMinute)
                    } else {
                        endMinute = if (newVal <= 9) {
                            "0${newVal}"
                        } else {
                            "${newVal}"
                        }
                        if (endHour.isNullOrBlank()) {
                            endHour = "XX"
                        }
                        bind.endTime.setText(endHour + ":" + endMinute)
                    }

                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrMinute }
            }
            startTime.setOnClickListener {
                type = 1
                startTime.helper.borderColorNormal = resources.getColor(R.color.theme_color)
                endTime.helper.borderColorNormal = resources.getColor(R.color.color_AA)

            }
            endTime.setOnClickListener {
                type = 2
                startTime.helper.borderColorNormal = resources.getColor(R.color.color_AA)
                endTime.helper.borderColorNormal = resources.getColor(R.color.theme_color)
            }
            tvOk.setOnClickListener {
                if (startHour.isNullOrBlank() || startMinute.isNullOrBlank() || endHour.isNullOrBlank() || endMinute.isNullOrBlank()) {
                    "Please select a time".toast()
                    return@setOnClickListener
                }
                var time = "$startHour:$startMinute - $endHour:$endMinute"
                if (time.contains("X")) {
                    "Please select a time".toast()
                    return@setOnClickListener
                }

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