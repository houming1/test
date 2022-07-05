package com.powercore.hydrahome.net

import com.gk.net.entity.ApiResponse
import com.squareup.moshi.JsonClass

/**
 *    Created by Administrator on 2021/11/29.
 *    Desc :
 */
@JsonClass(generateAdapter = true)
data class BaseResponse<T>(var code: Int?, var data: T?, var message: String?, var success: Boolean) : ApiResponse<T>() {

    override fun getResponseCode() = code ?: -1

    override fun getResponseMsg() = message

    override fun getResponseData() = data

    override fun isSuccess() = success
}