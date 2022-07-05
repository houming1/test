package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.bind.RecyclerViewConfig
import com.fdf.base.ext.log
import com.fdf.base.utils.CacheUtil
import com.lxj.xpopup.core.AttachPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ItemStatsHomePickerBinding
import com.powercore.hydrahome.databinding.PopStatsHomePickerBinding
import com.powercore.hydrahome.net.entity.rsp.HomeListBean
import com.powercore.hydrahome.BR
import com.powercore.hydrahome.Constant
import me.jessyan.autosize.utils.AutoSizeUtils

class PopHomePicker(context: Context) : AttachPopupView(context) {

    private var bind: PopStatsHomePickerBinding? = null
    private var mconfirmBlock: (homePks:MutableList<String>,homeName:String) -> Unit = {
            homePks: MutableList<String>, homeName: String -> }

    private val homeAdapterConfig by lazy {
        RecyclerViewConfig.Builder<HomeListBean, ItemStatsHomePickerBinding>()
            .adapter(
                BaseDataBindingAdapter(
                    R.layout.item_stats_home_picker,
                    arrayListOf(),
                    BR.item
                )
            )
            .build()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_stats_home_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        bind!!.rvConfig = homeAdapterConfig

        homeAdapterConfig.getAdapter().setOnItemClickListener { adapter, view, position ->
            val data = adapter.data as MutableList<HomeListBean>
            //如果直选中一个 则该选项不能再操作  以保证默认一条数据
            data.forEach {
                it.checked = false
            }
            data[position].checked = true
            adapter.notifyDataSetChanged()

        }

        bind!!.btnConfirm.setOnClickListener {
            val data = homeAdapterConfig.getAdapter().data
            val checkedList = data.filter { it.checked }
            val checkedPks = checkedList.map { it.hydraHomeHouseholdPk } as MutableList<String>
            mconfirmBlock.invoke(checkedPks, checkedList[0].householdName!!)
            dismiss()
        }
        bind!!.btnReset.setOnClickListener {
            dismiss()
        }
    }

    fun setCallBack(confirmBlock:(homePks:MutableList<String>,homeName:String) -> Unit = {
            homePks: MutableList<String>, homeName: String -> }) {
        this.mconfirmBlock = confirmBlock
    }

    fun setData(homeList: MutableList<HomeListBean>) {
        "bind=null?->${bind == null}".log()
        "homeList.size=${homeList.size}".log()
        homeList.forEach {
            if (it.hydraHomeHouseholdPk.equals(CacheUtil.getString(Constant.LAST_HOME_PK))) {
                it.checked = true
            }

        }
        homeAdapterConfig.getAdapter().setList(homeList)
    }

    override fun onShow() {
        super.onShow()
        val homeList = homeAdapterConfig.getAdapter().data
        "onShow  homeList=${homeList.size}".log()
        if (homeList.size >= 5) {
            val lp = bind?.rvHome?.layoutParams
            lp?.height = AutoSizeUtils.dp2px(context, 45 * 5f)
            bind?.rvHome?.layoutParams = lp

            homeAdapterConfig.getAdapter().notifyDataSetChanged()
        }
    }
}