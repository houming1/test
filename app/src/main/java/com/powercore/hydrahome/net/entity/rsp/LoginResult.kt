package com.powercore.hydrahome.net.entity.rsp

import android.os.Parcel
import android.os.Parcelable

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResult(
    var name: String? = "",
    var ssoticket: String? = "",
    var userPk: String? = "",
    var email: String? = "",
    var sex: String? = null,
    var birthday: String? = null,
    var phone: String? = null,
    var refreshToken: String? = "",
    var avatarUrl: String? = null
) : Parcelable {
    fun birthDayStr(): String {
        return birthday?.split(" ".toRegex())?.get(0)?:""
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(ssoticket)
        parcel.writeString(userPk)
        parcel.writeString(email)
        parcel.writeString(sex)
        parcel.writeString(birthday)
        parcel.writeString(phone)
        parcel.writeString(refreshToken)
        parcel.writeString(avatarUrl)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginResult> {
        override fun createFromParcel(parcel: Parcel): LoginResult {
            return LoginResult(parcel)
        }

        override fun newArray(size: Int): Array<LoginResult?> {
            return arrayOfNulls(size)
        }
    }
}
