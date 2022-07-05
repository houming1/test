package com.fdf.base.bind

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.widget.AutoLineFeedLayoutManager
import com.fdf.base.widget.ItemDecorationPowerful
import me.jessyan.autosize.utils.AutoSizeUtils

class RecyclerViewConfig<T, VB : ViewDataBinding> private constructor(
    private val mAdapter: BaseDataBindingAdapter<T, VB>,
    private val mIsNestedScrollingEnabled: Boolean,
    private val hasFixedSize: Boolean,
    private val spanCount: Int,
    private val isHorizontal: Boolean,
    private val dividerColor: Int,
    private val dividerWidth: Float,
    private val isFlow: Boolean
) {

    fun apply(rv: RecyclerView) {
        if (isFlow) {
            val layoutManager = AutoLineFeedLayoutManager()
            rv.apply {
                setLayoutManager(layoutManager)
                adapter = mAdapter
            }
            return

        }
        val width = AutoSizeUtils.dp2px(rv.context, dividerWidth)
        val layoutManager: RecyclerView.LayoutManager
        if (spanCount > 0) {
            layoutManager = GridLayoutManager(rv.context, spanCount)
            if (dividerWidth > 0f) {
                if (rv.itemDecorationCount == 0) {
                    rv.addItemDecoration(
                        ItemDecorationPowerful(
                            ItemDecorationPowerful.GRID_DIV,
                            dividerColor,
                            width
                        )
                    )
                }
            }
        } else {
            layoutManager = LinearLayoutManager(
                rv.context,
                if (isHorizontal) LinearLayoutManager.HORIZONTAL else LinearLayoutManager.VERTICAL,
                false
            )
            if (dividerWidth > 0f) {
                if (rv.itemDecorationCount == 0) {
                    rv.addItemDecoration(
                        ItemDecorationPowerful(
                            if (isHorizontal) ItemDecorationPowerful.HORIZONTAL_DIV else ItemDecorationPowerful.VERTICAL_DIV,
                            dividerColor,
                            width
                        )
                    )
                }
            }
        }
        rv.apply {
            setLayoutManager(layoutManager)
            isNestedScrollingEnabled = mIsNestedScrollingEnabled
            setHasFixedSize(hasFixedSize)
            adapter = mAdapter
        }
    }

    fun setList(newList: MutableList<T>) {
        mAdapter.setList(newList)
    }

    fun setNewInstance(newList: MutableList<T>) {
        mAdapter.setNewInstance(newList)
    }

    fun addData(position: Int, data: T) {
        mAdapter.addData(position, data)
    }

    fun removeAt(position: Int) {
        mAdapter.removeAt(position)
    }

    fun getAdapter() = mAdapter

    class Builder<T, VB : ViewDataBinding> {

        private lateinit var adapter: BaseDataBindingAdapter<T, VB>
        private var isNestedScrollingEnabled = true
        private var hasFixedSize = true
        private var spanCount: Int = 0
        private var isHorizontal: Boolean = false
        private var dividerColor: Int = Color.TRANSPARENT
        private var dividerWidth: Float = 0f
        private var isFlow: Boolean = false

        fun adapter(adapter: BaseDataBindingAdapter<T, VB>): Builder<T, VB> {
            this.adapter = adapter
            return this
        }


        fun nestedScrollingEnabled(isNestedScrollingEnabled: Boolean): Builder<T, VB> {
            this.isNestedScrollingEnabled = isNestedScrollingEnabled
            return this
        }

        fun hasFixedSize(hasFixedSize: Boolean): Builder<T, VB> {
            this.hasFixedSize = hasFixedSize
            return this
        }

        fun spanCount(spanCount: Int): Builder<T, VB> {
            this.spanCount = spanCount
            return this
        }

        fun isHorizontal(isHorizontal: Boolean): Builder<T, VB> {
            this.isHorizontal = isHorizontal
            return this
        }

        fun dividerColor(dividerColor: Int): Builder<T, VB> {
            this.dividerColor = dividerColor
            return this
        }

        fun dividerWidth(dividerWidth: Float): Builder<T, VB> {
            this.dividerWidth = dividerWidth
            return this
        }

        fun isFlow(isFlow: Boolean): Builder<T, VB> {
            this.isFlow = isFlow
            return this
        }

        fun build() = RecyclerViewConfig(
            adapter,
            isNestedScrollingEnabled,
            hasFixedSize,
            spanCount,
            isHorizontal,
            dividerColor,
            dividerWidth,
            isFlow
        )
    }

}