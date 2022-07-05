package com.fdf.base.ext

import android.text.TextUtils

/**
 *    Created by Administrator on 2021/12/7.
 *    Desc :
 */
//邮箱验证
fun String.isEmail(): Boolean {
    val strPattern = "^[a-zA-Z0-9][\\w.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z.]*[a-zA-Z]$"
    return if (TextUtils.isEmpty(strPattern)) {
        false
    } else {
        this.matches(Regex(strPattern))
    }
}

//邮箱验证
fun String.isPhone(): Boolean {
    val num = "[1][358]\\d{9}"
    return if (TextUtils.isEmpty(this)) {
        false
    } else {
        //matches():字符串是否在给定的正则表达式匹配
        matches(Regex(num))
    }
}