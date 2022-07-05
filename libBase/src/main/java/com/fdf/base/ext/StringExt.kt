package com.fdf.base.ext

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ToastUtils
import com.fdf.base.BuildConfig
import com.fdf.base.R
import com.fdf.base.app.appContext
import com.fdf.base.databinding.CustomToastViewBinding
import com.gk.net.utils.toJson
import com.orhanobut.logger.Logger
import java.security.MessageDigest

/**
 *    Created by Administrator on 2021/11/26.
 *    Desc :
 */
val isDebug = BuildConfig.DEBUG
fun String.log() {
    val tag = "log"
    if (isDebug)
        Logger.t(tag).e(this)
}

fun String.logD() {
    val tag = "log"
    if (isDebug)
        Logger.t(tag).d(this)
}

fun String.logW() {
    val tag = "log"
    if (isDebug)
        Logger.t(tag).w(this)
}

fun Any.log() {
    val tag = "log"
    if (isDebug && this !is String)
        Logger.t(tag).e(this.toJson())
}

fun String.toast() {
    if (isBlank()) return
    Toast.makeText(appContext, this, Toast.LENGTH_SHORT).show()
//    val bind = DataBindingUtil.inflate<CustomToastViewBinding>(
//        LayoutInflater.from(appContext),
//        R.layout.custom_toast_view,
//        null,
//        false
//    )
//    bind.tvMsg.text = this
////    ToastUtils
////    ToastUtils.setGravity(Gravity.NO_GRAVITY, 0, 0)
////    ToastUtils.showCustomShort(bind.root)
//    ToastUtils.showShort(this)
}

/** md5加密 */
fun String.md5(): String {
    val hash = MessageDigest.getInstance("MD5").digest(toByteArray())
    val hex = StringBuilder(hash.size * 2)
    for (b in hash) {
        var str = Integer.toHexString(b.toInt())
        if (b < 0x10) {
            str = "0$str"
        }
        hex.append(str.substring(str.length - 2))
    }
    return hex.toString()
}

fun getString(idRes: Int): String {
    return appContext.getString(idRes)
}