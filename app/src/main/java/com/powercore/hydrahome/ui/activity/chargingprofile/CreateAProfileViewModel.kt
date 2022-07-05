package com.powercore.hydrahome.ui.activity.chargingprofile

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.base.BaseViewModel
import com.fdf.base.bind.RecyclerViewConfig
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.BR
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ItemChargingProfileBinding
import com.powercore.hydrahome.databinding.ItemWeekBinding
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.AddProfileVo
import com.powercore.hydrahome.net.entity.req.DefaultVo
import com.powercore.hydrahome.net.entity.req.LoginVo
import com.powercore.hydrahome.net.entity.rsp.ProfileListRsp
import com.powercore.hydrahome.net.entity.rsp.WeekRsp

class CreateAProfileViewModel : BaseViewModel() {

    val name = MutableLiveData("")
    var dayStartTime = MutableLiveData("")
    var dayEndTime = MutableLiveData("")
    var nightStartTime = MutableLiveData("")
    var nightEndTime = MutableLiveData("")
    val weekAdapter by lazy {
        BaseDataBindingAdapter<WeekRsp, ItemWeekBinding>(
            R.layout.item_week,
            arrayListOf(),
            BR.data,
        )
    }
    val weekRvConfig by lazy {
        RecyclerViewConfig.Builder<WeekRsp, ItemWeekBinding>()
            .adapter(weekAdapter)
            .hasFixedSize(false)
            .spanCount(3)
            .dividerWidth(10f)
            .build()
    }
    var addProfileLiveData = MutableLiveData(false)
    fun addProfile(hydraHomeHouseholdProfileWeeks:MutableList<Int>) {
        var addProfileVo = AddProfileVo(
            hydraHomeHouseholdPk = CacheUtil.getString(Constant.LAST_HOME_PK) ?: "",
            hydraHomeHouseholdProfileDayStart = dayStartTime.value.toString(),
            hydraHomeHouseholdProfileDayStop = dayEndTime.value.toString(),
            hydraHomeHouseholdProfileName = name.value.toString(),
            hydraHomeHouseholdProfileNightStart = nightStartTime.value.toString(),
            hydraHomeHouseholdProfileNightStop = nightEndTime.value.toString(),
            hydraHomeHouseholdProfileWeeks = hydraHomeHouseholdProfileWeeks,
            hydraHomeHouseholdProfilePk = ""
        )

        request({ apiService.addProfile(addProfileVo.toRequestBody()) }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
                    addProfileLiveData.value = true
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg!!.toast()
                }
                onComplete = {

                }
            })
    }
    fun updateProfile(hydraHomeHouseholdProfileWeeks:MutableList<Int>,hydraHomeHouseholdProfilePk:String) {
        var addProfileVo = AddProfileVo(
            hydraHomeHouseholdPk = CacheUtil.getString(Constant.LAST_HOME_PK) ?: "",
            hydraHomeHouseholdProfileDayStart = dayStartTime.value.toString(),
            hydraHomeHouseholdProfileDayStop = dayEndTime.value.toString(),
            hydraHomeHouseholdProfileName = name.value.toString(),
            hydraHomeHouseholdProfileNightStart = nightStartTime.value.toString(),
            hydraHomeHouseholdProfileNightStop = nightEndTime.value.toString(),
            hydraHomeHouseholdProfileWeeks = hydraHomeHouseholdProfileWeeks,
            hydraHomeHouseholdProfilePk = hydraHomeHouseholdProfilePk
        )

        request({ apiService.updateProfile(addProfileVo.toRequestBody()) }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
                    addProfileLiveData.value = true
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg!!.toast()
                }
                onComplete = {

                }
            })
    }
}