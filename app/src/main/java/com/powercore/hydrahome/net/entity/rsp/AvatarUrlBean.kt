package com.powercore.hydrahome.net.entity.rsp

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvatarUrlBean(
    @Json(name = "avatarUrl")
    val avatarUrl: String
)