package com.fdf.base.ext

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fdf.base.base.BaseViewModel
import com.fdf.base.exception.GlobalCoroutineExceptionHandler
import com.gk.net.BuildConfig
import com.gk.net.entity.*
import kotlinx.coroutines.*

/**
 *    Created by Administrator on 2021/11/16.
 *    Desc :
 */
fun <T> BaseViewModel.request(
    block: suspend () -> ApiResponse<T>,
    isShowLoad: Boolean = false,
    loadStr: String = "Loading...",
    listenerBuilder: ResultBuilder<T>.() -> Unit
): Job {
    return viewModelScope.launch {
        runCatching {
            if (isShowLoad)
                showDialog.value = loadStr
            block()
        }.onSuccess { data: ApiResponse<T> ->
            dismissDialog.value = true
            val response = handleHttpOk(data)
            parseResultAndCallback(response, listenerBuilder)
        }.onFailure { e ->
            e.printStackTrace()
            val throwable = handleHttpError<T>(e)
            parseResultAndCallback(throwable, listenerBuilder)
        }
    }
}

fun <T> BaseViewModel.request(
    block: suspend () -> ApiResponse<T>,
    liveData: MutableLiveData<ResultState<T>>,
    isShowLoad: Boolean = false,
    loadStr: String = "Loading...",
): Job {
    return viewModelScope.launch {
        runCatching {
            if (isShowLoad)
                showDialog.value = loadStr
            block()
        }.onSuccess { data: ApiResponse<T> ->
            dismissDialog.value = true
            when (handleHttpOk(data)) {
                is ApiSuccessResponse -> liveData.value = ResultState.onSuccess(data.getResponseData())
                is ApiFailedResponse -> liveData.value = ResultState.onFailed(data.getResponseCode(), data.getResponseMsg() ?: "")
            }
        }.onFailure { e ->
            e.printStackTrace()
            liveData.value = ResultState.onAppError(e)
        }
    }
}

/**
 * 非后台返回错误，捕获到的异常
 */
private fun <T> handleHttpError(e: Throwable): ApiResponse<T> {
    if (BuildConfig.DEBUG)
        e.printStackTrace()
    handlingExceptions(e)
    return ApiErrorResponse(e)
}

/**
 * 返回200，但是还要判断isSuccess
 */
private fun <T> handleHttpOk(data: ApiResponse<T>): ApiResponse<T> {
    return if (data.isSuccess()) {
        ApiSuccessResponse(data)
    } else {
        handlingApiExceptions(data.getResponseCode(), data.getResponseMsg() ?: "unknow error.")
        ApiFailedResponse(data.getResponseCode(), data.getResponseMsg())
    }
}

private fun <T> parseResultAndCallback(response: ApiResponse<T>, listenerBuilder: ResultBuilder<T>.() -> Unit) {
    val listener = ResultBuilder<T>().also(listenerBuilder)
    when (response) {
        is ApiSuccessResponse -> listener.onSuccess(response.getResponseData(), response.getResponseMsg())
        is ApiFailedResponse -> listener.onFailed(response.getResponseCode(), response.getResponseMsg())
        is ApiErrorResponse -> listener.onError(response.throwable)
    }
    listener.onComplete()
}


class ResultBuilder<T> {
    var onSuccess: (data: T?, msg: String?) -> Unit = { _, _ -> }
    var onFailed: (errorCode: Int, errorMsg: String?) -> Unit = { _, _ -> }
    var onError: (throwable: Throwable) -> Unit = {}
    var onComplete: () -> Unit = {}
}

sealed class ResultState<out T> {
    companion object {
        fun <T> onSuccess(data: T?): ResultState<T> = Success(data)
        fun <T> onFailed(code: Int, msg: String): ResultState<T> = Failed(code, msg)
        fun <T> onAppError(error: Throwable): ResultState<T> = Error(error)
    }

    data class Failed(val code: Int, val msg: String?) : ResultState<Nothing>()
    data class Success<out T>(val data: T?) : ResultState<T>()
    data class Error(val error: Throwable) : ResultState<Nothing>()
}


inline fun BaseViewModel.requestMain(
    errCode: Int = -1, errMsg: String = "", report: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
        block.invoke(this)
    }
}

inline fun BaseViewModel.requestIO(
    errCode: Int = -1, errMsg: String = "", report: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(Dispatchers.IO + GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
        block.invoke(this)
    }
}

inline fun BaseViewModel.delayMain(
    errCode: Int = -1, errMsg: String = "", report: Boolean = false, delayTime: Long,
    noinline block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
        withContext(Dispatchers.IO) {
            delay(delayTime)
        }
        block.invoke(this)
    }
}
