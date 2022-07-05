package com.fdf.base.base

import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 *    Created by Administrator on 2021/11/12.
 *    Desc :
 */
open class BaseDataBindingAdapter<T, B : ViewDataBinding>(
    layoutResId: Int,
    data: MutableList<T>,
    private var variableId: Int,
    private var pairs: MutableList<Pair<Int, Any>?> = arrayListOf()
) :
    BaseQuickAdapter<T, BaseDataBindingHolder<B>>(layoutResId, data) {

    override fun convert(holder: BaseDataBindingHolder<B>, item: T) {
        val binding: ViewDataBinding? = holder.dataBinding
        if (binding != null) {
            binding.setVariable(variableId, item)

            if (pairs.isNotEmpty()) {
                pairs.forEach {
                    if (it != null) {
                        binding.setVariable(it.first, it.second)
                    }
                }
            }
            binding.executePendingBindings()
        }
    }


}