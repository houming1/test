package com.powercore.hydrahome.ui.activity.login

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.ResultState
import com.fdf.base.ext.md5
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.LoginVo
import com.powercore.hydrahome.net.entity.rsp.LoginResult


class LoginViewModel : BaseViewModel() {
    val loginResultLiveData = MutableLiveData<ResultState<LoginResult>>()
    val loginReq = MutableLiveData(LoginVo())
    fun login() {
        loginReq.value?.apply {
            if (account.isBlank() || password.isBlank()) {
                "Please input the required information".toast()
                return
            }
        }
        val map =
            mapOf("account" to loginReq.value!!.account, "password" to loginReq.value!!.password.md5())
        request({ apiService.loginByPwd(map.toRequestBody()) }, loginResultLiveData, true)
    }
}