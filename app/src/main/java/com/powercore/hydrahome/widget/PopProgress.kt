package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.lxj.xpopup.core.CenterPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopProgressBinding


/**
 *    Created by Administrator on 2021/12/16.
 *    Desc :
 */
class PopProgress(context: Context) : CenterPopupView(context) {


    private lateinit var bind: PopProgressBinding

    override fun getImplLayoutId() = R.layout.pop_progress


    fun setProgress(value: Int) {
        bind.circleProgressView.progress = value
        bind.circleProgressView.text="${value}%"
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        bind.circleProgressView.progress
    }
}