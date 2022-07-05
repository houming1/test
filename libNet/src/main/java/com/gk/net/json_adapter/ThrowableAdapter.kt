package com.gk.net.json_adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 *    Created by Administrator on 2021/11/26.
 *    Desc :
 */
object ThrowableAdapter {
    @FromJson
    fun fromJson(string: String) = Throwable(string)

    @ToJson
    fun toJson(value: Throwable) = value.toString()
}