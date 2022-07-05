package com.gk.net.base

import com.gk.net.utils.MoshiUtils
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 *    Created by Administrator on 2021/11/16.
 *    Desc :
 */

abstract class BaseRetrofitClient {
    companion object CLIENT {
        private const val TIME_OUT = 5L
        lateinit var instance: BaseRetrofitClient
    }

    val builder: OkHttpClient.Builder by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
    }

    private val client: OkHttpClient by lazy {
        handleBuilder(builder)
        builder.build()
    }

    abstract fun handleBuilder(builder: OkHttpClient.Builder)

    open fun <Service> getService(serviceClass: Class<Service>, baseUrl: String): Service {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(MoshiUtils.moshiBuild))
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .build()
            .create(serviceClass)
    }
}