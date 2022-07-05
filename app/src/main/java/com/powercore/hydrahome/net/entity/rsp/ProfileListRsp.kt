package com.powercore.hydrahome.net.entity.rsp

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileListRsp(
    @Json(name = "hydraHomeHouseholdProfileName")
    var hydraHomeHouseholdProfileName: String? = null,
    @Json(name = "hydraHomeHouseholdProfilePk")
    var hydraHomeHouseholdProfilePk: String? = null
)

@JsonClass(generateAdapter = true)
data class ProfileItemRsp(
    @Json(name = "hydraHomeHouseholdProfileDayStart")
    var hydraHomeHouseholdProfileDayStart: String? = null,

    @Json(name = "hydraHomeHouseholdProfileDayStop")
    var hydraHomeHouseholdProfileDayStop: String? = null,

    @Json(name = "hydraHomeHouseholdProfileName")
    var hydraHomeHouseholdProfileName: String? = null,

    @Json(name = "hydraHomeHouseholdProfileNightStart")
    var hydraHomeHouseholdProfileNightStart: String? = null,

    @Json(name = "hydraHomeHouseholdProfileNightStop")
    var hydraHomeHouseholdProfileNightStop: String? = null,

    @Json(name = "hydraHomeHouseholdProfilePk")
    var hydraHomeHouseholdProfilePk: String? = null,

    @Json(name = "hydraHomeHouseholdProfileWeeks")
    var hydraHomeHouseholdProfileWeeks: MutableList<Int> = mutableListOf()

) {
    fun tName(): String {

        var weeks = arrayOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
        if (hydraHomeHouseholdProfileWeeks.size == 7) {
            return "MON - SUN"
        } else {
            var names = ""
            for (i in 0 until hydraHomeHouseholdProfileWeeks.size) {
                if (i<hydraHomeHouseholdProfileWeeks.size-1){
                    names += weeks[hydraHomeHouseholdProfileWeeks[i]-1] +"·"
                }else{
                    names += weeks[hydraHomeHouseholdProfileWeeks[i]-1]
                }

            }
            return names
        }
    }


}