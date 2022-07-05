package com.gk.net.entity

/**
 *    Created by Administrator on 2021/11/16.
 *    Desc :
 */
open class ApiResponse<T>(open val error: Throwable? = null) {
    //抽象方法，用户的基类继承该类时，需要重写该方法
    open fun isSuccess(): Boolean = false

    open fun getResponseData(): T? = null

    open fun getResponseCode(): Int = -1

    open fun getResponseMsg(): String? = ""
}

open class ApiSuccessResponse<T>(val response: ApiResponse<T>?) : ApiResponse<T>() {

    override fun isSuccess(): Boolean {
        return response?.isSuccess() ?: false
    }

    override fun getResponseData(): T? {
        return response?.getResponseData()
    }

    override fun getResponseCode(): Int {
        return response?.getResponseCode() ?: -1
    }

    override fun getResponseMsg(): String? {
        return response?.getResponseMsg()
    }
}


open class ApiFailedResponse<T>(val failedCode: Int, val failedMsg: String?) : ApiResponse<T>() {
    override fun getResponseCode(): Int {
        return failedCode
    }

    override fun getResponseMsg(): String {
        return failedMsg ?: ""
    }
}

open class ApiErrorResponse<T>(val throwable: Throwable) : ApiResponse<T>(error = throwable)