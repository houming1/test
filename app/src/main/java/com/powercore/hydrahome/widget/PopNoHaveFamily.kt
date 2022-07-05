package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.log
import com.lxj.xpopup.core.CenterPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopNoHaveFamilyBinding


/**
 * TODO 没有家庭提示弹窗
 *
 * @constructor
 * TODO
 *
 * @param context
 */
class PopNoHaveFamily constructor(context: Context) : CenterPopupView(context) {

    private var clickCallBack: ClickCallBack? = null
    private lateinit var bind: PopNoHaveFamilyBinding

    override fun getImplLayoutId(): Int {
        return R.layout.pop_no_have_family
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!

        bind.apply {
            //点击了 GO 按钮
            btnGo.setOnClickListener {
                "点击了go".log()
                clickCallBack?.clickGo()
            }
            //点击关闭按钮
            ivClose.setOnClickListener { dismiss() }
        }
    }

    interface ClickCallBack {
        /**
         * TODO 点击红 GO 按钮
         *
         */
        fun clickGo()
    }

    fun setClickCallBack(callback: ClickCallBack) {
        this.clickCallBack = callback
        "点击了go${clickCallBack==null}".log()
    }
}