package com.powercore.hydrahome.net.entity.rsp

import androidx.annotation.Keep
import com.powercore.hydrahome.utils.DateUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class TransactionListBean(
    @Json(name = "startTime")
    var startTime: String = "",
    @Json(name = "chargingTime")
    var chargingTime: Int = 0,
    @Json(name = "householdName")
    var householdName: String = "",
    @Json(name = "cost")
    var cost: String = "",
    @Json(name = "name")
    var name: String = "",
    @Json(name = "energyUsed")
    var energyUsed: String = "",
) {
    fun costStr(): String {
        return "£$cost"
    }

    fun chargingTimeStr(): String {
        return DateUtil.formatTime(chargingTime!!)!!
    }

    fun energyUsedStr(): String {
        return (energyUsed!!.toFloat() / 1000.0f).toString() + "kWh"
    }
    fun startTimeStr():String{
        return DateUtil.formatDate(startTime)
    }
}