package com.fdf.base.ext

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.core.content.ContextCompat
import com.fdf.base.app.appContext

/**
 *    Created by Administrator on 2021/11/26.
 *    Desc :
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun getColor(@ColorRes color: Int): Int {
    return ContextCompat.getColor(appContext, color)
}

fun getDrwable(@DrawableRes resId: Int): Drawable? {
    return ContextCompat.getDrawable(appContext, resId)
}