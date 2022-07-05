package com.powercore.hydrahome.utils

import java.util.*

/**
 *    Created by Administrator on 2021/12/2.
 *    Desc :
 */

object OrderFuncUtils {

    private var queueFun = LinkedList<Function>()

    private var currentFun: (() -> Unit?)? = null // 当前任务


    fun addFunc(function: () -> Unit?) {
        val func = Function(function)
        doFunc(func)
    }

    fun finishFunc() {
        doFunc(null)
    }

    private fun doFunc(func: Function?) {
        if (func != null) {
            queueFun.offer(func)
        } else {
            currentFun = null
        }
        if (currentFun == null) {
            if (queueFun.size != 0) {
                val funNow = queueFun.poll()
                currentFun = funNow?.function
                currentFun?.invoke()
            }
        }
    }

    data class Function(var function: () -> Unit? = {})
}
