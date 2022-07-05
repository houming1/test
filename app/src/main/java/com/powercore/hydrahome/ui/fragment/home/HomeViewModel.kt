package com.powercore.hydrahome.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.log
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.DefaultVo
import com.powercore.hydrahome.net.entity.req.GetHomeChargeBoxVo
import com.powercore.hydrahome.net.entity.rsp.HomeChargeBox
import com.powercore.hydrahome.net.entity.rsp.HomeListBean

class HomeViewModel : BaseViewModel() {


//*  家庭列表

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

                }
                onComplete = {
                    isRequestEnd.value = true
                }
            })
    }

}