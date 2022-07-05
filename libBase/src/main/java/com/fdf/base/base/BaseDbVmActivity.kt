package com.fdf.base.base

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.fdf.base.app.appContext
import java.lang.reflect.ParameterizedType


/**
 *    Created by Administrator on 2021/11/10.
 *    Desc :
 */
abstract class BaseDbVmActivity<VB : ViewDataBinding, VM : BaseViewModel>(
    title: String = "Title",
    @ColorInt private var titleBarColor:Int=-1,
     private var rightIcon: Drawable ?= null,
    isOverrideContentView: Boolean = false,
    rightClick: () -> Unit = {}
) : BaseDbActivity<VB>(title,titleBarColor,rightIcon, isOverrideContentView, rightClick) {

    val mViewModel: VM by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(mViewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mViewModel)
    }

    private fun getViewModel(): VM {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]          // index of 0 means first argument of BaseActivity class param
        return ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(appContext)).get(type as Class<VM>)
    }


}