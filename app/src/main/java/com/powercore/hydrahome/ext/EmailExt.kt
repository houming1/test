package com.powercore.hydrahome.ext

/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2021/12/29
 *  desc   :
 *
 */
fun String.handEmail(): String {
    return this.replace("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)".toRegex(), "$1****$3$4")
}