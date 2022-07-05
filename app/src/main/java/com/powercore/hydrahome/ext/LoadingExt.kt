package com.powercore.hydrahome.ext

import android.content.Context
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.powercore.hydrahome.R

/**
 *    Created by Administrator on 2021/11/27.
 *    Desc :
 */

private var mLoadingDialog: BasePopupView? = null

fun showLoading(context: Context, msg: String) {
    if (mLoadingDialog == null)
        mLoadingDialog = XPopup.Builder(context).asLoading(msg, R.layout.dialog_loading)
    mLoadingDialog?.show()
}

fun hiddenLoading() {
    mLoadingDialog?.let {
        mLoadingDialog?.smartDismiss()
        mLoadingDialog = null
    }
}