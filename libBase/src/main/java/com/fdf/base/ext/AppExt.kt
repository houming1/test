package com.fdf.base.ext

import android.app.Activity
import java.util.*

/**
 *    Created by Administrator on 2021/11/27.
 *    Desc :
 */

val activityList = LinkedList<Activity>()

//栈顶
val currentActivity: Activity? get() = if (activityList.isNullOrEmpty()) null else activityList.last

/**
 * 入栈
 */
fun addActivity(activity: Activity) {
    activityList.add(activity)
    "addActivity  ${activityList.size}".log()


}

/**
 * 出栈
 */
fun removeActivity(activity: Activity) {
    if (!activity.isFinishing) {
        activity.finish()
    }
    activityList.remove(activity)
    "removeActivity for act ${activity.javaClass.simpleName}".log()
}

/**
 * 出栈
 */
fun removeActivity(cls: Class<*>) {
    if (activityList.isNullOrEmpty()) return
    val index = activityList.indexOfFirst { it.javaClass == cls }
    if (index == -1) return
    if (!activityList[index].isFinishing) {
        activityList[index].finish()
    }
    activityList.removeAt(index)
}

/**
 * 全部出栈
 */
fun removeAllActivity() {
    activityList.forEach {
        "removeAllActivity for act ${it.javaClass.simpleName}".log()
        if (!it.isFinishing) {
            it.finish()
        }
    }
    activityList.clear()
}