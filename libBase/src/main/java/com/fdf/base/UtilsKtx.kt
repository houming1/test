package com.fdf.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ToastUtils
import com.fdf.base.app.appContext
import com.fdf.base.databinding.CustomToastViewBinding
import java.util.*

/**
 *    Created by Administrator on 2021/11/16.
 *    Desc :
 */
inline fun <reified T : Activity> Activity.startActivity(bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

fun stringOf(@StringRes id: Int, vararg formatArgs: Any): String = getString(id, *formatArgs)

fun stringOf(@StringRes id: Int): String = getString(id)

fun EditText.getNotNullText(): String = text?.toString()?.trim() ?: ""

fun EditText.getNotNullUpperCaseText(): String = getNotNullText().uppercase(Locale.ENGLISH)

fun getString(@StringRes id: Int, vararg formatArgs: Any?): String {
    return appContext.getString(id, *formatArgs)
}

fun String.toast(duration: Int = Toast.LENGTH_SHORT) {
//    val bind = DataBindingUtil.inflate<CustomToastViewBinding>(LayoutInflater.from(appContext), R.layout.custom_toast_view, null, false)
//    bind.tvMsg.text = this
//    ToastUtils.setGravity(Gravity.CENTER, 0, 0)
    when (duration) {
        Toast.LENGTH_SHORT -> {
            ToastUtils.showShort(this)
        }
        Toast.LENGTH_LONG -> {
            ToastUtils.showLong(this)
        }
    }
}

//may only available on real device
fun EditText.openKeyBoard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.RESULT_SHOWN)
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

//may only available on real device
fun EditText.hideKeyBoard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.clickWithLimit(intervalMill: Int = 500, block: ((v: View?) -> Unit)) {
    setOnClickListener(object : View.OnClickListener {
        var last = 0L
        override fun onClick(v: View?) {
            if (System.currentTimeMillis() - last > intervalMill) {
                block(v)
                last = System.currentTimeMillis()
            }
        }
    })
}