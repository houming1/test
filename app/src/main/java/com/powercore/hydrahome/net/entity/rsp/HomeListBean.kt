package com.powercore.hydrahome.net.entity.rsp

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.fdf.base.utils.CacheUtil
import com.powercore.hydrahome.Constant
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 *    Created by Administrator on 2021/12/14.
 *    Desc :
 */
@JsonClass(generateAdapter = true)
data class HomeListBean(
    @Json(name = "img")
    var img: String? = null,
    @Json(name = "createTime")
    var createTime: String? = "",
    @Json(name = "householdName")
    var householdName: String? = "",
    @Json(name = "hydraHomeHouseholdPk")
    var hydraHomeHouseholdPk: String? = "",
    @Json(name = "householdCreator")
    var householdCreator: String? = "",
    var isEdit: Boolean = false

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    fun showType(): Boolean {
        return isEdit&&isCreator()

    }
    fun isCreator(): Boolean {
        val loginResult = CacheUtil.getParcelable(Constant.LOGIN_RESULT, LoginResult::class.java)
        return loginResult?.userPk == householdCreator
    }
    @Transient
    var checked: Boolean = false

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(img)
        parcel.writeString(createTime)
        parcel.writeString(householdName)
        parcel.writeString(hydraHomeHouseholdPk)
        parcel.writeString(householdCreator)
        parcel.writeString(isEdit.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeListBean> {
        override fun createFromParcel(parcel: Parcel): HomeListBean {
            return HomeListBean(parcel)
        }

        override fun newArray(size: Int): Array<HomeListBean?> {
            return arrayOfNulls(size)
        }
    }
}