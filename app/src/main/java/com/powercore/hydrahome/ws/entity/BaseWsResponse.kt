package com.powercore.hydrahome.ws.entity

import com.squareup.moshi.JsonClass


/**
 *    Created by Administrator on 2021/12/2.
 *    Desc : WebSocket 服务端消息基类
 */
@JsonClass(generateAdapter = true)
data class BaseWsResponse<T>(
    var code: Int = -1,
    var `data`: T? = null,
    var message: String = "",
    var success: Boolean = false
)
@JsonClass(generateAdapter = true)
data class WsData (
    var api: String? = "",
    var message: String? = "" ,
    var responseKey: String? = "",
    var connectorId: String? = "",
    var chargeboxId: String? = "",
    var percent: String? = ""
)