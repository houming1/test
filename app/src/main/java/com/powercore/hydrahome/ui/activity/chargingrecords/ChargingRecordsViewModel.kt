package com.powercore.hydrahome.ui.activity.chargingrecords

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseDataBindingAdapter
import com.fdf.base.base.BaseViewModel
import com.fdf.base.bind.RecyclerViewConfig
import com.fdf.base.ext.request
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.BR
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ItemChargingProfileBinding
import com.powercore.hydrahome.databinding.ItemChargingRecordsBinding
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.TransactionListVo
import com.powercore.hydrahome.net.entity.rsp.ChargingRecordRsp
import com.powercore.hydrahome.net.entity.rsp.TransactionListBean

class ChargingRecordsViewModel : BaseViewModel() {

    private val chargingRecordsAdapter by lazy {
        BaseDataBindingAdapter<TransactionListBean, ItemChargingRecordsBinding>(
            R.layout.item_charging_records,
            arrayListOf(),
            BR.data,
        )
    }
    val chargingRecordsRvConfig by lazy {
        RecyclerViewConfig.Builder<TransactionListBean, ItemChargingRecordsBinding>()
            .adapter(chargingRecordsAdapter)
            .hasFixedSize(false)
            .dividerWidth(10f)
            .build()
    }
    val transactionListVo = TransactionListVo()
    val statsPersonListLiveData = MutableLiveData<ChargingRecordRsp>()
    var isLoadMore = true
    fun getTransactionList() {
        request({ apiService.getTransactionList(transactionListVo.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->

                    if (transactionListVo.pageNum == 1) {
                        chargingRecordsAdapter.setList(data!!.transactionList)
                    } else {
                        chargingRecordsAdapter.addData(data!!.transactionList)
                    }
                    isLoadMore = (data?.transactionList != null && data.transactionList.size != 0)
                    statsPersonListLiveData.value = data
                    chargingRecordsAdapter.isUseEmpty = chargingRecordsAdapter.data.isEmpty()
                }
            })
    }
}