package com.powercore.hydrahome.ui.activity.main

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.base.BaseViewModel
import com.fdf.base.bind.RecyclerViewConfig
import com.fdf.base.ext.getString
import com.fdf.base.ext.log
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.gk.net.ext.toRequestBody
import com.gk.net.utils.toJson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.DefaultVo
import com.powercore.hydrahome.net.entity.req.GetHomeChargeBoxVo
import com.powercore.hydrahome.net.entity.req.HydraHomeHouseholdChargeBoxPkVo
import com.powercore.hydrahome.net.entity.req.RemoteStartVo
import com.powercore.hydrahome.net.entity.rsp.HomeChargeBox
import com.powercore.hydrahome.net.entity.rsp.HomeListBean


/**
 *    Created by Administrator on 2021/12/7.
 *    Desc :
 */
class MainViewModel : BaseViewModel() {

    //*****************************************************************************
    //*  家庭列表
    //*****************************************************************************
    val homeListLiveData = MutableLiveData<MutableList<HomeListBean>>()
    val showHomeSelector = MutableLiveData<Boolean>()
    fun getHomeList(isShowHomeSelector: Boolean = false) {
        request({ apiService.getHomeList(DefaultVo().toRequestBody()) }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
                    homeListLiveData.value = data
                    showHomeSelector.value = isShowHomeSelector
                }
            })
    }

    val currFamilyName = MutableLiveData("")
    val currFamilyKey = MutableLiveData("")

    //是否一次请求完成
    val isRequestEnd = MutableLiveData(false)
    val homeChargeBoxParams = MutableLiveData(GetHomeChargeBoxVo())
    val chargeBoxList = MutableLiveData<MutableList<HomeChargeBox>>()
    fun getHomeChargeBox() {
        homeChargeBoxParams.value?.let {
            it.hydraHomeHouseholdPk = CacheUtil.getString(Constant.LAST_HOME_PK) ?: ""
        }

        "获取充电桩列表 ${homeChargeBoxParams.value!!.hydraHomeHouseholdPk}".log()
        request(
            { apiService.getHomeChargeBox(homeChargeBoxParams.value!!.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    chargeBoxList.value = data
                }
                onComplete = {
                    isRequestEnd.value = true
                }
            })
    }

    /**
     * 验证蓝牙是否可以连接
     */
    fun validBle(chargeBoxId: String, connectBlock: () -> Unit, failed: () -> Unit) {
        val map = mapOf(
            "chargeBoxId" to chargeBoxId,
            "hydraHomeHouseholdPk" to CacheUtil.getString(Constant.LAST_HOME_PK)
        )
        request(
            { apiService.validBleAdd(map.toRequestBody()) },
            isShowLoad = false,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    connectBlock()
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg?.toast()
                    failed.invoke()
                }
                onError = {
                    it.printStackTrace()
                    failed.invoke()
                }
            })
    }

    //*****************************************************************************
    //*  远程启动
    //*****************************************************************************

    val connectorPkLiveData = MutableLiveData("")


    val remoteStartParams = MutableLiveData(RemoteStartVo())
    fun remoteStart() {
        remoteStartParams.value!!.apply {
            hydraHomeHouseholdChargeBoxPk = connectorPkLiveData.value!!
        }
        Log.e("hm---", remoteStartParams.value!!.toJson())
        request(
            { apiService.remoteStart(remoteStartParams.value!!.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    LiveEventBus.get<Boolean>(LiveDataBusKeys.CHARGE_REQUEST_SUCCESS).post(true)
                    //msg?.toast()
                    getHomeChargeBox()
                }
                onFailed = { errorCode, errorMsg ->
                    LiveEventBus.get<Boolean>(LiveDataBusKeys.CHARGE_REQUEST_SUCCESS).post(false)
                    errorMsg?.toast()
                }
                onComplete = {
                    connectorPkLiveData.value = ""
                }
            })
    }

    //    //*****************************************************************************
    //    //*  解绑充电桩
    //    //*****************************************************************************

    fun deleteChargeBox(hydraHomeHouseholdChargeBoxPk: String) {
        request(
            {
                apiService.deleteChargeBox(
                    HydraHomeHouseholdChargeBoxPkVo(
                        hydraHomeHouseholdChargeBoxPk = hydraHomeHouseholdChargeBoxPk
                    ).toRequestBody()
                )
            },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->

                    getHomeChargeBox()
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg!!.toast()
                }
                onComplete = {

                }
            })
    }

    //    //*****************************************************************************
    //    //*  重启充电桩
    //    //*****************************************************************************

    fun rebootChargeBox(hydraHomeHouseholdChargeBoxPk: String) {
        request(
            {
                apiService.rebootChargeBox(
                    HydraHomeHouseholdChargeBoxPkVo(
                        hydraHomeHouseholdChargeBoxPk = hydraHomeHouseholdChargeBoxPk
                    ).toRequestBody()
                )
            },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->

                    getHomeChargeBox()
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg!!.toast()
                }
                onComplete = {

                }
            })
    }

}