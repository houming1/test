package com.powercore.hydrahome.net.entity.rsp

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 *    Created by Administrator on 2021/12/16.
 *    Desc :
 */
@JsonClass(generateAdapter = true)
data class HomeChargeBox(
    @Json(name = "chargeBoxId")
    var chargeBoxId: String = "",

    @Json(name = "chargeBoxPk")
    var chargeBoxPk: String = "",

    @Json(name = "connectorStatus")
    var connectorStatus: String = "",

    @Json(name = "connectorType")
    var connectorType: String = "",

    @Json(name = "hydraHomeHouseholdChargeBoxPk")
    var hydraHomeHouseholdChargeBoxPk: String = "",

    @Json(name = "power")
    var power: Double = 0.0,

    @Json(name = "profileName")
    var profileName: String = "",
    )











