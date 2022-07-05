package com.powercore.hydrahome.net.entity.rsp

import com.fdf.base.utils.CacheUtil
import com.powercore.hydrahome.Constant

import com.powercore.hydrahome.ext.handEmail
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

/*
* "data": {
		"homeCreatorUserPk": "520563486297030656",
		"homeMaxPower": "1.0",
		"homeName": "test",
		"homePk": "523188287431639040",
		"homeReservedPower": "",
		"homeSettingPk": "523188288031424512",
		"img": "",
		"members": [{
			"avatarUrl": "https://oss.cnpowercore.com/avatar/2021_12/529117747667140608.png",
			"homeMemberPk": "523188288064978944",
			"userName": "jiangkuikui",
			"userPk": "520563486297030656"
		}],
		"note": "1"
	}
*
* */
@JsonClass(generateAdapter = true)
data class HomeSettingRsp(
    @Json(name = "hydraHomeHouseholdPk")
    var hydraHomeHouseholdPk: String = "",
    @Json(name = "householdCreator")
    var householdCreator: String = "",
    @Json(name = "householdName")
    var householdName: String = "",
    @Json(name = "householdMaxLoad")
    var householdMaxLoad: String = "",
    @Json(name = "dayStartTime")
    var dayStartTime: String = "",
    @Json(name = "dayStopTime")
    var dayStopTime: String = "",
    @Json(name = "dayRate")
    var dayRate: String = "",
    @Json(name = "nightStartTime")
    var nightStartTime: String = "",
    @Json(name = "nightStopTime")
    var nightStopTime: String = "",
    @Json(name = "nightRate")
    var nightRate: String = "",
    @Json(name = "delay")
    var delay: String = "",
    @Json(name = "note")
    var note: String = "",

) {

    fun isCreator(): Boolean {
        val loginResult = CacheUtil.getParcelable(Constant.LOGIN_RESULT, LoginResult::class.java)
        return loginResult?.userPk == householdCreator
    }

    @Json(name = "members")
    var members: MutableList<VerifyMemberRsp>? = null
        set(value) {
            value?.forEach {
                it.householdCreator = householdCreator
            }
            if (value?.isNotEmpty() == true) {
                val fromIndex = value.indexOfFirst { it.householdCreator == householdCreator }!!
                if (fromIndex != 0) {
                    Collections.swap(value, fromIndex, 0)
                }
            }
            field = value
        }

    @JsonClass(generateAdapter = true)
    data class VerifyMemberRsp(
        @Json(name = "avatar")
        val avatar: String? = null,
        @Json(name = "hydraHomeUserPk")
        val hydraHomeUserPk: String? = null,
        @Json(name = "hydraHomeUserName")
        var hydraHomeUserName: String? = null,
        @Json(name = "hydraHomeUserEmail")
        val hydraHomeUserEmail: String? = null,
        @Transient
        var householdCreator: String? = ""
    ) {

        fun getEmail() = if (isMe()) "Me" else hydraHomeUserName!!.handEmail()

        fun isMe(): Boolean {
            val loginResult = CacheUtil.getParcelable(Constant.LOGIN_RESULT, LoginResult::class.java)
            return hydraHomeUserPk == loginResult?.userPk
        }

        fun isCreator(): Boolean {
            val loginResult = CacheUtil.getParcelable(Constant.LOGIN_RESULT, LoginResult::class.java)
            return loginResult?.userPk == householdCreator
        }


        fun showDel(): Boolean {
            return if (isMe()) {
                !isCreator()
            } else {
                isCreator()
            }
        }
    }

}
@JsonClass(generateAdapter = true)
data class VerifyRsp(
    @Json(name = "avatarUrl")
    var avatarUrl: String? = null,
    @Json(name = "homeMemberPk")
    var homeMemberPk: String? = null,
    @Json(name = "userName")
    var userName: String? = null,
    @Json(name = "userPk")
    var userPk: String? = null,

    @Transient
    var homeCreatorUserPk: String? = ""
){

    fun getEmail() = if (isMe()) "Me" else userName!!.handEmail()

    fun isMe(): Boolean {
        val loginResult = CacheUtil.getParcelable(Constant.LOGIN_RESULT, LoginResult::class.java)
        return userPk == loginResult?.userPk
    }

    fun isCreator(): Boolean {
        val loginResult = CacheUtil.getParcelable(Constant.LOGIN_RESULT, LoginResult::class.java)
        return loginResult?.userPk == homeCreatorUserPk
    }


    fun showDel(): Boolean {
        return if (isMe()) {
            !isCreator()
        } else {
            isCreator()
        }
    }
}
