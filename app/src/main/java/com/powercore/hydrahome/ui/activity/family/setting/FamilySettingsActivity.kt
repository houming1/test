package com.powercore.hydrahome.ui.activity.family.setting

import android.view.View
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.fdf.base.getString
import com.fdf.base.utils.get
import com.gk.net.utils.MoshiUtils
import com.gk.net.utils.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityFamilySettingsBinding
import com.powercore.hydrahome.net.entity.rsp.HomeListBean
import com.powercore.hydrahome.ui.activity.family.add.NewHouseholdActivity
import com.fdf.base.utils.startActivity
import com.hjq.bar.OnTitleBarListener
import com.lxj.xpopup.XPopup

class FamilySettingsActivity : BaseDbVmActivity<ActivityFamilySettingsBinding, FamilySetViewModel>(
    title = getString(R.string.txt_family_settings),
    titleBarColor = getColor(R.color.act_bg_color),

    ) {

    var type = 0;//0是edit 1是confirm
    override fun init() {
        mViewBind.vm = mViewModel
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.act_bg_color)
            .autoDarkModeEnable(true).init()
        mViewBind.apply {
            getTitleBar()!!.setRightTitle("Edit")
        }

        val homeListJson = intent.get("homeList", "")!!
        val type = object : TypeToken<MutableList<HomeListBean>>() {}.type
        val homeList: MutableList<HomeListBean> = MoshiUtils.listFromJson(homeListJson)
        mViewModel.homeListLiveData.value!!.addAll(homeList)

        mViewBind.refreshLayout.apply {
            setEnableLoadMore(false)
            setOnRefreshListener {
                mViewModel.getHomeList()
            }
        }
    }

    override fun initClick() {
        mViewModel.apply {
            homeListRvConfig.getAdapter().addChildClickViewIds(R.id.delete)
            homeListRvConfig.getAdapter().setOnItemClickListener { adapter, view, position ->
                var homeListBean = adapter.data.get(position) as HomeListBean
                startActivity<NewHouseholdActivity>(
                    Pair(
                        "homePk",
                        homeListBean.hydraHomeHouseholdPk.toString()
                    ), Pair("type", 1)
                )


            }
            homeListRvConfig.getAdapter().setOnItemChildClickListener { adapter, view, position ->
                var popupView =
                    XPopup.Builder(this@FamilySettingsActivity)
                        .asConfirm(
                            "Tips", "Are you sure you want to delete the current family?",
                            "Cancel", "Confirm",
                            {
                                var data = adapter.data.get(position) as HomeListBean
                                mViewModel.delHome(data.hydraHomeHouseholdPk!!, position)
                            }, null, false
                        )
                popupView.cancelTextView.setTextColor(0x8193AE)
                popupView.confirmTextView.setTextColor(0x15CD80)
                popupView.show()
            }
        }

        mViewBind.apply {
            getTitleBar()?.setOnTitleBarListener(object : OnTitleBarListener {
                override fun onLeftClick(view: View?) {
                    onBackPressed()
                }

                override fun onTitleClick(view: View?) {
                }

                override fun onRightClick(view: View?) {
                    if (type == 0) {
                        getTitleBar()!!.rightTitle = "Confirm"
                        type = 1
                        mViewModel.homeListLiveData.value!!.forEach {
                            it.isEdit = true
                        }
                    } else {
                        getTitleBar()!!.rightTitle = "Edit"
                        type = 0
                        mViewModel.homeListLiveData.value!!.forEach {
                            it.isEdit = false
                        }

                    }
                    mViewModel.homeListRvConfig.setList(mViewModel.homeListLiveData.value!!)

                }
            })
        }
    }

    override fun loadData() {

    }

    override fun initObserver() {
        mViewModel.apply {
            requestHomeListEnd.observe(this@FamilySettingsActivity, {
                if (it) {
                    mViewBind.refreshLayout.finishRefresh()
                }
            })
        }
        LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME).observe(this, {
            mViewModel.getHomeList()
        })

        mViewModel.delHomeResult.observe(this, {
            mViewModel.homeListLiveData.value!!.removeAt(it)
            mViewModel.homeListRvConfig.setList(mViewModel.homeListLiveData.value!!)

        })
    }
}