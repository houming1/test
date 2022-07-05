package com.powercore.hydrahome.ui.activity.chargingprofile

import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.base.BaseViewModel
import com.fdf.base.bind.RecyclerViewConfig
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ItemChargingProfileBinding
import com.powercore.hydrahome.net.entity.rsp.ProfileListRsp
import com.powercore.hydrahome.BR
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.GetHomeChargeBoxVo
import com.powercore.hydrahome.net.entity.req.ProfilePkVo
import com.powercore.hydrahome.net.entity.rsp.ProfileItemRsp

class ChargingProfileViewModel : BaseViewModel() {
    private val chargingProfileAdapter by lazy {
        BaseDataBindingAdapter<ProfileItemRsp, ItemChargingProfileBinding>(
            R.layout.item_charging_profile,
            arrayListOf(),
            BR.data,
        )
    }
    val chargingProfileRvConfig by lazy {
        RecyclerViewConfig.Builder<ProfileItemRsp, ItemChargingProfileBinding>()
            .adapter(chargingProfileAdapter)
            .hasFixedSize(false)
            .dividerWidth(10f)
            .build()
    }

    fun getList() {
        request({
            apiService.getList(
                GetHomeChargeBoxVo(
                    hydraHomeHouseholdPk = CacheUtil.getString(
                        Constant.LAST_HOME_PK
                    ) ?: ""
                ).toRequestBody()
            )
        }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
                    chargingProfileAdapter.setList(data ?: arrayListOf())
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg!!.toast()
                }
                onComplete = {

                }
            })
    }

    fun deleteProfile(hydraHomeHouseholdProfilePk: String) {
        request({
            apiService.deleteProfile(
                ProfilePkVo(
                    hydraHomeHouseholdProfilePk = hydraHomeHouseholdProfilePk
                ).toRequestBody()
            )
        }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
                    getList()
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg!!.toast()
                }
                onComplete = {

                }
            })
    }

}