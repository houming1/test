package com.powercore.hydrahome.widget

import android.content.Context
import android.graphics.Color
import androidx.databinding.DataBindingUtil
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.bind.RecyclerViewConfig
import com.fdf.base.utils.CacheUtil
import com.gk.net.utils.MoshiUtils
import com.lxj.xpopup.core.AttachPopupView
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.R
import com.powercore.hydrahome.BR
import com.powercore.hydrahome.bean.ConnectWifiBean
import com.powercore.hydrahome.databinding.ItemWifiNamaeBinding
import com.powercore.hydrahome.databinding.PopWifiNameBinding
import me.jessyan.autosize.utils.AutoSizeUtils
import me.jessyan.autosize.utils.ScreenUtils

/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2022/01/19
 *  desc   :
 *
 */
class PopWifiName(context: Context) : AttachPopupView(context) {

    private lateinit var bind: PopWifiNameBinding
    private var mCacheWifiNameList: MutableList<ConnectWifiBean> = mutableListOf()

    private var mCallBck: CallBack? = null

    private val wifiRvConfig by lazy {
        RecyclerViewConfig.Builder<ConnectWifiBean, ItemWifiNamaeBinding>()
            .adapter(BaseDataBindingAdapter(R.layout.item_wifi_namae, mCacheWifiNameList, BR.data))
            .dividerColor(Color.GRAY)
            .dividerWidth(.5f)
            .build()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_wifi_name
    }

    override fun onCreate() {
        super.onCreate()

        bind = DataBindingUtil.bind(popupImplView)!!
        bind.rvConfig = wifiRvConfig

        val wifiNameJson = CacheUtil.getString(Constant.WIFI_NAME_KEY)
        if (!wifiNameJson.isNullOrBlank()) {
            val wifiBeanList = MoshiUtils.listFromJson<ConnectWifiBean>(wifiNameJson)
            mCacheWifiNameList.addAll(wifiBeanList)
        }

        wifiRvConfig.getAdapter().setList(mCacheWifiNameList)

        wifiRvConfig.getAdapter().setOnItemClickListener { adapter, view, position ->
            val data = adapter.data[position] as ConnectWifiBean
            mCallBck?.onClickItem(data.ssid)
            dismiss()
        }
    }

    override fun getMaxHeight(): Int {
        return ScreenUtils.getScreenSize(context)[1] / 3
    }

    override fun getPopupWidth(): Int {
        return ScreenUtils.getScreenSize(context)[0] - AutoSizeUtils.dp2px(context, 40f)
    }

    fun setCallBack(callBack: CallBack) {
        this.mCallBck = callBack
    }

    interface CallBack {
        fun onClickItem(name: String)
    }
}