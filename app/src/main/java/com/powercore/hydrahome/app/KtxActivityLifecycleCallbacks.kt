package com.powercore.hydrahome.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.powercore.hydrahome.ext.hiddenLoading
import com.powercore.hydrahome.ext.showLoading
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.addActivity
import com.fdf.base.ext.currentActivity
import com.fdf.base.ext.log
import com.fdf.base.ext.removeActivity

open class KtxActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        addActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
//        "onActivityStarted".log()
    }

    override fun onActivityResumed(activity: Activity) {
        if (currentActivity is BaseDbVmActivity<*, *>) {
            val act = currentActivity as BaseDbVmActivity<*, *>
            act.mViewModel.showDialog.observeInActivity(act) {
                it.log()
                showLoading(act, it)
            }
            act.mViewModel.dismissDialog.observeInActivity(act) {
                hiddenLoading()
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
//        "onActivityPaused".log()
    }

    override fun onActivityStopped(activity: Activity) {
//        "onActivityStopped".log()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
//        "onActivitySaveInstanceState".log()
    }

    override fun onActivityDestroyed(activity: Activity) {
        removeActivity(activity)
    }
}