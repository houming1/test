package com.fdf.base.anno

/**
 *    Created by Administrator on 2021/11/16.
 *    Desc :
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class ActivityConfiguration(

    val useEventBus: Boolean = false,

    val needLogin: Boolean = true
)