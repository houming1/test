package com.gk.net.observer

/**
 *    Created by Administrator on 2021/11/16.
 *    Desc :
 */
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gk.net.entity.*
import com.gk.net.ext.showToast

typealias StateLiveData<T> = LiveData<ApiResponse<T>>
typealias StateMutableLiveData<T> = MutableLiveData<ApiResponse<T>>

@MainThread
inline fun <T> StateMutableLiveData<T>.observeState(
    owner: LifecycleOwner,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) {
    val listener = ResultBuilder<T>().also(listenerBuilder)
    observe(owner) { apiResponse ->
        when (apiResponse) {
            is ApiSuccessResponse -> listener.onSuccess(apiResponse)
            is ApiFailedResponse -> listener.onFailed(apiResponse.getResponseCode(), apiResponse.getResponseMsg())
            is ApiErrorResponse -> listener.onError(apiResponse.throwable)
        }
        listener.onComplete()
    }
}

@MainThread
inline fun <T> LiveData<ApiResponse<T>>.observeState(
    owner: LifecycleOwner,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) {
    val listener = ResultBuilder<T>().also(listenerBuilder)
    observe(owner) { apiResponse ->
        when (apiResponse) {
            is ApiSuccessResponse -> listener.onSuccess(apiResponse)
            is ApiFailedResponse -> listener.onFailed(apiResponse.getResponseCode(), apiResponse.getResponseMsg())
            is ApiErrorResponse -> listener.onError(apiResponse.throwable)
        }
        listener.onComplete()
    }
}

class ResultBuilder<T> {
    var onSuccess: (data: ApiResponse<T>?) -> Unit = {}
    var onDataEmpty: () -> Unit = {}
    var onFailed: (errorCode: Int?, errorMsg: String?) -> Unit = { _, errorMsg ->
        errorMsg?.showToast()
    }
    var onError: (e: Throwable) -> Unit = { e ->
        e.message?.showToast()
    }
    var onComplete: () -> Unit = {}
}