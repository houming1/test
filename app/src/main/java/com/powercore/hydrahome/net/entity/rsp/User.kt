package com.powercore.hydrahome.net.entity.rsp

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "userPk")
    var userPk: String? = null,
    @Json(name = "name")
    var name: String? = null,
    @Json(name = "account")
    var account: String? = null,
    @Json(name = "password")
    var password: String? = null,
    @Json(name = "email")
    var email: String? = null,
    @Json(name = "ssoticket")
    var ssoticket: String? = null,//token
    @Json(name = "sex")
    var sex: String? = null,//female ,male
    @Json(name = "birthDay")
    var birthDay: String? = null,
    @Json(name = "birthday")
    var birthday: String? = null,//-
    @Json(name = "phone")
    var phone: String? = null,
    @Json(name = "areaCode")
    var areaCode: String? = null,
    @Json(name = "avatarUrl")
    var avatarUrl: String? = null,//-
    @Json(name = "note")
    var note: String? = null,
    @Json(name = "code")
    var code: String? = null,
    @Json(name = "refresh_token")
    var refresh_token: String? = null,
    @Json(name = "background")
    var background: String? = null
)