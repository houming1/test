package com.fdf.base.base

import android.graphics.drawable.Drawable

/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2021/12/01
 *  desc   :
 *
 */
interface IBaseConfig {
    fun getCacheId(): String
    fun getTitleBarLeftIcon():Drawable
}