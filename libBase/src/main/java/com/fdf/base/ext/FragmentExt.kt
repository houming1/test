package com.fdf.base.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fdf.base.base.BaseDbFragment
import com.fdf.base.exception.GlobalCoroutineExceptionHandler
import kotlinx.coroutines.*

/**
 *    Created by Administrator on 2021/11/26.
 *    Desc :
 */

fun <T> BaseDbFragment<*>.parseState(
    resultState: ResultState<T>,
    onSuccess: (T) -> Unit,
    onFailed: ((Int, String) -> Unit) = { code, msg -> },
    onError: ((Throwable) -> Unit)? = null,
) {
    when (resultState) {
        is ResultState.Success -> {
            resultState.data?.let { onSuccess(it) }
        }
        is ResultState.Failed -> {
            resultState.msg?.let { onFailed(resultState.code, it) }
        }
        is ResultState.Error -> {
            onError?.let { it(resultState.error) }
        }
    }
}

inline fun Fragment.requestMain(
    errCode: Int = -1, errMsg: String = "", report: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
        block.invoke(this)
    }
}

inline fun Fragment.requestIO(
    errCode: Int = -1, errMsg: String = "", report: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(Dispatchers.IO + GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
        block.invoke(this)
    }
}

inline fun Fragment.delayMain(
    errCode: Int = -1, errMsg: String = "", report: Boolean = false,
    delayTime: Long, noinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
        withContext(Dispatchers.IO) {
            delay(delayTime)
        }
        block.invoke(this)
    }
}