package com.powercore.hydrahome.net.entity.rsp

import androidx.annotation.Keep
import com.powercore.hydrahome.utils.DateUtil
import com.squareup.moshi.JsonClass


/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2021/12/31
 *  desc   :
 *
 */
@Keep
@JsonClass(generateAdapter = true)
data class StatsPersonBean(
    var chartsData: MutableList<ChartsData> = mutableListOf(),
    var cost: String? = "0",
    var chargingTime: Int? = 0,
    var sessions: String? = "0",
    var energyUsed: String? = "0",
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class ChartsData(var week: String = "", var value: String = "", var count: Int = 0)
    fun costStr():String{
        return "£$cost"
    }
    fun chargingTimeStr():String{
        return DateUtil.formatTime(chargingTime!!)!!
    }
    fun energyUsedStr():String{
        return (energyUsed!!.toFloat()/1000.0f).toString()+"kWh"
    }
}