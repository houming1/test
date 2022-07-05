package com.powercore.hydrahome.net.entity.rsp

import com.powercore.hydrahome.ext.dateFormat
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class ChargingBean(
    @Json(name = "adjustPowerUnit")
    var adjustPowerUnit: String? = "",
    @Json(name = "adjustPowerValue")
    var adjustPowerValue: String? = "",
    @Json(name = "chargeBoxId")
    var chargeBoxId: String? = "",
    @Json(name = "cost")
    var cost: Double? = 0.0,
    @Json(name = "current")
    var current: String? = "",
    @Json(name = "energyUsed")
    var energyUsed: String? = "",
    @Json(name = "power")
    var power: String? = "",
    @Json(name = "startTime")
    var startTime: String? = "",
    @Json(name = "status")
    var status: String? = "",
    @Json(name = "supportLimitUnits")
    var supportLimitUnits: String? = "",
    @Json(name = "transactionPk")
    var transactionPk: Long? = 0,
    @Json(name = "voltage")
    var voltage: String? = "",


    ) {
    fun getNameStr():String{
       return "Zodiac - $chargeBoxId"
    }
    fun getEnergyUsedStr(): String {
        var value = ""
        if (energyUsed.isNullOrBlank()) {
            value = "0 kWh"
        } else {
            value = energyUsed + " kWh"
        }
        return value
    }

    fun getVoltageStr(): String {
        var value = ""
        if (voltage.isNullOrBlank()) {
            value = "0 V"
        } else {
            value = voltage + " V"
        }
        return value
    }
    fun getCurrentStr(): String {
        var value = ""
        if (current.isNullOrBlank()) {
            value = "0 A"
        } else {
            value = current + " A"
        }
        return value
    }
    fun getCostStr(): String {

            var value ="£"+ cost.toString()

        return value
    }

    fun getPowerStr(): String {
        var value = ""
        if (power.isNullOrBlank()) {
            value = "0 kW"
        } else {
            value = power + " kW"
        }
        return value
    }



    fun getAdjustPowerValueStr(): String {
        var value = ""
        if (adjustPowerValue.isNullOrBlank()) {
            value = "0Amps"
        } else {
            value = adjustPowerValue+"Amps"
        }
        return value
    }
}