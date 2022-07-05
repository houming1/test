package com.fdf.base.ext


import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.imageLoader
import coil.loadAny
import coil.request.Disposable
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.fdf.base.app.appContext
import me.jessyan.autosize.utils.AutoSizeUtils
import java.io.File

/** @see ImageView.loadAny */
@JvmSynthetic
inline fun ImageView.load(
    uri: String?,
    imageLoader: ImageLoader = appContext.imageLoader,
    builder: ImageRequest.Builder.() -> Unit = {
        allowHardware(false)
    }
): Disposable {
    return if (uri!!.startsWith("http://") || uri.startsWith("https://")) {
        loadAny(uri, imageLoader, builder)
    } else {
        loadAny(File(uri), imageLoader, builder)
    }
}

@JvmSynthetic
inline fun ImageView.loadFile(
    file: File?,
    imageLoader: ImageLoader = appContext.imageLoader,
    builder: ImageRequest.Builder.() -> Unit = {}
): Disposable = loadAny(file, imageLoader, builder)

@JvmSynthetic
fun ImageView.load(
    uri: String? = "",
    imageLoader: ImageLoader = appContext.imageLoader,
    placeHolder: Drawable? = null,
    errorImg: Drawable? = null,
    roundRadius: Float = 0f,
    isCircle: Boolean = false,
): Disposable {
    val builder: ImageRequest.Builder.() -> Unit = {
        if (isCircle) {
            //圆形
            transformations(CircleCropTransformation())
        } else if (roundRadius != 0f) {
            val radius = AutoSizeUtils.dp2px(context, roundRadius).toFloat()
            //圆角
            transformations(
                RoundedCornersTransformation(
                    radius,
                    radius,
                    radius,
                    radius
                )
            )
        }
        placeHolder?.let {
            placeholder(placeHolder)

        }
        errorImg?.let {
            error(errorImg)
        }
        //淡入淡出
        crossfade(true)
        //设置显示动画的时间
        crossfade(200)
    }
    return loadAny(
        if (uri == null) {
            ""
        } else if (uri.startsWith("http://") || uri.startsWith("https://")) {
            uri
        } else {
            File(uri)
        }, imageLoader, builder
    )
}