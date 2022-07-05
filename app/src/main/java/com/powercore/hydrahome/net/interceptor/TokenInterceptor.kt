package com.powercore.hydrahome.net.interceptor

import android.content.Intent
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.net.RetrofitClient
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.ui.activity.login.LoginActivity
import com.fdf.base.app.appContext
import com.fdf.base.ext.activityList
import com.fdf.base.ext.removeActivity
import com.fdf.base.utils.CacheUtil
import com.gk.net.utils.fromJson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.powercore.hydrahome.BuildConfig
import com.powercore.hydrahome.net.BaseResponse
import com.powercore.hydrahome.net.interceptor.logging.util.ZipHelper
import okhttp3.*
import okio.Buffer
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*

/**
 *    Created by Administrator on 2021/12/6.
 *    Desc :
 */
class TokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("app_id", "HydraHome")
        builder.addHeader("app_version", BuildConfig.VERSION_NAME)
        builder.addHeader("app_device", "Android")
        builder.addHeader("token", CacheUtil.getString(Constant.TOKEN) ?: "").build()


        val response = chain.proceed(builder.build())
        if (isTokenExpired(response)) {
            //需要刷新token
            val newToken = getNewToken()
            CacheUtil.save(Constant.TOKEN, newToken)
            builder.addHeader("token", CacheUtil.getString(Constant.TOKEN) ?: "").build()
            return chain.proceed(builder.build())
        } else if (isReLogin(response)) {
            CacheUtil.removeKey(Constant.TOKEN)
            //需要重新登录
            LiveEventBus.get<Boolean>(LiveDataBusKeys.RELOGIN).post(true)
            activityList.forEach {
                removeActivity(it)
            }
            appContext.startActivity(Intent(appContext, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
        return response
    }

    /**
     * 510表示需要刷新token
     * @param response
     * @return
     */
    private fun isTokenExpired(response: Response): Boolean {
        try {
            val jsonStr = printResult(response) ?: ""
            if (jsonStr.isBlank()) return false
            val rsp: BaseResponse<Any>? = jsonStr.fromJson()
            if (rsp != null) {
                return rsp.code == 510
            }
        } catch (e: Exception) {
            return false
        }
        return response.code() == 510
    }

    /**
     * 401表示token过期
     * @param response
     * @return
     */
    private fun isReLogin(response: Response): Boolean {
        try {
            val jsonStr = printResult(response) ?: ""
            if (jsonStr.isBlank()) return false
            val rsp: BaseResponse<Any>? = jsonStr.fromJson()
            if (rsp != null) {
                return rsp.code == 401||rsp.code==511
            }
        } catch (e: Exception) {
            return false
        }
        return response.code() == 401
    }

    private fun printResult(response: Response): String? {
        return try {
            //读取服务器返回的结果
            val responseBody = response.newBuilder().build().body()
            val source = responseBody!!.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer()

            //获取content的压缩类型
            val encoding = response
                .headers()["Content-Encoding"]
            val clone = buffer.clone()

            //解析response content
            parseContent(responseBody, encoding, clone)
        } catch (e: IOException) {
            e.printStackTrace()
            "{\"error\": \"" + e.message + "\"}"
        }
    }

    /**
     * 解析服务器响应的内容
     *
     * @param responseBody [ResponseBody]
     * @param encoding 编码类型
     * @param clone 克隆后的服务器响应内容
     * @ return 解析后的响应结果
     */
    private fun parseContent(
        responseBody: ResponseBody?,
        encoding: String?,
        clone: Buffer
    ): String? {
        var charset = Charset.forName("UTF-8")
        val contentType = responseBody!!.contentType()
        if (contentType != null) {
            charset = contentType.charset(charset)
        }
        //content 使用 gzip 压缩
        return when {
            "gzip".equals(encoding, ignoreCase = true) -> {
                //解压
                ZipHelper.decompressForGzip(
                    clone.readByteArray(),
                    convertCharset(charset)
                )
            }
            "zlib".equals(encoding, ignoreCase = true) -> {
                //content 使用 zlib 压缩
                ZipHelper.decompressToStringForZlib(
                    clone.readByteArray(),
                    convertCharset(charset)
                )
            }
            else -> {
                //content 没有被压缩, 或者使用其他未知压缩方式
                clone.readString(charset)
            }
        }
    }

    private fun isJson(mediaType: MediaType?): Boolean {
        return if (mediaType?.subtype() == null) {
            false
        } else mediaType.subtype().toLowerCase(Locale.getDefault()).contains("json")
    }

    private fun convertCharset(charset: Charset?): String {
        val s = charset.toString()
        val i = s.indexOf("[")
        return if (i == -1) {
            s
        } else s.substring(i + 1, s.length - 1)
    }

    /**
     * 同步请求方式，获取最新的Token
     *
     * @return
     */
    @Throws(IOException::class)
    private fun getNewToken(): String {
        RetrofitClient.builder.addNetworkInterceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.addHeader("refresh_token", CacheUtil.getString(Constant.REFRESH_TOKEN) ?: "").build()
            chain.proceed(builder.build())
        }
        val tokenJson: retrofit2.Response<JSONObject> = apiService.refreshToken().execute()
        return tokenJson.body()?.get("token").toString()
    }
}