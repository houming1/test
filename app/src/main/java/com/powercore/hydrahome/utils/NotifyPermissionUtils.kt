package com.powercore.hydrahome.utils

import android.content.Context
import androidx.core.app.NotificationManagerCompat

/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2022/04/12
 *  desc   :
 *
 */
class NotifyPermissionUtils {
    /**
     * 检查通知权限
     */
    private fun checkNotifyPermissionStatus(context: Context?): Boolean {
        context?.let {
            val manager = NotificationManagerCompat.from(context)
            // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
            return manager.areNotificationsEnabled()
        }
        return false
    }
}