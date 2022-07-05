package com.powercore.hydrahome.ui.fragment.record

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.log
import com.fdf.base.ext.request
import com.fdf.base.utils.CacheUtil
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.TransactionStatsVo
import com.powercore.hydrahome.net.entity.rsp.StatsPersonBean
import com.powercore.hydrahome.utils.DateUtil
import com.powercore.hydrahome.utils.WeekTimeUtils

class PersonalRecordViewModel : BaseViewModel() {

    //*****************************************************************************
    //*  数据统计
    //*****************************************************************************

    val statsPersonalParams = MutableLiveData(TransactionStatsVo().apply {
        type = "person"
        cycle = "week"
        date =
            WeekTimeUtils.getMondayTime(DateUtil.getNowDay()) + "~" + WeekTimeUtils.getSundayTime(
                DateUtil.getNowDay()
            )
        hydraHomeHouseholdPk = CacheUtil.getString(Constant.LAST_HOME_PK) ?: ""
    })
    val statsPersonListLiveData = MutableLiveData<StatsPersonBean>()

    fun transactionPersonalStats() {
        request(
            { apiService.transactionStats(statsPersonalParams.value!!.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    "我成功了".log()
                    data?.let {
                        statsPersonListLiveData.value = data
                    }
                }
                onFailed = { errorCode, errorMsg ->

                }
                onComplete = {
                }
            })
    }

}