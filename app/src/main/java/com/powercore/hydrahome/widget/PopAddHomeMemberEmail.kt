package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.getString
import com.fdf.base.ext.isEmail
import com.fdf.base.ext.toast
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopAddHomeBinding


/**
 *    Created by Administrator on 2021/12/16.
 *    Desc :
 */
class PopAddHomeMemberEmail(context: Context) : BottomPopupView(context) {


    private lateinit var bind: PopAddHomeBinding

    private var mCallBack: CallBack? = null

    override fun getImplLayoutId() = R.layout.pop_add_home

    fun setCallBack(callBack: CallBack) {
        mCallBack = callBack
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        bind.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                if (bind.input.text.toString().trim().isBlank()) {
                    getString(R.string.txt_enter_the_member_email).toast()
                    return@setOnClickListener
                }
                if (!bind.input.text.toString().trim().isEmail()) {
                    getString(R.string.txt_incorrect_eamil).toast()
                    return@setOnClickListener
                }
                mCallBack?.add(bind.input.text.toString().trim())
                dismiss()
            }
        }
    }


    interface CallBack {
        fun add(memberEmail: String)
    }
}