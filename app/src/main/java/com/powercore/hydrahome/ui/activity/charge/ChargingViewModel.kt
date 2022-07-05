package com.powercore.hydrahome.ui.activity.charge

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.gk.net.ext.toRequestBody
import com.jeremyliao.liveeventbus.LiveEventBus
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.HydraHomeHouseholdChargeBoxPkVo
import com.powercore.hydrahome.net.entity.req.LimitPowerVo
import com.powercore.hydrahome.net.entity.rsp.ChargingBean

class ChargingViewModel : BaseViewModel() {
    val chargingDatas = MutableLiveData<MutableList<ChargingBean>>()
    val chargingData = MutableLiveData<ChargingBean?>()
    val isShow = MutableLiveData<Boolean>(false)
    val diffTime = MutableLiveData("00:00:00")
    var mPosition = 0;
    fun getCharging() {
        val map = mapOf("a" to "1")
        request(
            { apiService.getChargingData(map.toRequestBody()) },
            isShowLoad = false,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    if (data?.isNotEmpty()!!) {
                        chargingData.value = data[0]
                        chargingDatas.value = data
                        isShow.value = data.size > 1
                    } else {
                        chargingData.value = null
                        chargingDatas.value = data
                    }
                }
                onFailed = { errorCode, errorMsg -> }
            })
    }

    fun remoteStop(stop: (Boolean) -> Unit) {
        val map = mapOf("transactionPk" to chargingData.value!!.transactionPk)
        request(
            { apiService.remoteStop(map.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    msg?.toast()
                    LiveEventBus.get<Boolean>(LiveDataBusKeys.REFRESH_CHARGE_BOX).post(true)
                    LiveEventBus.get<Boolean>(LiveDataBusKeys.NAVIGATION_TO_RECORD).post(true)
                    chargingData.value = ChargingBean()
                    stop.invoke(true)
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg?.toast()
                    stop.invoke(false)
                }
            })
    }

    fun limitPower(unit: String, value: String) {
        request(
            {
                apiService.limitPower(
                    LimitPowerVo(
                        transactionPk = chargingData.value!!.transactionPk,
                        unit = unit,
                        value = value
                    ).toRequestBody()
                )
            },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    "success".toast()

                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg!!.toast()
                }
                onComplete = {

                }
            })
    }
}