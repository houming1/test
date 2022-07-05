package com.gk.net.entity

/**
 *    Created by Administrator on 2021/11/16.
 *    Desc :
 */
import com.gk.net.ext.showToast
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import java.util.concurrent.CancellationException
import javax.net.ssl.SSLHandshakeException

fun handlingApiExceptions(code: Int, errorMsg: String) {
    "code:$code,msg:$errorMsg".showToast()
}

const val UNAUTHORIZED = 401
const val FORBIDDEN = 403
const val NOT_FOUND = 404
const val REQUEST_TIMEOUT = 408
const val INTERNAL_SERVER_ERROR = 500
const val BAD_GATEWAY = 502
const val SERVICE_UNAVAILABLE = 503
const val GATEWAY_TIMEOUT = 504
fun handlingExceptions(e: Throwable) = when (e) {

    is HttpException -> {
        e.message().showToast()
    }
    is ConnectException, is SocketTimeoutException, is UnknownHostException -> {
        "connect failed".showToast()
    }
    is CancellationException -> {
    }
    is SocketTimeoutException -> {
    }
    is JSONException, is ParseException -> {

    }
    is SSLHandshakeException -> {

    }
    else -> {
    }
}


