package com.powercore.hydrahome.utils

import android.content.Context

import kotlinx.coroutines.*
import kotlin.time.Duration

/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2022/05/10
 *  desc   :
 *
 */
object CoroutineTask {
    var isActive = true
   private var isStarted=false
    var repeatScope: CoroutineScope? = null
    fun start(delay: Int,task:()->Unit) {
        isStarted=true
        repeatScope = CoroutineScope(Dispatchers.IO)
        repeatScope?.launch {
            while (isActive){
                task.invoke()
                delay(delay*1000L)
            }
        }
    }

    fun cancel() {
        repeatScope?.cancel()
        repeatScope=null
        isStarted=false
    }

    fun isStart()= isStarted
}