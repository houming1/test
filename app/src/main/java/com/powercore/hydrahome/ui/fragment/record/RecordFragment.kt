package com.powercore.hydrahome.ui.fragment.record

import android.util.TypedValue
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fdf.base.base.BaseDbVmFragment
import com.fdf.base.ext.getColor
import com.fdf.base.ext.gone
import com.fdf.base.ext.visible
import com.fdf.base.utils.CacheUtil
import com.fdf.base.utils.startActivity
import com.gk.net.utils.MoshiUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.FragmentRecordBinding
import com.powercore.hydrahome.ext.nowDate
import com.powercore.hydrahome.ext.nowTime
import com.powercore.hydrahome.ui.activity.chargingrecords.ChargingRecordsActivity
import com.powercore.hydrahome.widget.PopDateTimePicker
import com.powercore.hydrahome.widget.PopHomePicker
import com.powercore.hydrahome.widget.PopSelectDateRangePicker

class RecordFragment : BaseDbVmFragment<FragmentRecordBinding, RecordViewModel>() {
    private var statsFragments: ArrayList<Fragment> = arrayListOf()
    private var isPersonal = true
    private var mHomePicker: BasePopupView? = null
    var type = "person"
    var householdName=CacheUtil.getString(Constant.LAST_HOME_NANE) ?: ""
    var hydraHomeHouseholdPk = CacheUtil.getString(Constant.LAST_HOME_PK) ?: ""


    private val mHomePickerView: PopHomePicker by lazy {
        PopHomePicker(requireContext())
    }

    override fun init() {

        statsFragments.add(PersonalRecordFragment())
        statsFragments.add(HouseholdRecordFragment())

        mViewBind.viewPager.apply {
            isSaveEnabled = true
            isUserInputEnabled = false
            adapter =
                object : FragmentStateAdapter(this@RecordFragment.childFragmentManager, lifecycle) {

                    override fun getItemCount() = statsFragments.size

                    override fun createFragment(position: Int): Fragment {
                        return statsFragments[position]
                    }
                }
        }
    }

    override fun initClick() {
        mViewBind.apply {
            clPersonal.setOnClickListener {
                if (mViewBind.viewPager.currentItem != 0) {
                    isPersonal = true
                    changeTableState()
                    mViewBind.viewPager.currentItem = 0
                    type = "person"
                    hydraHomeHouseholdPk = CacheUtil.getString(Constant.LAST_HOME_PK) ?: ""
                    householdName=CacheUtil.getString(Constant.LAST_HOME_NANE) ?: ""
                }
            }
            clFamily.setOnClickListener {
                type = "household"
                if (mViewBind.viewPager.currentItem != 1) {
                    isPersonal = false
                    changeTableState()
                    mViewBind.viewPager.currentItem = 1
                    hydraHomeHouseholdPk = CacheUtil.getString(Constant.LAST_HOME_PK) ?: ""
                    householdName=CacheUtil.getString(Constant.LAST_HOME_NANE) ?: ""

                    //弹出家庭选项
//                    if (mHomePicker == null) {
//                        mHomePicker = XPopup.Builder(requireContext())
//                            .dismissOnTouchOutside(false)
//                            .dismissOnBackPressed(false)
//                            .atView(mViewBind.clPersonal)
//                            .popupAnimation(PopupAnimation.NoAnimation)
//                            .asCustom(mHomePickerView.apply {
//                                setCallBack {
//                                    LiveEventBus.get<String>(LiveDataBusKeys.HOME_STATS_CHECKED).post(MoshiUtils.toJson(it))
//                                }
//                            })
//                            .show()
//                    } else
//                        mHomePicker!!.show()
                } else {
                    //弹出家庭选项
                    if (mHomePicker == null) {
                        mHomePicker = XPopup.Builder(requireContext())
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false)
                            .atView(mViewBind.clPersonal)
                            .popupAnimation(PopupAnimation.NoAnimation)
                            .asCustom(mHomePickerView.apply {
                             setCallBack { homePks, homeName ->
                                 hydraHomeHouseholdPk = homePks[0]
                                 householdName=homeName
                                 LiveEventBus.get<String>(LiveDataBusKeys.HOME_STATS_CHECKED)
                                     .post(
                                         MoshiUtils.toJson(it)
                                     )
                             }
                            })
                            .show()
                    } else
                        mHomePicker!!.show()
                }
            }
        }
        mViewBind.download.setOnClickListener {
            showDateTimerPickerView()
        }
        mViewBind.filter.setOnClickListener {
            startActivity<ChargingRecordsActivity>(
                Pair("type", type),
                Pair("hydraHomeHouseholdPk",hydraHomeHouseholdPk),
                Pair("householdName",householdName)
            )
        }
    }

    override fun loadData() {
        mViewModel.getHomeList()
    }

    override fun initObserver() {
        mViewModel.homeListLiveData.observe(this, {
//            it.forEach {home->
//                home.checked = true
//            }
            val lastHomePk = CacheUtil.getString(Constant.LAST_HOME_PK)
            if (it.isNotEmpty()) it.first { it.hydraHomeHouseholdPk == lastHomePk }.checked = true

            mHomePickerView.setData(it)
            val checkedPks =
                it.map { homeListBean -> homeListBean.hydraHomeHouseholdPk } as MutableList<String>
            LiveEventBus.get<String>(LiveDataBusKeys.HOME_STATS_CHECKED)
                .post(MoshiUtils.toJson(checkedPks))
        })

        LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME).observe(this, {
            mViewModel.getHomeList()
        })
    }

    private fun changeTableState() {
        mViewBind.apply {
            tvPerson.setTextColor(if (isPersonal) getColor(R.color.theme_color) else getColor(R.color.color_33))
            tvPerson.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isPersonal) 18f else 14f)
            if (isPersonal) linePerson.visible() else linePerson.gone()
            tvFamily.setTextColor(if (!isPersonal) getColor(R.color.theme_color) else getColor(R.color.color_33))
            tvFamily.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (!isPersonal) 18f else 14f)
            if (!isPersonal) lineFamily.visible() else lineFamily.gone()
        }
    }

    /* *
  * 显示日期时间选择弹窗*/

    private fun showDateTimerPickerView() {
        val contentView = PopSelectDateRangePicker(requireActivity()).apply {
            setCallBack(
                PopSelectDateRangePicker.DateTimeType.START,
                System.currentTimeMillis().nowDate(),
                System.currentTimeMillis().nowTime("HH:mm"),
                confrimBlock = { startTime, endTime ->
                    mViewModel.downloadHydraHomeTransaction(startTime, endTime)
                },
            )
        }
        XPopup.Builder(context)
            .asCustom(contentView)
            .show()
    }
}