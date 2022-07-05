package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.log
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopMonthPickerBinding
import com.powercore.hydrahome.utils.DateUtil


class PopMonthPicker(context: Context) : BottomPopupView(context) {

    private lateinit var bind: PopMonthPickerBinding
    private var mCurrYear: Int = DateUtil.getCurrDate("yyyy").toInt()
    private var mCurrMonth: Int = DateUtil.getCurrDate("MM").toInt()
    private var mOkBlock: (String) -> Unit = {}
    private var mFinalYearMonth: Array<String?> = arrayOfNulls(2)

    override fun getImplLayoutId(): Int {
        return R.layout.pop_month_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {

        bind.apply {
            wheelYear.apply {
                val max = mCurrYear + 100
                val min = 2020
                refreshByNewDisplayedValues(arrayOfNulls<String>(max - min + 1).apply {
                    for (i in 0..max - min) {
                        this[i] = "${2020 + i}"
                    }
                })

                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    "year->${displayedValues[newVal]}".log()
                    mFinalYearMonth[0] = displayedValues[newVal]
                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrYear }
            }

            wheelMonth.apply {
                refreshByNewDisplayedValues(arrayOfNulls<String>(12).apply {
                    for (i in 0..11) {
                        this[i] = if (i < 9) "0${i + 1}" else "${i + 1}"
                    }
                })
                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    "month->${displayedValues[newVal]}".log()
                    mFinalYearMonth[1] = displayedValues[newVal]
                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrMonth }
            }

            tvOk.setOnClickListener {
                mOkBlock.invoke("${mFinalYearMonth[0]}-${mFinalYearMonth[1]}")
                dismiss()
            }
            tvCancel.setOnClickListener { dismiss() }
        }
    }

    fun setCallBack(currYear: String, currMonth: String, okBlock: (String) -> Unit = {}) {
        mCurrYear = currYear.toInt()
        mCurrMonth = currMonth.toInt()
        mOkBlock = okBlock

        mFinalYearMonth[0] = currYear
        mFinalYearMonth[1] = currMonth
    }
}