package com.fdf.base.bind

import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.fdf.base.R
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.ext.getColor
import com.fdf.base.ext.load
import com.fdf.base.ext.log
import com.ruffian.library.widget.RTextView
import com.ruffian.library.widget.RView
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 *    Created by Administrator on 2021/11/25.
 *    Desc :
 */
object ViewBindAdapter {

    @BindingAdapter(value = ["resId"], requireAll = false)
    @JvmStatic
    fun imgRes(imageView: ImageView, resId: Int) {
        imageView.setImageResource(resId)
    }

    @BindingAdapter(value = ["rtvColorId"], requireAll = false)
    @JvmStatic
    fun rtvColorId(rtv: RTextView, resId: Int) {
        rtv.helper.backgroundColorNormal = getColor(resId)
    }

    @BindingAdapter(
        value = ["imageUrl", "placeHolder", "errorImg", "roundRadius", "isCircle"],
        requireAll = false
    )
    @JvmStatic
    fun imageUrl(
        imageView: ImageView,
        imageUrl: String? = "",
        placeHolder: Drawable? = null,
        errorImg: Drawable? = null,
        roundRadius: Float = 0f,
        isCircle: Boolean = false,
    ) {
        val imgUrl = imageUrl ?: ""
        imageView.load(imgUrl) {
            if (isCircle) {
                //圆形
                transformations(CircleCropTransformation())
            } else if (roundRadius != 0f) {
                val radius = AutoSizeUtils.dp2px(imageView.context, roundRadius).toFloat()
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
    }

    @BindingAdapter(
        value = ["imageUrl", "placeHolderId", "errorImgId", "roundRadius", "isCircle"],
        requireAll = false
    )
    @JvmStatic
    fun imageUrl2(
        imageView: ImageView,
        imageUrl: String? = "",
        placeHolderId: Int = -1,
        errorImgId: Int = -1,
        roundRadius: Float = 0f,
        isCircle: Boolean = false

    ) {
        val imgUrl = imageUrl ?: ""
        imageView.load(imgUrl) {
            if (isCircle) {
                //圆形
                transformations(CircleCropTransformation())
            } else if (roundRadius != 0f) {
                val radius = AutoSizeUtils.dp2px(imageView.context, roundRadius).toFloat()
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
            if (placeHolderId != -1)
                placeholder(placeHolderId)

            if (errorImgId != -1)
                error(errorImgId)

            //淡入淡出
            crossfade(true)
            //设置显示动画的时间
            crossfade(200)
        }
    }

    @BindingAdapter(value = ["afterTextChanged"])
    @JvmStatic
    fun EditText.afterTextChanged(action: (String) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                action(s.toString())
            }
        })
    }


    @BindingAdapter("noRepeatClick")
    @JvmStatic
    fun setOnClick(view: View, clickListener: () -> Unit) {
        val mHits = LongArray(2)
        view.setOnClickListener {
            System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1)
            mHits[mHits.size - 1] = SystemClock.uptimeMillis()
            if (mHits[0] < SystemClock.uptimeMillis() - 500) {
                clickListener.invoke()
            }
        }
    }

    @BindingAdapter(value = ["visible"], requireAll = false)
    @JvmStatic
    fun setVisibility(v: View, visible: Boolean) {
        v.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter(value = ["invisible"], requireAll = false)
    @JvmStatic
    fun setInVisibility(v: View, invisible: Boolean) {
        v.visibility = if (invisible) View.INVISIBLE else View.VISIBLE
    }

    @BindingAdapter(value = ["rvConfig"], requireAll = false)
    @JvmStatic
    fun bindConfig(recyclerView: RecyclerView, rvConfig: RecyclerViewConfig<Any, ViewDataBinding>) {
        rvConfig.apply(recyclerView)
    }

    @BindingAdapter(value = ["rvData"], requireAll = false)
    @JvmStatic
    fun <T> bindConfig(recyclerView: RecyclerView, rvData: MutableList<T>) {
        if (recyclerView.adapter is BaseDataBindingAdapter<*, *>) {
            (recyclerView.adapter as BaseDataBindingAdapter<T, ViewDataBinding>).setNewInstance(
                rvData
            )
        }
    }
}