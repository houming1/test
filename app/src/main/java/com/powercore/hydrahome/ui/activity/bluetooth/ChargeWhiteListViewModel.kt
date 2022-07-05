package com.powercore.hydrahome.ui.activity.bluetooth

import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.net.apiService

class ChargeWhiteListViewModel :BaseViewModel() {

    /**
     * 验证蓝牙是否可以连接
     */
    fun validBle(chargeBoxId: String, connectBlock: () -> Unit, failed: () -> Unit) {
        val map = mapOf("chargeBoxId" to chargeBoxId, "hydraHomeHouseholdPk" to CacheUtil.getString(Constant.LAST_HOME_PK))
        request({ apiService.validBleAdd(map.toRequestBody()) }, isShowLoad = false, listenerBuilder = {
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
}