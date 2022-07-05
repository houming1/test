package com.powercore.hydrahome.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fdf.base.ext.log
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ItemVpDatePickerBinding
import com.powercore.hydrahome.databinding.ItemVpTimePickerBinding
import com.powercore.hydrahome.databinding.PopWeekDateTimePickerBinding
import com.powercore.hydrahome.ext.getUTCByLocal
import com.powercore.hydrahome.ext.nowDate
import com.powercore.hydrahome.ext.nowTime
import com.powercore.hydrahome.utils.WeekTimeUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class PopWeekDateTimePicker constructor(context: Context) : BottomPopupView(context) {

    private var currType: DateTimeType = DateTimeType.START
    private var currDate: String = ""//当前日期
    private var currHour: String = ""//当前时
    private var currMinute: String = ""//当前分
    private var startHour: Int = 0
    private var startMinute: Int = 0
    private var currTime: String = ""

    private var isStartZero = false

    private lateinit var commonNavigator: CommonNavigator
    private val mTitleDataList = arrayListOf("", "")

    private var mStartBlock: (String) -> Unit = { }


    private var mViewList = arrayListOf<View>()

    private lateinit var bind: PopWeekDateTimePickerBinding

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            bind.magicIndicator.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            bind.magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            bind.magicIndicator.onPageSelected(position)
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_week_date_time_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!

        bind.time.text = WeekTimeUtils.getMondayTime(currDate) + " - " + WeekTimeUtils.getSundayTime(currDate)
        initIndicator()
        initViewPager()

        bind.confitm.setOnClickListener {
            if (currType == DateTimeType.START) {
                mStartBlock.invoke(bind.time.text.toString())
            } else if (currType == DateTimeType.END) {

            }
            dismiss()
        }
    }

    private fun initIndicator() {
        mTitleDataList[0] = currDate
        // mTitleDataList[1] = currTime

        currHour = currTime.split(":".toRegex())[0]
        currMinute = currTime.split(":".toRegex())[1]
        startHour = currHour.toInt()
        startMinute = currMinute.toInt()

        commonNavigator = CommonNavigator(context)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mTitleDataList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.normalColor = Color.parseColor("#868989")
                colorTransitionPagerTitleView.selectedColor = Color.parseColor("#222215")
                colorTransitionPagerTitleView.text = mTitleDataList[index]
                colorTransitionPagerTitleView.typeface = Typeface.DEFAULT_BOLD
                colorTransitionPagerTitleView.setOnClickListener {
                    bind.vpDateTime.currentItem = index
                }
                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                return indicator
            }
        }
        commonNavigator.adapter.notifyDataSetChanged()
        bind.magicIndicator.navigator = commonNavigator
    }

    private fun initViewPager() {
        //日期选择
        val dateView = inflate(context, R.layout.item_vp_date_picker, null)
        val dateViewBind: ItemVpDatePickerBinding = DataBindingUtil.bind(dateView)!!
        mViewList.add(dateViewBind.root)

        //时间选择
        val timeView = inflate(context, R.layout.item_vp_time_picker, null)
        val timeViewBind: ItemVpTimePickerBinding = DataBindingUtil.bind(timeView)!!
        // mViewList.add(timeViewBind.root)

        //此处是指定日期，不需要可不设置
        val dateArray = currDate.split("-".toRegex())
        //指定日期区间
        // dateViewBind.calendarView.setRange(dateArray[0].toInt(), dateArray[1].toInt(), dateArray[2].toInt(), dateArray[0].toInt() + 1, dateArray[1].toInt(), dateArray[2].toInt())
        //滑动到指定日期
        dateViewBind.calendarView.scrollToCalendar(
            dateArray[0].toInt(),
            dateArray[1].toInt(),
            dateArray[2].toInt()
        )

        dateViewBind.calendarView.setOnCalendarSelectListener(object :
            CalendarView.OnCalendarSelectListener {

            override fun onCalendarOutOfRange(calendar: Calendar?) {

            }

            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                currDate = "${calendar.year}-${
                    String.format(
                        "%02d",
                        calendar.month
                    )
                }-${String.format("%02d", calendar.day)}"
                val currSysDate = System.currentTimeMillis().nowDate()
                isStartZero = currSysDate != currDate
                //判断是否从0开始
                if (isStartZero) {
                    startHour = 0
                    startMinute = 0

                } else {
                    startHour = System.currentTimeMillis().nowTime("HH").toInt()
                    startMinute = System.currentTimeMillis().nowTime("mm").toInt()
                }
                refreshHour(timeViewBind.wheelHour)
                refreshMinute(timeViewBind.wheelMinute)
                if (isStartZero) {
                    val hour = System.currentTimeMillis().nowTime("HH").toInt()
                    val minute = System.currentTimeMillis().nowTime("mm").toInt()

                    timeViewBind.wheelHour.value = hour
                    timeViewBind.wheelMinute.value = minute

                    //  mTitleDataList[1] = "${String.format("%02d", hour)}:${String.format("%02d", minute)}"
                    // currTime = mTitleDataList[1]
                    commonNavigator.adapter.notifyDataSetChanged()
                } else {
                    timeViewBind.wheelHour.value = 0
                    timeViewBind.wheelMinute.value = 0
                }
                mTitleDataList[0] = currDate

                bind.time.text = WeekTimeUtils.getMondayTime(currDate) + " - " + WeekTimeUtils.getSundayTime(currDate)
                commonNavigator.adapter.notifyDataSetChanged()
            }
        })

        dateViewBind.calendarView.setOnCalendarInterceptListener(object :
            CalendarView.OnCalendarInterceptListener {
            override fun onCalendarIntercept(calendar: Calendar?): Boolean {
                val dateArray = currDate.split("-".toRegex())
                val currCalendar = Calendar().apply {
                    year = dateArray[0].toInt()
                    month = dateArray[1].toInt()
                    day = dateArray[2].toInt()
                }
                "日期大小 ${calendar?.compareTo(currCalendar)}".log()
                return calendar?.compareTo(currCalendar) != -1
            }

            override fun onCalendarInterceptClick(calendar: Calendar?, isClick: Boolean) {

            }
        })

        refreshHour(timeViewBind.wheelHour)
        refreshMinute(timeViewBind.wheelMinute)

        timeViewBind.wheelMinute.setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
            currMinute = String.format("%02d", newVal + startMinute)

            val hour = currHour.toInt() //+ startHour
            val minute = currMinute.toInt()// + startMinute
            // mTitleDataList[1] = "${String.format("%02d", hour)}:${String.format("%02d", minute)}"
            //currTime = mTitleDataList[1]
            commonNavigator.adapter.notifyDataSetChanged()
        }

        timeViewBind.wheelHour.setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
            currHour = String.format("%02d", newVal + startHour)

            val hour = currHour.toInt()// + startHour
            val minute = currMinute.toInt() //+ startMinute
            // mTitleDataList[1] = "${String.format("%02d", hour)}:${String.format("%02d", minute)}"
            // currTime = mTitleDataList[1]
            commonNavigator.adapter.notifyDataSetChanged()

            if (currHour.toInt() + startHour == startHour) {
                startMinute =
                    System.currentTimeMillis().nowTime("HH:mm").split(":".toRegex())[1].toInt()
                refreshMinute(timeViewBind.wheelMinute)
            } else {
                startMinute = 0
                refreshMinute(timeViewBind.wheelMinute)
            }
        }


        bind.vpDateTime.apply {
            adapter = object : BaseQuickAdapter<View, BaseViewHolder>(
                R.layout.item_pop_date_time_picker_view,
                mViewList
            ) {
                override fun convert(holder: BaseViewHolder, item: View) {
                    holder.getView<RelativeLayout>(R.id.container).addView(item)
                    item.layoutParams = RelativeLayout.LayoutParams(-1, -1)
                }
            }
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            isUserInputEnabled = false

            registerOnPageChangeCallback(onPageChangeCallback)
        }
    }

    /**
     * 更新时
     */
    private fun refreshHour(wheelHour: PickerView) {
        wheelHour.refreshByNewDisplayedValues(arrayOfNulls<String>(24 - startHour).apply {
            for (i in 0..23 - startHour) {
                val hour = startHour + i
                this[i] = String.format("%02d", hour)
            }
        })
    }

    /**
     * 更新分
     */
    private fun refreshMinute(wheelMinute: PickerView) {
        wheelMinute.refreshByNewDisplayedValues(arrayOfNulls<String>(60 - startMinute).apply {
            for (i in 0..59 - startMinute) {
                val minute = startMinute + i
                this[i] = String.format("%02d", minute)
            }
        })
    }


    override fun onDismiss() {
        super.onDismiss()
        bind.vpDateTime.unregisterOnPageChangeCallback(onPageChangeCallback)
    }


    fun setCallBack(
        type: DateTimeType,
        currDate: String,
        currTime: String,
        startBlock: (String) -> Unit
    ) {
        this.currType = type
        this.currDate = currDate
        this.currTime = currTime
        this.mStartBlock = startBlock

    }

    enum class DateTimeType {
        START, END
    }
}