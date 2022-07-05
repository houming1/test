package com.powercore.hydrahome.ui.activity.resetpassword

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.*
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.ForgetPwdVo
import java.util.regex.Matcher
import java.util.regex.Pattern

class ResetPassworViewModel: BaseViewModel() {

    /**
     * 忘记密码
     */
    val forgetPwdReq = MutableLiveData(ForgetPwdVo())
    val forgetLiveData = MutableLiveData<Boolean>()
    fun forgetPwd() {
        forgetPwdReq.value?.apply {
            if (email.isBlank() || !email.isEmail()||!code.isBlank() || password.isBlank()||checkedPassword.isBlank()) {
                "Please input the required information".toast()
                return
            }
            if (!isPassword(password)) {
                "The password should be a combination of at least eight digits of numbers and letters".toast()
                return
            }
        }
        forgetPwdReq.value!!.apply {
            password = password.md5()
        }

        val body = forgetPwdReq.value!!.toRequestBody()
        request({ apiService.forgetPwd(body) }, true, "Loading...", listenerBuilder = {
            onSuccess = { _, msg ->
                msg?.toast()
                forgetLiveData.value = true
            }
            onFailed = { code, msg ->
                msg?.toast()
            }
        })
    }

    /**
     * 发送验证码
     */
    val sendForgetPwdCodeSuccess = MutableLiveData<Boolean>()
    fun getEmailCode() {
        "点击了验证码".log()
        if (forgetPwdReq.value!!.email.isBlank()) {
            "Please input the required information".toast()
            return
        }
        request(
            { apiService.getEmailCode(forgetPwdReq.value!!.email) },
            true,
            "Sending...",
            listenerBuilder = {
                onSuccess = { _, msg ->
                    sendForgetPwdCodeSuccess.postValue(true)
                    "success".toast()
//                    msg?.toast()
                }
                onFailed = { code, msg ->
                    sendForgetPwdCodeSuccess.postValue(false)
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

}