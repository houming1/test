package com.powercore.hydrahome.utils


import android.util.Log
import com.blankj.utilcode.constant.TimeConstants
import java.lang.StringBuilder
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


object DateUtil {

    fun stringToLong(date: String?): Long {
        if (date.isNullOrBlank())
            return 0

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val tempDate = simpleDateFormat.parse(date)

        return tempDate.time
    }

    fun stringToLongHMS(date: String): Long {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val tempDate = simpleDateFormat.parse(date)
        return tempDate.time
    }


    fun formatTimeStamp(t: String?): Long {
        if (t.isNullOrEmpty()) {
            return 0
        }
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val date = simpleDateFormat.parse(t)
        return date.time
    }


    fun formatDateStamp(t: String?): Long {
        if (t.isNullOrEmpty()) {
            return 0
        }
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val date = simpleDateFormat.parse(t)
        return date.time
    }

    fun formatTimeDef(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
//        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val simpleDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.DEFAULT)
        val date = simpleDateFormat.format(Date(stringToLong(t)))
        return formatTime12H(t)
    }

    fun formatTime12H(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }

        val lo = Locale.getDefault()

        val simpleDateFormatTime = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH)
        val simpleDateFormat = DateFormat.getDateInstance(DateFormat.SHORT)


        val date = simpleDateFormat.format(Date(stringToLong(t)))
        val dateT = simpleDateFormatTime.format(Date(stringToLong(t)))
        return date.plus(" ").plus(dateT)
    }


    fun stringToUTC(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val simpleDateFormatIn = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val simpleDateFormatIn = DateFormat.getDateTimeInstance()
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val timeZone = TimeZone.getTimeZone("UTC")
        simpleDateFormat.timeZone = timeZone

        val dateIn = simpleDateFormatIn.parse(t)
        val date = simpleDateFormat.format(dateIn)
        return date
    }

    //    utc ??????????????? ????????????
    fun UTCToString(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
        try {

            val simpleDateFormatU = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//            val simpleDateFormatU = DateFormat.getDateTimeInstance()

            val timeZoneU = TimeZone.getTimeZone("UTC")
            simpleDateFormatU.timeZone = timeZoneU
            val dateU = simpleDateFormatU.parse(t)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//            val simpleDateFormat = DateFormat.getDateTimeInstance()
            simpleDateFormat.timeZone = TimeZone.getDefault()
            val date = simpleDateFormat.format(dateU)
            return date
        } catch (e: Exception) {
            return ""
        }
    }

    fun longToString(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }


    fun longToStringSlash(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }


    fun longToStringYM(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }

    fun longToStringY(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }


    fun longToStringEN(long: Long): String {

        val simpleDateFormat = SimpleDateFormat("MMM dd,yyyy", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }

    fun longToStringDetail(long: Long): String {
        if (long == 0L) {
            return ""
        }
        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate

    }

    fun  formatDate(date:String):String{
        val simpleDateFormatU = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var  dateTime=simpleDateFormatU.parse(date)
        val simpleDateFormat1 = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
       var dateTime1=simpleDateFormat1.format(dateTime)

        return dateTime1
    }
    fun getNowDay(): String {
        val dft = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
        return dft.format(Date())
    }
    fun getNowMonth(): String {
        val dft = SimpleDateFormat("yyyy-MM", Locale.CHINESE)
        return dft.format(Date())
    }
    fun getBefore( day:Int):String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val calendar = Calendar.getInstance()

        calendar.time = Date()

        calendar.add(Calendar.DAY_OF_MONTH, -day) //???????????????  30????????????  365?????????


       var mBefore = calendar.time
        return sdf.format(mBefore)
    }
    /**
     * ?????????n???????????????n?????????
     * @param distanceDay ????????? ????????????7???????????????-7??????????????????7?????????7
     * @return
     */
    fun getOldDate(distanceDay: Int): String {
        val dft = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
        val beginDate = Date()
        val date = Calendar.getInstance()
        date.time = beginDate
        date[Calendar.DATE] = date[Calendar.DATE] + distanceDay
        var endDate: Date = Date()
        try {
            endDate = dft.parse(dft.format(date.time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dft.format(endDate)
    }

    /**
     * ???????????????????????????
     */
    fun isBeforeToday(t1: Long): Boolean {
        val sf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val d1 = sf.format(Date(t1))
        val d2 = sf.format(Date())
        return d1 < d2

    }

    fun isBeforeNow(t1: Long): Boolean {
        val sf = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault())
        val d1 = sf.format(Date(t1))
        val d2 = sf.format(Date())
        Log.e("d1:", d1)
        Log.e("d2:", d2)
        return d1 < d2
    }

    fun isAbsNow2Min(t1: String): Boolean {
        val sf = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault())
        val timeZone = TimeZone.getTimeZone("UTC")
        sf.timeZone = timeZone
        val d1 = stringToLong(t1)
        val d2 = stringToLong(sf.format(Date()))
        Log.e("d1:", "" + d1 + "  " + t1)
        Log.e("d2:", d2.toString() + "  " + sf.format(Date()))
        Log.e("d2-d1", abs(d2.minus(d1)).toString())
        return abs(d2.minus(d1)) > 120 * 1000
    }

    fun getNow(): String {
//        val sf = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault())
        val sf = DateFormat.getDateTimeInstance()
        val d2 = sf.format(Date(System.currentTimeMillis()))
        Log.e("d2:", d2)
        return d2

    }

    fun getNow1(): String {
        val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        // val sf = DateFormat.getDateTimeInstance()
        val d2 = sf.format(Date(System.currentTimeMillis()))
        Log.e("d2:", d2)
        return d2

    }

    fun getDay(t: String): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
        val lo = Locale.getDefault()
        val simpleDateFormatTime = SimpleDateFormat(" a", Locale.ENGLISH)
        val simpleDateFormat = DateFormat.getDateInstance(DateFormat.SHORT)


        val date = simpleDateFormat.format(Date(stringToLong(t)))
        val dateT = simpleDateFormatTime.format(Date(stringToLong(t)))
        return date.plus(" ").plus(dateT)
    }

    /**
     * ???????????????????????????[startTime, endTime]????????????????????????????????????
     *
     * @param nowTime ????????????
     * @param startTime ????????????
     * @param endTime ????????????
     * @return
     */

    fun containNowDate(startTime: String?, endTime: String?): Boolean {
        if (startTime.isNullOrEmpty())
            return false
        if (endTime.isNullOrEmpty())
            return false
        val sf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = sf.format(Date(System.currentTimeMillis()))
        return stringToLongHMS(now) <= stringToLongHMS(endTime) && stringToLongHMS(now) > stringToLongHMS(
            startTime
        )
    }

    fun getFriendlyTimeSpanByNow(millis: String?): String? {
        if (millis == null)
            return ""
        val now = System.currentTimeMillis()
        var span = 0L
        try {
            span = now - stringToLong(UTCToString(millis))
        } catch (e: Exception) {
            return millis
        }
        val mon = 1000 * 60 * 60 * 24 * 30L
        val year = mon * 12L
//        if (span < 0) //
//            return String.format("%t", millis)
        if (span < 1000) {
            return "just recently"
        } else if (span < TimeConstants.MIN) {
            return String.format(
                Locale.getDefault(),
                "%d seconds ago",
                span / TimeConstants.SEC
            )
        } else if (span < TimeConstants.HOUR) {
            return String.format(
                Locale.getDefault(),
                "%d minutes ago",
                span / TimeConstants.MIN
            )
        } else if (span < TimeConstants.DAY) {
            return if ((span / TimeConstants.HOUR).toInt() == 1)
                "a hour ago"
            else
                String.format(
                    Locale.getDefault(),
                    "%d hours ago",
                    span / TimeConstants.HOUR
                )
        } else if (span < mon) {
            return if ((span / TimeConstants.DAY).toInt() == 1)
                "a day ago"
            else String.format(
                Locale.getDefault(),
                "%d days ago",
                span / TimeConstants.DAY
            )
        } else if (span < year) {
            return if ((span / mon).toInt() == 1)
                "a month ago"
            else String.format(
                Locale.getDefault(),
                "%d months ago",
                span / mon
            )

        } else {
            return if ((span / year).toInt() == 1)
                "a year ago"
            else
                String.format(
                    Locale.getDefault(),
                    "%d years ago",
                    span / year
                )
        }
    }


    fun getCurrDate(pattern: String): String {
        val sf = SimpleDateFormat(pattern, Locale.getDefault())
        return sf.format(Date(System.currentTimeMillis()))
    }

    fun String.dateUTCToString(): String {
        return UTCToString(this)
    }

    fun String.toDayFrendly2(): String {
        var s = ""
        try {
            val all = this.toLong().div(1000)
            val h = all.div(60 * 60)
            val min = all.minus(h * 60 * 60).div(60)
            val sec = all.minus(h * 60 * 60).minus(min * 60)
            s = "${String.format("%1$02d", h)}:${String.format("%1$02d", min)}:${
                String.format(
                    "%1$02d",
                    sec
                )
            }"
        } catch (e: Exception) {

        }
        return s
    }

    fun formatTime(time: Int): String? {
        val hh = time / 3600
        val mm = (time - 3600 * hh) / 60
        val ss = time - 3600 * hh - 60 * mm
        val sb = StringBuilder()
        sb.append("$hh:")
        if (mm < 10) {
            sb.append("0$mm:")
        } else {
            sb.append("$mm:")
        }
        if (ss < 10) {
            sb.append("0$ss")
        } else {
            sb.append(ss.toString() + "")
        }
        return sb.toString()
    }
}