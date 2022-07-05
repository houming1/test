package com.powercore.hydrahome.ui.activity.family.add

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.base.BaseViewModel
import com.fdf.base.bind.RecyclerViewConfig
import com.fdf.base.ext.log
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.gk.net.ext.toRequestBody
import com.gk.net.utils.toJson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.BR
import com.powercore.hydrahome.databinding.ItemMemberBinding
import com.powercore.hydrahome.ext.handEmail
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.AddHomeVo
import com.powercore.hydrahome.net.entity.rsp.HomeSettingRsp
import com.powercore.hydrahome.net.entity.rsp.VerifyRsp

class NewHouseholdViewModel : BaseViewModel() {

    var homePk = ""
    var type = 0//0 添加 1 修改
    val homeLiveData = MutableLiveData<HomeSettingRsp>()

    //*****************************************************************************
    //*  获取个人key
    //*****************************************************************************
    fun getHomePk(block: (String) -> Unit) {
        request({ apiService.getHomePk() }, isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    data?.let { block(it) }
                }
            })
    }

    //*****************************************************************************
    //*  新增家庭
    //*****************************************************************************
    val delHomeResult = MutableLiveData<Boolean>()
    fun delHome() {
        val map = mapOf("homePk" to homePk)
        request(
            { apiService.delHome(map.toRequestBody()) },
            isShowLoad = true,
            loadStr = "deleting...",
            listenerBuilder = {
                onSuccess = { data, msg ->
                    delHomeResult.value = true
                }
                onFailed = { errorCode, errorMsg ->
                    errorMsg?.toast()
                }
            })
    }


    //*****************************************************************************
//*  新增家庭
//*****************************************************************************
    val addHomeLiveData = MutableLiveData<Any>()
    val addHomeParams = MutableLiveData(AddHomeVo())

    //新增家庭参数
    fun addHome(pk: String) {
        addHomeParams.value!!.apply {
            homePk = pk
            addMemberLiveData.value!!.forEach {
                userPks.add(it.userPk!!)
            }
        }
        request(
            { apiService.addHome(addHomeParams.value!!.toRequestBody()) },
            isShowLoad = true,
            "Adding...",
            listenerBuilder = {
                onSuccess = { data, msg ->
                    addHomeLiveData.value = data
                    msg?.toast()
                    LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME).post(true)
                }
                onFailed = { _, errorMsg ->
                    errorMsg?.log()
                }
                onComplete = {
                }
            })
    }

    //*****************************************************************************
//*  验证用户名
//*****************************************************************************
    val addMemberLiveData = MutableLiveData<MutableList<VerifyRsp>>(arrayListOf())
    val verifyEmail = MutableLiveData("")
    fun homeVerify() {
        val map = mapOf("email" to verifyEmail.value!!)
        request(
            { apiService.homeVerify(map.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    if (data != null) {
                        //  if (type == 1) {
                        //修改
                        //  addMember(verifyEmail.value!!)
                        // } else {
                        //新增
                        data.userName = data.userName!!.handEmail()
                        addMemberLiveData.value!!.add(data)
                        memberRvConfig.setList(addMemberLiveData.value!!)
                        // }

                    }
                }
                onFailed = { errorCode: Int, errorMsg: String? ->
                    errorMsg?.toast()
                }
            })
    }

//*****************************************************************************
//*  获取家庭设置
//*****************************************************************************

    var memberRvConfig = RecyclerViewConfig.Builder<VerifyRsp, ItemMemberBinding>()
        .adapter(BaseDataBindingAdapter(R.layout.item_member, addMemberLiveData.value!!, BR.data))
        .build()
    val getHomeLiveData = MutableLiveData<HomeSettingRsp>()

    fun getHomeSetting() {
        val map = mapOf("hydraHomeHouseholdPk" to homePk!!)
        addMemberLiveData.value!!.clear()
        memberRvConfig.getAdapter().notifyDataSetChanged()
        request(
            { apiService.getHomeSetting(map.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    if (data != null) {
                        addHomeParams.value!!.hydraHomeHouseholdPk = data.hydraHomeHouseholdPk
                        getHomeLiveData.value = data
                        for (i in 0..data.members!!.size - 1) {
                            var verifyRsp = VerifyRsp()
                            verifyRsp.avatarUrl = data.members!![i].avatar
                            verifyRsp.userName = data.members!![i].hydraHomeUserName
                            verifyRsp.homeCreatorUserPk = data.members!![i].householdCreator
                            verifyRsp.homeMemberPk = data.members!![i].hydraHomeUserPk
                            verifyRsp.userPk = data.members!![i].hydraHomeUserPk
                            addMemberLiveData.value!!.add(verifyRsp)
                        }
                        addHomeLiveData.value = addMemberLiveData.value
                        Log.e("hm---", addMemberLiveData.value!!.toJson())
                        data.members!!.size.toString().log()
                        memberRvConfig.setList(addMemberLiveData.value!!)
                        memberRvConfig.getAdapter().notifyDataSetChanged()
                        homeLiveData.value = data
                    }
                }
                onFailed = { errorCode: Int, errorMsg: String? ->
                    errorMsg?.toast()
                }
            })
    }

    //*****************************************************************************
//*  修改功率
//*****************************************************************************
    val isUpdateSuccess = MutableLiveData<Boolean>()
    fun updateSetting() {
        isUpdateSuccess.value = false
        val map = mapOf(
            "homeSettingPk" to homeLiveData.value!!.hydraHomeHouseholdPk,
            "maxPower" to addHomeParams.value!!.householdMaxLoad
        )
        request(
            { apiService.updateSetting(map.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    isUpdateSuccess.value = true
                    LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME).post(true)
                }
                onFailed = { errorCode: Int, errorMsg: String? ->
                    errorMsg?.toast()
                }
            })
    }

    //*****************************************************************************
//*  修改name note
//*****************************************************************************
    fun update(block: () -> Unit) {
        val map = mapOf(
            "homePk" to homeLiveData.value!!.hydraHomeHouseholdPk,
            "homeName" to addHomeParams.value!!.householdName,
            "note" to addHomeParams.value!!.note,
            "img" to ""
        )
        request(
            { apiService.update(map.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME).post(true)
                    block()
                }
                onFailed = { errorCode: Int, errorMsg: String? ->
                    errorMsg?.toast()
                }
            })
    }

    //*****************************************************************************
//*  删除成员
//*****************************************************************************
    fun delete(homeMemberPk: String, success: () -> Unit = {}) {
        val map = mapOf("homeMemberPk" to homeMemberPk)
        request(
            { apiService.delete(map.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    success.invoke()
//                    LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME).post(true)
//                    addMemberLiveData.value!!.removeAt(position)
//                    memberRvConfig.getAdapter().removeAt(position)
//                    memberRvConfig.getAdapter().notifyDataSetChanged()
                    getHomeSetting()
                }
                onFailed = { errorCode: Int, errorMsg: String? ->
                    errorMsg?.toast()
                }
            })
    }

    //*****************************************************************************
//*  添加成员
//*****************************************************************************
    fun addMember(email: String) {
        val map = mapOf("homePk" to homeLiveData.value!!.hydraHomeHouseholdPk, "email" to email)
        request(
            { apiService.addMember(map.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    getHomeSetting()
                }
                onFailed = { errorCode: Int, errorMsg: String? ->
                    errorMsg?.toast()
                }
            })
    }

    //*****************************************************************************
//*  修改
//*****************************************************************************

    fun updateHousehold() {
        addHomeParams.value!!.apply {
            addMemberLiveData.value!!.forEach {
                userPks.add(it.userPk!!)
            }
        }
        request(
            { apiService.updateHousehold(addHomeParams.value!!.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, msg ->
                    addHomeLiveData.value = data
                    msg?.toast()
                    LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME).post(true)
                }
                onFailed = { errorCode: Int, errorMsg: String? ->
                    errorMsg?.toast()
                }
            })
    }
}