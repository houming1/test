package com.powercore.hydrahome.ui.activity.family.setting

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.base.BaseViewModel
import com.fdf.base.bind.RecyclerViewConfig
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.R
import com.powercore.hydrahome.BR
import com.powercore.hydrahome.databinding.ItemFamilySetHomeListBinding
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.DefaultVo
import com.powercore.hydrahome.net.entity.rsp.HomeListBean

class FamilySetViewModel : BaseViewModel() {
    //*****************************************************************************
    //*  家庭列表
    //*****************************************************************************
    val homeListLiveData = MutableLiveData<MutableList<HomeListBean>>(arrayListOf())
    val requestHomeListEnd= MutableLiveData<Boolean>()
    fun getHomeList() {
        requestHomeListEnd.value=false
        request({ apiService.getHomeList(DefaultVo().toRequestBody()) }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
                    homeListLiveData.value = data
                    data?.let { homeListRvConfig.setList(it) }

                }
                onFailed={errorCode, errorMsg ->

                }
                onComplete={
                    requestHomeListEnd.value=true
                }
            })
    }

    val homeListRvConfig= RecyclerViewConfig.Builder<HomeListBean, ItemFamilySetHomeListBinding>()
        .adapter(BaseDataBindingAdapter(R.layout.item_family_set_home_list,homeListLiveData.value!!,BR.data))
        .build()


    //*****************************************************************************
    //*  删除家庭组
    //*****************************************************************************
    val delHomeResult = MutableLiveData<Int>()
    fun delHome(hydraHomeHouseholdPk:String,postion:Int) {
        val map = mapOf("hydraHomeHouseholdPk" to hydraHomeHouseholdPk)
        request(
            { apiService.delHome(map.toRequestBody()) },
            isShowLoad = true,
            loadStr = "deleting...",
            listenerBuilder = {
                onSuccess = { data, msg ->
                    delHomeResult.value = postion
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg?.toast()
                }
            })
    }
}