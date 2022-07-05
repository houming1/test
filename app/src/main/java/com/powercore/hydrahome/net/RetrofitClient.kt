package com.powercore.hydrahome.net

import com.fdf.base.ext.log
import com.powercore.hydrahome.net.interceptor.LoggerInterceptor
import com.powercore.hydrahome.net.interceptor.TokenInterceptor
import com.gk.net.base.BaseRetrofitClient
import okhttp3.OkHttpClient

/**
 *    Created by Administrator on 2021/11/25.
 *    Desc :
 */

val apiService by lazy { RetrofitClient.getService(ApiService::class.java, ApiService.BASE_URL) }

object RetrofitClient : BaseRetrofitClient() {


    override fun handleBuilder(builder: OkHttpClient.Builder) {
        "handleBuilder".log()
        builder.apply {
            addInterceptor(TokenInterceptor())
            addNetworkInterceptor(LoggerInterceptor())
        }
    }
}