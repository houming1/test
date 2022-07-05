package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdf.base.app.screenHeight
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.ext.log
import com.lxj.xpopup.core.AttachPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.BR
import com.powercore.hydrahome.databinding.ItemPopFamilyBinding
import com.powercore.hydrahome.databinding.PopFamilySelectorBinding
import com.powercore.hydrahome.net.entity.rsp.HomeListBean

import me.jessyan.autosize.utils.AutoSizeUtils

/**
 *    Created by Administrator on 2021/12/14.
 *    Desc : 首页家庭列表弹窗
 */
class PopFamilySelector @JvmOverloads constructor(context: Context) :
    AttachPopupView(context) {

    private var mHomeClickCallBack: HomeClickCallBack? = null

    private lateinit var bind: PopFamilySelectorBinding
    private val mAdapter: BaseDataBindingAdapter<HomeListBean, ItemPopFamilyBinding> by lazy {
        BaseDataBindingAdapter(R.layout.item_pop_family, arrayListOf(), BR.item)
    }

    override fun getImplLayoutId() = R.layout.pop_family_selector

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!

        mAdapter.setOnItemClickListener { adapter, _, position ->

//            mHomeClickCallBack?.onClickItem(vm?.homeListLiveData?.value!![position].homePk ?: "")
//            vm?.homeListLiveData?.value!!.forEachIndexed { index, _ ->
//                vm?.homeListLiveData?.value!![position].checked = index == position
//            }

            val data: MutableList<HomeListBean> = adapter.data as MutableList<HomeListBean>
            data.forEachIndexed { index, any ->
                data[index].checked = index == position
                adapter.notifyItemChanged(index)
            }
            mHomeClickCallBack?.onClickItem(data[position].hydraHomeHouseholdPk ?: "",data[position].householdName?: "")
        }

        bind.apply {
            rvFamily.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
            //点击添加
            tvAddFamily.setOnClickListener {
                mHomeClickCallBack?.onClickAdd()
                dismiss()
            }
            //点击设置
            tvFamilySet.setOnClickListener {
                mHomeClickCallBack?.onClickSetting()
                dismiss()
            }
            tvChargingProfileSettings.setOnClickListener {
                mHomeClickCallBack!!.onChargingProfileSettings()
                dismiss()
            }

        }
    }

    fun setData(homeList: MutableList<HomeListBean>) {
        mAdapter.setList(homeList)

    }

    override fun onShow() {
        super.onShow()

        val homeList = mAdapter.data
        "onShow  homeList=${homeList.size}".log()
        if (homeList.size >= 5) {
            val lp = bind.rvFamily.layoutParams
            lp.height = AutoSizeUtils.dp2px(context, 45 * 5f)
            bind.rvFamily.layoutParams = lp

            mAdapter.notifyDataSetChanged()
        }
    }


    interface HomeClickCallBack {
        /**
         * TODO 点击红home item
         *
         * @param homePk homePk
         */
        fun onClickItem(homePk: String,homeName:String)

        /**
         * TODO 点击点击家庭
         *
         */
        fun onClickAdd()

        /**
         * TODO 点击家庭设置
         *
         */
        fun onClickSetting()

        fun onChargingProfileSettings()
    }

    fun setHomeClickCallBack(homeClickCallBack: HomeClickCallBack) {
        this.mHomeClickCallBack = homeClickCallBack
    }
}