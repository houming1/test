package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.core.BottomPopupView


import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopAvatarPikerBinding

class PopAvatarPicker constructor(context: Context) : AttachPopupView(context) {

    private lateinit var bind: PopAvatarPikerBinding

    private var mCallBack: CallBack? = null

    override fun getImplLayoutId(): Int {
        return R.layout.pop_avatar_piker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!

        bind.apply {
            tvCamera.setOnClickListener {
                mCallBack?.clickCamera()
            }
            tvPhoto.setOnClickListener {
                mCallBack?.clickPhoto()
            }

        }
    }

    fun setCallBack(callBack: CallBack) {
        this.mCallBack = callBack
    }

    interface CallBack {
        fun clickCamera()
        fun clickPhoto()
    }
}