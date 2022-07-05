package com.powercore.hydrahome.ui.fragment.record

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.DateVo
import com.powercore.hydrahome.net.entity.req.DefaultVo
import com.powercore.hydrahome.net.entity.rsp.HomeListBean

class RecordViewModel : BaseViewModel() {

    //*****************************************************************************
    //*  家庭列表
    //*****************************************************************************
    val homeListLiveData = MutableLiveData<MutableList<HomeListBean>>()
    fun getHomeList() {
        request({ apiService.getHomeList(DefaultVo().toRequestBody()) }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
                    homeListLiveData.value = data
                }
            })
    }


    fun downloadHydraHomeTransaction(startTime:String, endTime:String) {

        request({ apiService.downloadHydraHomeTransaction(DateVo(startTime=startTime,stopTime = endTime).toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
                    "success".toast()
                }
            })
    }

}