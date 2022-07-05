package com.powercore.hydrahome.ui.fragment.me

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.request
import com.fdf.base.utils.CacheUtil
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.Image
import com.powercore.hydrahome.net.entity.req.Logout
import com.powercore.hydrahome.net.entity.rsp.LoginResult
import com.powercore.hydrahome.net.entity.rsp.User

class MeViewModel: BaseViewModel() {
    val logout = MutableLiveData<String>()
    fun logOut() {
        request({
            apiService.logout(Logout(user = User(account = CacheUtil.getString(Constant.EMAIL))).toRequestBody())
        }, isShowLoad = true, listenerBuilder = {
            onSuccess = { data, msg ->
                logout.value = "1"
            }
            onError = {
            }
        })
    }

    var avatarUrl = MutableLiveData<String>("https://oss.cnpowercore.com/avatar/2021_12/528958850406940672.png")
    fun updateAvatar(img: String) {
        request({
            apiService.updateAvatar(Image(img = img).toRequestBody())
        }, isShowLoad = true, listenerBuilder = {
            onSuccess = { data, msg ->
                avatarUrl.value = data!!.avatarUrl
                val loginResult =
                    CacheUtil.getParcelable(Constant.LOGIN_RESULT, LoginResult::class.java)
                loginResult!!.avatarUrl = data.avatarUrl
                CacheUtil.saveParcelable(Constant.LOGIN_RESULT, loginResult)
            }
            onError = {
            }
        })
    }
}