package com.fdf.base.utils

import android.content.Context
import android.util.DisplayMetrics


/**
 *    Created by Administrator on 2021/12/15.
 *    Desc :
 */
class DensityUtil {
    fun getScreenWidth(context: Context): Int {
        val screenMetrics = IntArray(2)
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        screenMetrics[0] = displayMetrics.widthPixels
        screenMetrics[1] = displayMetrics.heightPixels
        return screenMetrics[0]
    }

    fun getScreenHeight(mContext: Context): Int {
        return getScreenMetrics(mContext)[1]
    }

    fun getScreenMetrics(context: Context): IntArray {
        val screenMetrics = IntArray(2)
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        screenMetrics[0] = displayMetrics.widthPixels
        screenMetrics[1] = displayMetrics.heightPixels
        return screenMetrics
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale: Float = context.getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale: Float = context.getResources().getDisplayMetrics().density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale: Float = context.getResources().getDisplayMetrics().density
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale: Float = context.getResources().getDisplayMetrics().density
        return (spValue * fontScale + 0.5f).toInt()
    }
}