package com.gk.net.ext

import com.gk.net.utils.toJson
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 *    Created by Administrator on 2021/10/14.
 *    Desc :
 */

/**
 * TODO Any 转 RequestBody
 * 对象不能含子类
 *
 * @return
 */
fun Any.toRequestBody(): RequestBody {
    return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), toJson())
}