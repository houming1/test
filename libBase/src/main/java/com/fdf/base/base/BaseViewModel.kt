package com.fdf.base.base

import androidx.lifecycle.*
import com.fdf.base.ext.currentActivity
import com.fdf.base.ext.log
import com.kunminx.architecture.ui.callback.UnPeekLiveData

/**
 *    Created by Administrator on 2021/11/11.
 *    Desc :
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {

    //显示加载框
    val showDialog by lazy { UnPeekLiveData<String>() }

    //隐藏
    val dismissDialog by lazy { UnPeekLiveData<Boolean>() }

}