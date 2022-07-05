package com.gk.net.ext

import com.jeremyliao.liveeventbus.LiveEventBus

/**
 *    Created by Administrator on 2021/11/16.
 *    Desc :
 */

fun String.showToast() {
    LiveEventBus.get<String>("show_toast").post(this)
}
