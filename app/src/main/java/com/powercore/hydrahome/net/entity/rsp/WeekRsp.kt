package com.powercore.hydrahome.net.entity.rsp

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class WeekRsp(
    @Json(name = "weekNmae")
    var weekNmae: String? = null,
    @Json(name = "isSel")
    var isSel: Boolean? = false
)