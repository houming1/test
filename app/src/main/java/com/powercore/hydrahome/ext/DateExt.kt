package com.powercore.hydrahome.ext

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 *    Created by Administrator on 2021/12/7.
 *    Desc :
 */
/**
 * TODO 日期格式化
 * 目标格式 星期几,月份 日期 年份
 *
 * @param local
 * @return
 */
fun Long.getDate(local: Locale = Locale.getDefault()): String {
    val df: DateFormat = SimpleDateFormat("EE,LLL dd yyyy", local)
    df.timeZone = TimeZone.getDefault()
    return df.format(this)
}

fun Long.getDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val df: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    df.timeZone = TimeZone.getDefault()
    return df.format(this)
}

fun String.getUTCByLocal(pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
    val utcSdf: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    utcSdf.timeZone = TimeZone.getDefault()
    return utcSdf.parse(this)!!.time
}

fun Long.getLocalByUTC(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val utcSdf: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    utcSdf.timeZone = TimeZone.getTimeZone("UTC")
    return utcSdf.format(this)
}

fun String.utc2Local(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    if (this.isEmpty()) {
        return ""
    }
    return try {
        val utcFormat = SimpleDateFormat(pattern, Locale.getDefault())
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val utcDate = utcFormat.parse(this)!!

        val localFormat = SimpleDateFormat(pattern, Locale.getDefault())
        localFormat.timeZone = TimeZone.getDefault()
        localFormat.format(utcDate)
    } catch (e: Exception) {
        ""
    }
}

fun Long.nowDate(pattern: String = "yyyy-MM-dd"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun Long.nowTime(pattern: String = "HH:mm:ss"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun Long.nowDateTime(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun String.dateFormat(pattern: String = "yyyy-MM-dd HH:mm:ss"):String{
    return  SimpleDateFormat(pattern, Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(this))
}

fun Long.timeDiff(): String {
    val nd = 10000 * 24 * 60 * 60
    var nh = 1000 * 60 * 60
    var nm = 1000 * 60
    var ns = 1000
    var diff = this - System.currentTimeMillis()

    var day = diff / nd
    var hour = diff % nd / nh
    var min = diff % nd % nm / nm
    var sec = diff % nd % nd % nd / ns
    return "${if (hour < 10) "0$hour" else "$hour"}:${if (min < 10) "0$min" else "$min"}:${if (sec < 10) "0$sec" else "$sec"}"
}