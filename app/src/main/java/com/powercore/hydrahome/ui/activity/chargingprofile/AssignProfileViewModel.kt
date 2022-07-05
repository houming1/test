package com.powercore.hydrahome.ui.activity.chargingprofile

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
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.AssignChargeBoxProfileVo
import com.powercore.hydrahome.net.entity.req.GetHomeChargeBoxVo
import com.powercore.hydrahome.net.entity.req.ProfilePkVo
import com.powercore.hydrahome.net.entity.rsp.ProfileItemRsp
import com.powercore.hydrahome.net.entity.rsp.ProfileListRsp

class AssignProfileViewModel : BaseViewModel() {
    private val profileAdapter by lazy {
        BaseDataBindingAdapter<ProfileListRsp, ItemChargingProfileBinding>(
            R.layout.item_profile,
            arrayListOf(),
            BR.data,
        )
    }
    val profileRvConfig by lazy {
        RecyclerViewConfig.Builder<ProfileListRsp, ItemChargingProfileBinding>()
            .adapter(profileAdapter)
            .hasFixedSize(false)
            .dividerWidth(10f)
            .build()
    }

    fun getHouseholdProfileList() {
        request({
            apiService.getHouseholdProfileList(
                GetHomeChargeBoxVo(
                    hydraHomeHouseholdPk = CacheUtil.getString(
                        Constant.LAST_HOME_PK
                    ) ?: ""
                ).toRequestBody()
            )
        }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
                    profileAdapter.setList(data ?: arrayListOf())
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg!!.toast()
                }
                onComplete = {

                }
            })
    }

    fun assignChargeBoxProfile(
        hydraHomeHouseholdChargeBoxPk: String,
        hydraHomeHouseholdProfilePk: String
    ) {
        request({
            apiService.assignChargeBoxProfile(
                AssignChargeBoxProfileVo(
                    hydraHomeHouseholdChargeBoxPk = hydraHomeHouseholdChargeBoxPk,
                    hydraHomeHouseholdProfilePk = hydraHomeHouseholdProfilePk
                ).toRequestBody()
            )
        }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->
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