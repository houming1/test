package com.powercore.hydrahome.ui.activity.charge

import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.gyf.immersionbar.ImmersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityChargingBinding
import com.powercore.hydrahome.utils.CoroutineTask
import com.powercore.hydrahome.utils.DateUtil
import com.powercore.hydrahome.widget.PopAdjustPowerPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChargingActivity : BaseDbVmActivity<ActivityChargingBinding, ChargingViewModel>(
    title = "",
    titleBarColor = getColor(R.color.white)
) {
    private var mUpTimeJob: Job? = null
    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
        Glide.with(this).asGif().load(R.drawable.charge_on).into(mViewBind.ivLightning);
        lifecycleScope.launchWhenResumed {
            mViewModel.getCharging()
        }
    }

    override fun initClick() {
        mViewBind.apply {
            mViewBind.arrowRight.setOnClickListener {
                mViewModel.apply {
                    if (mPosition < chargingDatas.value!!.size - 1) {
                        mPosition++
                        chargingData.value = chargingDatas.value!!.get(mPosition)
                        mUpTimeJob?.cancel()
                        chargingData.value!!.startTime?.let { it1 -> upTime(it1) }
                    }
                }

            }

            mViewBind.arrowLeft.setOnClickListener {
                mViewModel.apply {
                    if (mPosition > 0) {
                        mPosition--
                        chargingData.value = chargingDatas.value!!.get(mPosition)
                        mUpTimeJob?.cancel()
                        chargingData.value!!.startTime?.let { it1 -> upTime(it1) }
                    }
                }
            }
        }
        mViewBind.stop.setOnClickListener {
            if (mViewModel.chargingData.value?.status == "Charging") {
                val popupView =
                    XPopup.Builder(this@ChargingActivity)
                        .asConfirm(
                            "stop", "Are you sure to finish charging?",
                            "Cancel", "Confirm",
                            {
                                mViewModel.remoteStop() {
                                    //loadData()
                                }
                            }, null, false
                        )
                popupView.cancelTextView.setTextColor(0x8193AE)
                popupView.confirmTextView.setTextColor(0x15CD80)
                popupView.show()
            }

        }
        mViewBind.clAdjustPower.setOnClickListener {
            if (mViewModel.chargingData.value!!.supportLimitUnits.isNullOrBlank()) {
                return@setOnClickListener
            }
            XPopup.Builder(this@ChargingActivity)
                .asCustom(
                    PopAdjustPowerPicker(
                        this@ChargingActivity,
                        mViewModel.chargingData.value!!.supportLimitUnits!!
                    ).apply {
                        setCallBack { value, type ->
                            var unit = if (type == 0) {
                                "Amps"
                            } else {
                                "kW"
                            }
                            mViewModel.limitPower(unit, value)
                        }
                    })
                .show()
        }
    }

    override fun loadData() {
        mViewModel.getCharging()
    }

    override fun initObserver() {
        mViewModel.apply {
            chargingData.observe(this@ChargingActivity, {
//                mViewBind.tvTime.setUpTime(it.startTime.toString())
                if (it?.startTime == null || it?.startTime!!.isBlank()) {
                    mIsEndUpTime = true
                } else {
                    mUpTimeJob?.cancel()
                    upTime(it?.startTime!!)
                }
                if (it != null) {
                    if (CoroutineTask.isStart()) return@observe
                    CoroutineTask.start(15) { mViewModel.getCharging() }
                } else {
                    CoroutineTask.cancel()
                }
            })
        }

        LiveEventBus.get<Boolean>(LiveDataBusKeys.REFRESH_CHARGING).observe(this, {
            mViewModel.getCharging()
        })
        LiveEventBus.get<Boolean>(LiveDataBusKeys.REFRESH_CHARGE_BOX).observe(this, {
            mViewModel.getCharging()
        })
        LiveEventBus.get<Long>(LiveDataBusKeys.REFRESH_CHARGE_RECORD).observe(this) {

        }
    }

    private var mIsEndUpTime = false
    private fun upTime(startTime: String) {
        mUpTimeJob = lifecycleScope.launch(Dispatchers.IO) {
            while (!mIsEndUpTime) {
                val remainingTime = getDisTime(startTime)
                val hour: Int = (remainingTime / 1000 / 3600).toInt()
                val minute: Int = (((remainingTime) - 1000 * hour * 3600) / 1000 / 60).toInt()
                val second: Int =
                    ((remainingTime - 1000 * hour * 3600 - minute * 60 * 1000) / 1000).toInt()
                launch(Dispatchers.Main) {
                    mViewBind.tvTime.text =
                        "${if (hour < 10) "0${hour}" else "$hour"}:${if (minute < 10) "0${minute}" else "$minute"}:${if (second < 10) "0${second}" else "$second"}"
                }
                delay(1000)
            }
            launch(Dispatchers.Main) {
                mViewBind.tvTime.text = "00:00:00"
            }
        }
    }

    private fun getDisTime(startTime: String): Long {
//        val endTimeStamp = startTime.utc2Local("yyyy-MM-dd HH:mm:ss")
        return System.currentTimeMillis() - DateUtil.stringToLong(startTime)
    }
}