package com.powercore.hydrahome.utils

import android.graphics.Color
import com.fdf.base.ext.log
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


object BarChartUtils {


    //设置X轴
    private fun initX(
        barChart: BarChart,
        showGridBackground: Boolean = false,
        touchEnable: Boolean = true,
        dragEnable: Boolean = true,
        scaleEnable: Boolean = true,
        descriptionText: String = "",
        bottomOffset: Float = 10f,
        drawableValueAboveBar: Boolean = true,
        drawBarShadow: Boolean = false
    ) {
        barChart?.apply {
            setDrawGridBackground(showGridBackground) // 是否显示表格颜色
            setTouchEnabled(touchEnable) // 设置是否可以触摸
            isDragEnabled = dragEnable // 是否可以拖拽
            setScaleEnabled(scaleEnable) // 是否可以缩放
            description.text = descriptionText //设置不显示右下角的描述
            setDrawBorders(false) //设置无边框
            extraBottomOffset = bottomOffset //偏移 为了使x轴的文字显示完全
            setDrawValueAboveBar(drawableValueAboveBar) // 如果设置为true,在条形图上方显示值。如果为false，会显示在顶部下方。
            setDrawBarShadow(drawBarShadow)//设置阴影

            //设置比例图
            legend.apply {
                isEnabled = true //设置是否显示比例图
                form = Legend.LegendForm.CIRCLE //图示 标签的形状。  圆
                //显示位置
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            setPinchZoom(true) //设置按比例放缩柱状图
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    "BarChart---${e?.x}".log()
                }

                override fun onNothingSelected() {}
            })
            //获得x轴对象实例
            xAxis.apply {
                setDrawAxisLine(true) //设置显示x轴的线
                setDrawGridLines(true) //设置是否显示网格
                granularity = 1f //设置最小的区间，避免标签的迅速增多
                setCenterAxisLabels(true) //设置标签居中
                position = XAxis.XAxisPosition.BOTTOM //数据位于底部
                textColor = Color.RED //设置x轴文字颜色
                setAxisMinimum(-0.5f);//设置离左边位置0.5个柱状图的宽度,否则最左侧的柱子会显示半个
            }
        }
    }

    //设置Y轴
    private fun initY(
        barChart: BarChart,
        drawAxisLine: Boolean = true,
        txtSize: Float = 16f,
        labelCount: Int = 5,
        labelForce: Boolean = false,
        minimum: Float = 0f
    ) {
        barChart?.apply {
            axisLeft.apply {
                setDrawAxisLine(drawAxisLine) //显示左侧y轴的线
                textSize = txtSize //显示左侧y轴字体大小
                setLabelCount(labelCount, labelForce) //设置左侧y轴显示文字数量
                //保证Y轴从0开始，不然会上移一点
                axisMinimum = minimum

                //设置左侧Y轴上文字的样式
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return super.getFormattedValue(value)
                    }
                }
            }
            axisRight.apply {
                axisMinimum = 0f
                isEnabled = false //设置y轴关闭
            }
        }
    }

    /**
     * 显示柱状图 单个柱子
     * @param yValues 柱子上的数据
     * @param lable
     * @param color
     * @param xValues x轴上显示的数据
     */
    fun showData(barChart: BarChart, xValues: ArrayList<String>, yValues: List<BarEntry>, label: String?, color: Int) {
        initX(barChart)
        initY(barChart)
        //装载显示数据
        val barDataSet = BarDataSet(yValues, label).also {
            it.color = color
            it.valueTextSize = 14f //设置柱子上字体大小
            it.setDrawValues(true) //设置是否显示柱子上的文字
            it.highLightAlpha = 37 //设置点击后柱子透明度改变
            //设置柱子上文字的格式
            it.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return super.getFormattedValue(value)
                }
            }
        }

        val customX = CustomX(xValues)
        barChart.xAxis!!.valueFormatter = customX
        //装载数据
        barChart?.also {
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(barDataSet)
            val data = BarData(dataSets)
            it.data = data
        }

    }

    internal class CustomX(private val list: ArrayList<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            val v = value.toInt()
            return if (v < list.size) {
                list[v]
            } else {
                ""
            }
        }
    }
}