package com.powercore.hydrahome.ui.fragment.record

import android.graphics.Color
import com.fdf.base.base.BaseDbVmFragment
import com.fdf.base.ext.log
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.gk.net.utils.MoshiUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.databinding.FragmentRecordHouseholdBinding
import com.powercore.hydrahome.ext.nowDate
import com.powercore.hydrahome.ext.nowTime
import com.powercore.hydrahome.net.entity.rsp.StatsPersonBean
import com.powercore.hydrahome.utils.DateUtil
import com.powercore.hydrahome.utils.WeekTimeUtils
import com.powercore.hydrahome.widget.PopMonthOrWeek
import com.powercore.hydrahome.widget.PopMonthPicker
import com.powercore.hydrahome.widget.PopWeekDateTimePicker
import java.util.ArrayList

class HouseholdRecordFragment: BaseDbVmFragment<FragmentRecordHouseholdBinding, HouseholdRecordViewModel>()  {
    private var mQueryType = PopMonthOrWeek.QueryType.WEEK
    override fun init() {
        mViewBind.vm=mViewModel
        var monday = WeekTimeUtils.getMondayTime(DateUtil.getNowDay())
        var sunday = WeekTimeUtils.getSundayTime(DateUtil.getNowDay())
        mViewBind.tvMonthWeek.text = "$monday - $sunday"
    }

    override fun initClick() {
        mViewBind.apply {
            rclMonthPicker.setOnClickListener {
                XPopup.Builder(requireContext())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .atView(mViewBind.rclMonthPicker)
                    .popupAnimation(PopupAnimation.NoAnimation)
                    .asCustom(PopMonthOrWeek(requireContext()).apply {
                        setCallBack(mQueryType, result = {
                            mQueryType = it
                            when (mQueryType) {
                                PopMonthOrWeek.QueryType.WEEK -> {
                                    tvMonthOrWeek.text = "Week"
                                    tvMonthWeek.text =
                                        WeekTimeUtils.getMondayTime(DateUtil.getNowDay()) + " - " + WeekTimeUtils.getSundayTime(
                                            DateUtil.getNowDay()
                                        )
                                    mViewModel.statsPersonalParams.value!!.cycle = "week"
                                    mViewModel.statsPersonalParams.value!!.date =
                                        WeekTimeUtils.getMondayTime(DateUtil.getNowDay()) + "~" + WeekTimeUtils.getSundayTime(
                                            DateUtil.getNowDay()
                                        )
                                    mViewModel.transactionPersonalStats()

                                }
                                PopMonthOrWeek.QueryType.MONTH -> {
                                    tvMonthOrWeek.text = "Month"
                                    tvMonthWeek.text = DateUtil.getNowMonth()
                                    mViewModel.statsPersonalParams.value!!.cycle = "month"
                                    mViewModel.statsPersonalParams.value!!.date =
                                        DateUtil.getNowMonth()
                                    mViewModel.transactionPersonalStats()
                                }

                            }
                        })


                    })
                    .show()
            }
            tvMonthWeek.setOnClickListener {
                when (mQueryType) {
                    PopMonthOrWeek.QueryType.WEEK -> {
                        showDateTimerPickerView()
                    }
                    PopMonthOrWeek.QueryType.MONTH -> {
                        XPopup.Builder(requireContext())
                            .asCustom(PopMonthPicker(requireContext()).apply {
                                setCallBack(
                                    DateUtil.getCurrDate("yyyy"),
                                    DateUtil.getCurrDate("MM"),
                                    okBlock = {
                                        "选中的年月->${it}".log()
                                        mViewBind.tvMonthWeek.text = it
                                        mViewModel.statsPersonalParams.value!!.date = it
                                        mViewModel.transactionPersonalStats()
                                    })
                            })
                            .show()
                    }
                }
            }
        }

    }

    override fun loadData() {
        mViewModel.transactionPersonalStats()
    }

    override fun initObserver() {
        LiveEventBus.get<String>(LiveDataBusKeys.HOME_STATS_CHECKED).observeSticky(this, {
            "默认的homepk=${it}".log()
            if (it.isNotBlank()) {
                val checkedPks = MoshiUtils.listFromJson<String>(it)
                mViewModel.statsPersonalParams.value!!.hydraHomeHouseholdPk=checkedPks[0]
                mViewModel.transactionPersonalStats()
            }
        })
        mViewModel.statsPersonListLiveData.observe(this, {
            setBarChartData(it.chartsData)
            if (it.chartsData.isNotEmpty()) {
                mViewBind.barChart.highlightValue(
                    (it.chartsData.size - 1).toFloat(),
                    it.chartsData.size - 1
                )
            }
        })
    }

    /* *
* 显示日期时间选择弹窗*/

    private fun showDateTimerPickerView() {
        val contentView = PopWeekDateTimePicker(requireActivity()).apply {
            setCallBack(
                PopWeekDateTimePicker.DateTimeType.START,
                System.currentTimeMillis().nowDate(),
                System.currentTimeMillis().nowTime("HH:mm"),
                startBlock = {
                    mViewBind.tvMonthWeek.text = it
                    var dates = it.split(" - ")
                    mViewModel.statsPersonalParams.value!!.date = dates[0] + "~" + dates[1]
                    mViewModel.transactionPersonalStats()
                },
            )
        }
        XPopup.Builder(context)
            .asCustom(contentView)
            .show()
    }

    private fun setBarChartData(xyList: MutableList<StatsPersonBean.ChartsData>) {

        val xValues = xyList.map { i -> i.week } as ArrayList<String>
        val yValues: ArrayList<BarEntry> = arrayListOf()
        xyList.forEachIndexed { index, xy ->
            var v = xy.value.toFloat() / 1000.0f
            yValues.add(BarEntry(index.toFloat(), v))
        }

        mViewBind.barChart.apply {
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e != null) {

                    }
                }

                override fun onNothingSelected() {

                }
            })
            setDrawBarShadow(true)

            description.isEnabled = false;//隐藏右下角英文
            axisRight.isEnabled = false//隐藏右侧Y轴   默认是左右两侧都有Y轴
            isDoubleTapToZoomEnabled = false
            setPinchZoom(true)//y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放
            setScaleEnabled(false)

            //x軸
            xAxis.apply {
                textColor = Color.parseColor("#C01818")
                labelCount = xValues.size
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM;//X轴的位置 默认为上面
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (value.toInt() < xValues.size) {
                            val fullLabel = xValues[value.toInt()]
                            when (mQueryType) {
                                PopMonthOrWeek.QueryType.WEEK -> {
                                    getWeek2English(fullLabel.split("-".toRegex()).last().toInt())
                                }
                                PopMonthOrWeek.QueryType.MONTH -> {
                                    getMonth2English(fullLabel.split("-".toRegex()).last().toInt())
                                }
                            }

                        } else
                            ""
                    }
                }
                setDrawGridLines(false)
            }
            axisLeft.apply {
                textColor = Color.parseColor("#C6C6C6")
                axisMinimum = 0f
                setDrawZeroLine(false)
                enableGridDashedLine(10f, 5f, 5f)
            }
            axisRight.apply {

            }
            //图例
            legend.apply {
                isEnabled = false
            }
            data = BarData(BarDataSet(yValues, "").apply {
                color = Color.parseColor("#C01818")
                valueTextColor = Color.parseColor("#C01818")
                valueTextSize = 10f

            }).apply {
                barWidth = xValues.size * .45f / 8
            }

//            zoom(xValues.size / 7f, 1f, 0f, 0f)
            invalidate()
            animateY(500)
        }
    }


    fun getMonth2English(num: Int): String {
        return when (num) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> "--"
        }

    }

    fun getWeek2English(num: Int): String {
        return when (num) {
            1 -> "Mon"
            2 -> "Tue"
            3 -> "Wed"
            4 -> "Thur"
            5 -> "Fri"
            6 -> "Sat"
            7 -> "Sun"
            else -> "--"
        }

    }
}