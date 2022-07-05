package com.powercore.hydrahome.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.lifecycle.lifecycleScope
import com.fdf.base.app.appContext
import com.fdf.base.ext.getColor
import com.fdf.base.ext.getString
import com.fdf.base.ext.log
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopChargeTypePickerBinding
import com.powercore.hydrahome.ext.getDate
import com.powercore.hydrahome.ext.nowDate
import com.powercore.hydrahome.ext.nowTime
import com.powercore.hydrahome.net.entity.rsp.HomeChargeBox
import com.powercore.hydrahome.ui.activity.main.MainViewModel
import com.powercore.hydrahome.utils.DateUtil.stringToLongHMS
import com.powercore.hydrahome.utils.NumberInputFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PopChargeTypePicker constructor(context: Context) : BottomPopupView(context) {

    private var mCurrState: CurrSelState = CurrSelState.NOTHING

    private var mainViewModel: MainViewModel? = null

    private lateinit var bind: PopChargeTypePickerBinding

    private var mData: HomeChargeBox? = null

    override fun getImplLayoutId() = R.layout.pop_charge_type_picker
    var type = 0;

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        changeState()
        setClickListener()
        initView()
        bind.vm = mainViewModel
        mainViewModel!!.connectorPkLiveData.value = mData!!.hydraHomeHouseholdChargeBoxPk
        bind.apply {
            tvChargePoint.text = mData?.chargeBoxId
            tvConnectorInfo.text = "${mData?.connectorType} | ${mData?.power}kW  ｜ Socket only"

            llEmpty.apply {
                isFocusable = true
                isFocusableInTouchMode = true
                requestFocus()
            }
        }
    }

    private fun initView() {
        bind.apply {
            totalEnergyEt.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
              if (charSequence!!.isNotEmpty()){
                  tvNext.isEnabled = true
                  tvNext.helper.backgroundColorNormal = getColor(R.color.theme_color)
              }else{
                  tvNext.isEnabled = false
                  tvNext.helper.backgroundColorNormal = getColor(R.color.common_grey)
              }
            }
            costEt.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
                if (charSequence!!.isNotEmpty()){
                    tvNext.isEnabled = true
                    tvNext.helper.backgroundColorNormal = getColor(R.color.theme_color)
                }else{
                    tvNext.isEnabled = false
                    tvNext.helper.backgroundColorNormal = getColor(R.color.common_grey)
                }
            }
        }
    }

    private fun hideSoftInput(editText: EditText) {
        editText.clearFocus()
        val imm: InputMethodManager? =
            appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun setViewModel(viewModel: MainViewModel) {
        this.mainViewModel = viewModel

    }

    fun setData(data: HomeChargeBox) {
        this.mData = data
    }

    override fun beforeShow() {
        super.beforeShow()
        hideSoftInput(bind.inputMaxPower)
    }

    override fun beforeDismiss() {
        super.beforeDismiss()
        hideSoftInput(bind.inputMaxPower)
    }

    private fun setClickListener() {
        bind.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            clChargeNow.setOnClickListener {
                mCurrState = CurrSelState.NOW
                changeState()
            }
            clSetTime.setOnClickListener {
                mCurrState = CurrSelState.TIMING
                changeState()
            }
            tvNext.setOnClickListener {

                when (mCurrState) {
                    CurrSelState.NOW -> {
                        mainViewModel?.remoteStart()
                        dismiss()
                    }
                    CurrSelState.TIMING -> {
                        tvNext.text = getString(R.string.txt_start_charging)
                        val confirmEnable = false
                        tvNext.helper.backgroundColorNormal =
                            getColor(if (confirmEnable) R.color.theme_color else R.color.common_grey)
                        tvNext.helper.setEnabled(confirmEnable)
                        tvNext.isEnabled = confirmEnable

                        if (groupStep.visibility == View.INVISIBLE) {
                            dismiss()
                        }
                        groupStep.visibility = View.INVISIBLE
                        groupStep2.visibility = View.VISIBLE
                        mCurrState = CurrSelState.TIMECONFIRM
                    }
                    CurrSelState.TIMECONFIRM -> {
                        //确定充电
                        if (type==3){
                            mainViewModel!!.remoteStartParams.value!!.value=bind.totalEnergyEt.text.toString()
                        }else if (type==4){
                            mainViewModel!!.remoteStartParams.value!!.value=bind.costEt.text.toString()
                        }
                        mainViewModel?.remoteStart()
                        dismiss()
                    }
                    else -> {

                    }
                }
            }

            chargeByTime.setOnClickListener {
                type = 1
                showType(type)
                mainViewModel!!.remoteStartParams.value!!.type = "time"
            }
            timeSchedule.setOnClickListener {
                type = 2
                showType(type)
                mainViewModel!!.remoteStartParams.value!!.type = "schedule"
            }
            totalEnergy.setOnClickListener {
                type = 3
                showType(type)
                mainViewModel!!.remoteStartParams.value!!.type = "energy"
            }
            cost.setOnClickListener {
                type = 4
                showType(type)
                mainViewModel!!.remoteStartParams.value!!.type = "cost"
            }
            chargeByTimeTv.setOnClickListener {
                //选择时间长度
                XPopup.Builder(context).asCustom(
                    PopChargeTimePicker(context).apply {
                        setCallBack(okBlock = {
                            mainViewModel!!.remoteStartParams.value!!.value =
                                (stringToLongHMS(it) / 1000).toString()
                            chargeByTimeTv.text = it
                            checkConfrim()
                        })
                    }).show()
            }
            timeScheduleTv.setOnClickListener {
                //选择时间区间
                showDateTimerPickerView(PopDateTimePicker.DateTimeType.START)
            }
        }
    }

    fun showType(type: Int) {
        bind.apply {
            if (type == 1) {
                chargeByTime.setImageResource(R.mipmap.icon_sel)
                chargeByTimeName.setTextColor(context.resources.getColor(R.color.color_33))
                chargeByTimeTv.visibility = VISIBLE
            } else {
                chargeByTime.setImageResource(R.mipmap.icon_unsel)
                chargeByTimeName.setTextColor(context.resources.getColor(R.color.color_8f9392))
                chargeByTimeTv.visibility = GONE
            }
            if (type == 2) {
                timeSchedule.setImageResource(R.mipmap.icon_sel)
                timeScheduleName.setTextColor(context.resources.getColor(R.color.color_33))
                timeScheduleTv.visibility = VISIBLE
            } else {
                timeSchedule.setImageResource(R.mipmap.icon_unsel)
                timeScheduleName.setTextColor(context.resources.getColor(R.color.color_8f9392))
                timeScheduleTv.visibility = GONE
            }
            if (type == 3) {
                totalEnergy.setImageResource(R.mipmap.icon_sel)
                totalEnergyName.setTextColor(context.resources.getColor(R.color.color_33))
                totalEnergyRl.visibility = VISIBLE
            } else {
                totalEnergy.setImageResource(R.mipmap.icon_unsel)
                totalEnergyName.setTextColor(context.resources.getColor(R.color.color_8f9392))
                totalEnergyRl.visibility = GONE
            }
            if (type == 4) {
                cost.setImageResource(R.mipmap.icon_sel)
                costName.setTextColor(context.resources.getColor(R.color.color_33))
                costRl.visibility = VISIBLE
            } else {
                cost.setImageResource(R.mipmap.icon_unsel)
                costName.setTextColor(context.resources.getColor(R.color.color_8f9392))
                costRl.visibility = GONE
            }
        }

        mainViewModel!!.remoteStartParams.value!!.value = ""
        bind.apply {
            chargeByTimeTv.text = ""
            timeScheduleTv.text = ""
            totalEnergyEt.setText("")
            costEt.setText("")
            tvNext.isEnabled = false
            tvNext.helper.backgroundColorNormal = getColor(R.color.common_grey)
        }

    }
    /* *
      * TODO 切换状态
      **/

    private fun changeState() {
        when (mCurrState) {
            CurrSelState.NOTHING -> {
                bind.apply {
                    clChargeNow.helper.backgroundColorNormal = getColor(R.color.white)
                    tvChargeNow.setTextColor(getColor(R.color.common_grey))
                    ivChargeNow.setImageResource(R.mipmap.ic_charge_now)
                    clSetTime.helper.backgroundColorNormal = getColor(R.color.white)
                    tvChargeSetTime.setTextColor(getColor(R.color.common_grey))
                    ivChargeSetTime.setImageResource(R.mipmap.ic_charge_set_time)
                    tvNext.helper.setEnabled(false)
                    tvNext.isEnabled = false
                    tvNext.helper.backgroundColorNormal = getColor(R.color.common_grey)
                    tvNext.text = getString(R.string.txt_next)
                }
            }
            CurrSelState.NOW -> {
                bind.apply {
                    clChargeNow.helper.backgroundColorNormal = getColor(R.color.color_F9D5D5)
                    tvChargeNow.setTextColor(getColor(R.color.white))
                    ivChargeNow.setImageResource(R.mipmap.ic_charge_now)

                    clSetTime.helper.backgroundColorNormal = getColor(R.color.white)
                    tvChargeSetTime.setTextColor(getColor(R.color.common_grey))
                    ivChargeSetTime.setImageResource(R.mipmap.ic_charge_set_time)

                    mainViewModel!!.remoteStartParams.value!!.startMode = "now"

                    tvNext.helper.setEnabled(true)
                    tvNext.isEnabled = true
                    tvNext.helper.backgroundColorNormal = getColor(R.color.theme_color)
                    tvNext.text = getString(R.string.txt_start_charging)

                }
            }
            CurrSelState.TIMING -> {
                bind.apply {
                    clChargeNow.helper.backgroundColorNormal = getColor(R.color.white)
                    tvChargeNow.setTextColor(getColor(R.color.common_grey))
                    ivChargeNow.setImageResource(R.mipmap.ic_charge_now)
                    clSetTime.helper.backgroundColorNormal = getColor(R.color.color_F9D5D5)
                    tvChargeSetTime.setTextColor(getColor(R.color.white))
                    ivChargeSetTime.setImageResource(R.mipmap.ic_charge_set_time)
                    tvNext.helper.setEnabled(true)
                    tvNext.isEnabled = true
                    tvNext.helper.backgroundColorNormal = getColor(R.color.theme_color)
                    tvNext.text = getString(R.string.txt_next)
                    mainViewModel!!.remoteStartParams.value!!.startMode = "param"
                }
            }
        }
    }

    /* *
      * 显示日期时间选择弹窗*/

    private fun showDateTimerPickerView(type: PopDateTimePicker.DateTimeType) {
        val contentView = PopDateTimePicker(context).apply {
            setCallBack(
                type,
                System.currentTimeMillis().nowDate(),
                System.currentTimeMillis().nowTime("HH:mm"),
                confrimBlock = { startTime, endTime ->
                    mainViewModel!!.remoteStartParams.value!!.value = "$startTime~$endTime"
                    bind.timeScheduleTv.text = startTime + " - " + endTime
                    checkConfrim()
                },
            )
        }
        XPopup.Builder(context)
            .asCustom(contentView)
            .show()
    }

    enum class CurrSelState {
        NOTHING, //没选中任何选项
        NOW,//立即充电
        TIMING,//定时充电
        TIMECONFIRM//选择充电后确定充电
    }

    fun checkConfrim() {
        bind.apply {
            if (!mainViewModel!!.remoteStartParams.value!!.value.isNullOrBlank()) {
                tvNext.isEnabled = true
                tvNext.helper.backgroundColorNormal = getColor(R.color.theme_color)
            } else {
                tvNext.isEnabled = false
                tvNext.helper.backgroundColorNormal = getColor(R.color.common_grey)
            }

        }

    }
}