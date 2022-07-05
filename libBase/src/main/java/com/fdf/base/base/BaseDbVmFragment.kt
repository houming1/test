package com.fdf.base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.fdf.base.app.appContext
import java.lang.reflect.ParameterizedType


/**
 *    Created by Administrator on 2021/11/10.
 *    Desc :
 */
abstract class BaseDbVmFragment<VB : ViewDataBinding, VM : BaseViewModel> : BaseDbFragment<VB>() {


    protected val mViewModel: VM by lazy { getViewModel() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        lifecycle.addObserver(mViewModel)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun getViewModel(): VM {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]          // index of 0 means first argument of BaseActivity class param
        return ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(appContext)).get(type as Class<VM>)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycle.removeObserver(mViewModel)
    }
}