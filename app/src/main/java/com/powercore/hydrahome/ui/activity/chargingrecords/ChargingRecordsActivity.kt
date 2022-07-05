package com.powercore.hydrahome.ui.activity.chargingrecords

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityChargingRecordsBinding
import com.powercore.hydrahome.ext.nowDate
import com.powercore.hydrahome.ext.nowTime
import com.powercore.hydrahome.utils.DateUtil
import com.powercore.hydrahome.widget.PopSelectDateRangePicker

class ChargingRecordsActivity :
    BaseDbVmActivity<ActivityChargingRecordsBinding, ChargingRecordsViewModel>(
        title = "Charging Records",
        titleBarColor = getColor(R.color.white),
    ) {
    var pageNum = 1
    var type = ""
    var hydraHomeHouseholdPk = ""
    var householdName = ""
    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
        type = intent.getStringExtra("type").toString()
        hydraHomeHouseholdPk = intent.getStringExtra("hydraHomeHouseholdPk").toString()
        householdName = intent.getStringExtra("householdName").toString()
        getTitleBar()!!.title = householdName + "Charging Records"
        mViewModel.transactionListVo!!.type = type
        mViewModel.transactionListVo!!.hydraHomeHouseholdPk = hydraHomeHouseholdPk
        mViewModel.transactionListVo!!.stopDate = DateUtil.getNowDay()
        mViewModel.transactionListVo!!.startDate = DateUtil.getBefore(30)
        mViewModel.transactionListVo!!.pageNum = pageNum
        mViewBind.tvDates.text = DateUtil.getBefore(30) + " - " + DateUtil.getNowDay()
        registerRefresh()
    }

    override fun initClick() {
        mViewBind.timeLl.setOnClickListener {
            showDateTimerPickerView();
        }

    }

    override fun loadData() {
        mViewModel.getTransactionList()

    }

    override fun initObserver() {
        mViewModel.statsPersonListLiveData.observe(this, {
            mViewBind.apply {
                refreshLayout.finishLoadMore()
                refreshLayout.finishRefresh()
            }
        })
    }

    private fun registerRefresh() {
        mViewBind.refreshLayout.setOnRefreshListener {
            mViewModel.apply {
                transactionListVo.pageNum = 1
                getTransactionList()
            }

        }
        mViewBind.refreshLayout.setOnLoadMoreListener { refreshLayout ->
            mViewModel.apply {
                if (isLoadMore) {
                    transactionListVo.pageNum += 1
                    getTransactionList()
                } else {
                    refreshLayout.finishLoadMore()
                }
            }
        }
    }


    private fun showDateTimerPickerView() {
        val contentView = PopSelectDateRangePicker(this).apply {
            setCallBack(
                PopSelectDateRangePicker.DateTimeType.START,
                System.currentTimeMillis().nowDate(),
                System.currentTimeMillis().nowTime("HH:mm"),
                confrimBlock = { startTime, endTime ->
                    mViewBind.tvDates.text = "$startTime - $endTime"
                    mViewModel.apply {
                        transactionListVo.pageNum = 1
                        transactionListVo.startDate = startTime
                        transactionListVo.stopDate = endTime
                        getTransactionList()
                    }
                },
            )
        }
        XPopup.Builder(this)
            .asCustom(contentView)
            .show()
    }
}