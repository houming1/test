package com.powercore.hydrahome.ui.activity.register

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.*
import com.fdf.base.getString
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.R
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.RegisterVo
import com.powercore.hydrahome.net.entity.rsp.LoginResult
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterViewModel : BaseViewModel() {
    val registerReq = MutableLiveData(RegisterVo())

    /**
     * 验证用户名
     */
    inline fun validUserName(crossinline block: () -> Unit = {}) {
        if (registerReq.value!!.name.isBlank()) {
            "Please input the required information".toast()
            return
        }
        if (isNumeric(registerReq.value!!.name)) {
            getString(R.string.txt_name_cannot_be_a_pure_number).toast()
            return
        }
        request(
            { apiService.validUserName(registerReq.value!!.name) },
            true,
            "Sending...",
            listenerBuilder = {
                onSuccess = { _, msg ->
                    msg?.toast()
                    block()
                }
                onFailed = { code, msg ->
                    msg?.toast()
                }
            })
    }

    /** * 纯数字
     * @param str
     * @return
     */
    fun isNumeric(str: String): Boolean {
        var i = str.length
        while (--i >= 0) {
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    }

    val loginResultLiveData = MutableLiveData<ResultState<LoginResult>>()

    fun register() {
        registerReq.value?.apply {
            if (name.isBlank() || email.isBlank() || !email.isEmail() || note.isBlank() || password.isBlank()) {
                "Please input the required information".toast()
                return
            }
            if (password != checkedPassword) {
                "Inconsistent passwords".toast()
            }
            if (note.length < 6) {
                "The verification code is incorrect".toast()
                return
            }
            if (!isPassword(password)) {
                "The password should be a combination of at least eight digits of numbers and letters".toast()
                return
            }
        }
        registerReq.value!!.apply {
            password = password.md5()
        }
        val body = registerReq.value!!.toRequestBody()
        request({ apiService.updateSelfInfo(body) }, true, "Sending...", listenerBuilder = {
            onSuccess = { _, msg ->
                getString(R.string.txt_register_success).toast()
                val map = mapOf(
                    "account" to registerReq.value!!.email,
                    "password" to registerReq.value!!.password
                )
                request({ apiService.loginByPwd(map.toRequestBody()) }, loginResultLiveData, true)
            }
            onFailed = { code, msg ->
                msg?.toast()
            }
        })
    }

    fun isPassword(password: String?): Boolean {
        val regex = "^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$"
        val p: Pattern = Pattern.compile(regex)
        val m: Matcher = p.matcher(password)
        val isMatch: Boolean = m.matches()
        return isMatch
    }

    /**
     * 发送验证码
     */
    val sendSuccess = MutableLiveData<Boolean>()
    fun sendCode() {
        "点击了验证码".log()
        if (registerReq.value!!.email.isBlank()) {
            "Please input the required information".toast()
            return
        }
        val map = mapOf("email" to registerReq.value!!.email)
        request(
            { apiService.sendCode(map.toRequestBody()) },
            true,
            "Sending...",
            listenerBuilder = {
                onSuccess = { _, msg ->
//                    msg?.toast()
                    "success".toast()
                    sendSuccess.postValue(true)
                }
                onFailed = { code, msg ->
                    msg?.toast()
                    sendSuccess.postValue(false)
                }
            })
    }
}