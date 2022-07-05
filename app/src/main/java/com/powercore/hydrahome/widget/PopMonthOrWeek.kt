package com.powercore.hydrahome.widget

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.gone
import com.fdf.base.ext.visible
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.WeekMonthBinding
import android.text.TextPaint
import com.lxj.xpopup.core.AttachPopupView


class PopMonthOrWeek(context: Context) : AttachPopupView(context) {

    enum class QueryType {
        MONTH, WEEK
    }

    private var currType = QueryType.WEEK

    private var mCallBack: (QueryType) -> Unit = {}

    private var bind: WeekMonthBinding? = null

    override fun getImplLayoutId(): Int {
        return R.layout.week_month
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!

        setCheckStatus()

        bind?.rlMonth?.setOnClickListener {
            currType = QueryType.MONTH
            setCheckStatus()

        }
        bind?.rlWeek?.setOnClickListener {
            currType = QueryType.WEEK
            setCheckStatus()

        }
        bind!!.cancel.setOnClickListener {
            dismiss()
        }
        bind!!.confrim.setOnClickListener {
            mCallBack.invoke(currType)
            dismiss()
        }

    }


    fun setCallBack(type: QueryType, result: (QueryType) -> Unit = {}) {
        currType = type
        mCallBack = result
    }

    private fun setCheckStatus() {
        when (currType) {
            QueryType.MONTH -> {
                bind!!.rlMonth.helper.backgroundColorNormal =
                    ContextCompat.getColor(context, R.color.theme_color20)
                bind!!.rlWeek.helper.backgroundColorNormal =
                    ContextCompat.getColor(context, R.color.white)
                bind?.tvMonth?.setTextColor(ContextCompat.getColor(context, R.color.theme_color))
                bind?.tvWeek?.setTextColor(ContextCompat.getColor(context, R.color.color_33))
                val paint: TextPaint = bind!!.tvMonth.getPaint()
                paint.isFakeBoldText = true
                val paint1: TextPaint = bind!!.tvWeek.getPaint()
                paint1.isFakeBoldText = false
                bind?.ivCheckMonth?.visible()
                bind?.ivCheckYear?.gone()
            }
            QueryType.WEEK -> {
                val paint: TextPaint = bind!!.tvMonth.paint
                paint.isFakeBoldText = false
                val paint1: TextPaint = bind!!.tvWeek.paint
                paint1.isFakeBoldText = true
                bind!!.rlMonth.helper.backgroundColorNormal =
                    ContextCompat.getColor(context, R.color.white)
                bind!!.rlWeek.helper.backgroundColorNormal =
                    ContextCompat.getColor(context, R.color.theme_color20)
                bind?.tvMonth?.setTextColor(ContextCompat.getColor(context, R.color.color_33))
                bind?.tvWeek?.setTextColor(ContextCompat.getColor(context, R.color.theme_color))
                bind?.ivCheckMonth?.gone()
                bind?.ivCheckYear?.visible()
            }
        }
    }
}