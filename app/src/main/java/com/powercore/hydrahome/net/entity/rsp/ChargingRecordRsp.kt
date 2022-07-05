package com.powercore.hydrahome.net.entity.rsp

import androidx.annotation.Keep
import com.powercore.hydrahome.utils.DateUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChargingRecordRsp(
    @Json(name = "cost")
    var cost: String? = null,
    @Json(name = "chargingTime")
    var chargingTime: Int? = 0,
    @Json(name = "sessions")
    var sessions: String? = null,
    @Json(name = "energyUsed")
    var energyUsed: String? = null,
    @Json(name = "transactionList")
    val transactionList: MutableList<TransactionListBean> = mutableListOf()
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class ChartsData(var week: String = "", var value: String = "", var count: Int = 0)

    fun costStr(): String {
        return "£$cost"
    }

    fun chargingTimeStr(): String {
        return DateUtil.formatTime(chargingTime!!)!!
    }

    fun energyUsedStr(): String {
        return (energyUsed!!.toFloat() / 1000.0f).toString() + "kWh"
    }


}
