package com.powercore.hydrahome.ui.fragment.home

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdf.base.base.BaseDbVmFragment
import com.fdf.base.ext.toast
import com.fdf.base.startActivity
import com.fdf.base.utils.CacheUtil
import com.gk.net.utils.MoshiUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.databinding.FragmentHomeBinding
import com.powercore.hydrahome.net.entity.rsp.ChargingBean
import com.powercore.hydrahome.net.entity.rsp.HomeListBean
import com.powercore.hydrahome.ui.activity.family.add.NewHouseholdActivity
import com.powercore.hydrahome.ui.activity.family.setting.FamilySettingsActivity
import com.powercore.hydrahome.ui.adapter.ChargePointAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.fdf.base.utils.startActivity
import com.gk.net.utils.toJson
import com.powercore.hydrahome.ext.hiddenLoading
import com.powercore.hydrahome.net.entity.rsp.HomeChargeBox
import com.powercore.hydrahome.ui.activity.bluetooth.BluetoothConnectDeviceActivity
import com.powercore.hydrahome.ui.activity.bluetooth.ChargeWhiteListEditActivity
import com.powercore.hydrahome.ui.activity.charge.ChargingActivity
import com.powercore.hydrahome.ui.activity.chargingprofile.ChargingProfileActivity
import com.powercore.hydrahome.ui.activity.main.MainViewModel
import com.powercore.hydrahome.utils.BlufiUtils
import com.powercore.hydrahome.widget.*

class HomeFragment : BaseDbVmFragment<FragmentHomeBinding, MainViewModel>() {
    private val mHomeList = arrayListOf<HomeListBean>()

    //家庭选择弹窗
    private var mBaseFamilySelectorPop: BasePopupView? = null

    private var mNoHomePop: BasePopupView? = null
    var adapter: ChargePointAdapter? = null

    //家庭选择弹窗ContentView
    private val mFamilySelectorPop by lazy {
        PopFamilySelector(requireContext()).apply {
            setHomeClickCallBack(object : PopFamilySelector.HomeClickCallBack {
                override fun onClickItem(homePk: String,homeName:String) {
                    mViewModel.currFamilyKey.value = homePk
                    mViewModel.currFamilyName.value =
                        mViewModel.homeListLiveData.value!!.filter { it.checked }[0].householdName

                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(200)
                        mBaseFamilySelectorPop?.dismiss()
                        CacheUtil.save(Constant.LAST_HOME_PK, homePk)
                        CacheUtil.save(Constant.LAST_HOME_NANE, homeName)
                    }
                }

                override fun onClickAdd() {
                    mBaseFamilySelectorPop?.dismiss()
                    startActivity<NewHouseholdActivity>()
                }

                override fun onClickSetting() {
                    mBaseFamilySelectorPop?.dismiss()
                    startActivity<FamilySettingsActivity>(
                        Pair(
                            "homeList",
                            mViewModel.homeListLiveData.value!!.toJson()
                        )
                    )

                }

                override fun onChargingProfileSettings() {
                    startActivity<ChargingProfileActivity>()
                }
            })
        }
    }

    override fun init() {
        mViewBind.vm = mViewModel
        initRv()
    }

    override fun initClick() {
        mViewBind.apply {
            clFamilySelector.setOnClickListener {
                mViewModel.getHomeList(isShowHomeSelector = true)
            }

            //点击添加
            addPoint.setOnClickListener {
                if (mViewModel.homeListLiveData.value!!.isEmpty()) {
                    "Don't have home!".toast()
                    return@setOnClickListener
                }
                startActivity(Intent(activity, BluetoothConnectDeviceActivity::class.java))
            }
        }

    }

    override fun loadData() {
        mViewModel.getHomeList()
        /*        mViewModel.validBle("hydra_1005", {

                }, {

                    hiddenLoading()
                })*/
    }

    override fun initObserver() {
        val owner = this
        mViewModel.apply {
            homeListLiveData.observe(owner) {
                if (it.isEmpty()) {
                    showNoFamilyTipsPop()
                }
                mHomeList.clear()
                mHomeList.addAll(it)
                //默认加载home列表中第一个home的充电桩
                if (mHomeList.isNotEmpty()) {
                    val lastHomePk = CacheUtil.getString(Constant.LAST_HOME_PK)
                    if (mHomeList.any { it.hydraHomeHouseholdPk == lastHomePk }) {
                        val home = mHomeList.first { it.hydraHomeHouseholdPk == lastHomePk }
                        mViewModel.currFamilyKey.value = home.hydraHomeHouseholdPk
                        mViewModel.currFamilyName.value = home.householdName
                        home.checked = true
                        getHomeChargeBox(home.hydraHomeHouseholdPk ?: "")
                    } else {
                        CacheUtil.save(
                            Constant.LAST_HOME_PK,
                            mHomeList[0].hydraHomeHouseholdPk ?: ""
                        )
                        CacheUtil.save(
                            Constant.LAST_HOME_NANE,
                            mHomeList[0].householdName ?: ""
                        )
                        val homePks = mutableListOf<String>().apply {
                            add(mHomeList[0].hydraHomeHouseholdPk ?: "")
                        }
                        LiveEventBus.get<String>(LiveDataBusKeys.HOME_STATS_CHECKED)
                            .post(MoshiUtils.toJson(homePks))
                        LiveEventBus.get<String>(LiveDataBusKeys.HOME_RECORD_CHECKED).post(
                            MoshiUtils.toJson(homePks)
                        )

                        mViewModel.currFamilyKey.value = mHomeList[0].hydraHomeHouseholdPk
                        mViewModel.currFamilyName.value = mHomeList[0].householdName
                        mHomeList[0].checked = true
                        getHomeChargeBox(mHomeList[0].hydraHomeHouseholdPk ?: "")
                    }
                    mFamilySelectorPop.setData(mHomeList)
                }

            }
            chargeBoxList.observe(owner) {
                adapter!!.setList(it)

            }

            showHomeSelector.observe(owner, {
                if (it) {
                    mBaseFamilySelectorPop = XPopup.Builder(requireContext())
                        .atView(mViewBind.clFamilySelector)
                        .popupAnimation(PopupAnimation.NoAnimation)
                        .asCustom(mFamilySelectorPop)
                        .show()
                    mFamilySelectorPop.setData(mHomeList)
                }
            })
        }

    }

    /**
     * TODO 显示没有家庭提示弹窗
     *
     */
    private fun showNoFamilyTipsPop() {
        if (mNoHomePop == null)
            mNoHomePop = XPopup.Builder(requireContext())
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(PopNoHaveFamily(requireContext()).apply {
                    setClickCallBack(object : PopNoHaveFamily.ClickCallBack {
                        override fun clickGo() {
                            startActivity<NewHouseholdActivity>()
                        }
                    })
                })
                .show()
        else
            mNoHomePop?.show()
    }

    /**
     * TODO 获取家庭充电桩
     *
     * @param pk homepk
     */
    private fun getHomeChargeBox(pk: String) {
        mViewModel.apply {
            homeChargeBoxParams.value?.apply {
                hydraHomeHouseholdPk = pk
            }
            getHomeChargeBox()
        }
    }

    private fun initRv() {
        adapter = ChargePointAdapter()
        mViewBind.rvChargePoint.layoutManager = LinearLayoutManager(activity)
        mViewBind.rvChargePoint.adapter = adapter

        adapter!!.setOnClickCallBack(object : ChargePointAdapter.OnClickCallBack {
            override fun click(position: Int, homeChargeBox: HomeChargeBox?) {
                showChargeTypePicker(homeChargeBox!!)
            }

            override fun clickItem(position: Int, chargingBean: HomeChargeBox?) {
                if (chargingBean!!.connectorStatus.equals("Charging")) {
                    startActivity(Intent(activity, ChargingActivity::class.java))
                }
            }

            override fun clickOptions(position: Int, chargingBean: HomeChargeBox?) {
                XPopup.Builder(requireContext()).asCustom(
                    PopOptionsPicker(requireContext(), chargingBean!!).apply {
                        setCallBack(connectToWifiBlock = {
                            showChargeTypePicker(it)
                        }, deleteChargePointBlock = {
                            showDeleteChargePointPicker(it)
                        }, rebootChargeWifiBlock = {
                            showRebootChargePointPicker(it)
                        })

                    }).show()
            }

            override fun clickDetele(position: Int, chargingBean: HomeChargeBox?) {
                showDeleteChargePointPicker(chargingBean!!)
            }

        })
    }

    fun showDeleteChargePointPicker(data: HomeChargeBox) {
        XPopup.Builder(requireContext())
            .autoFocusEditText(false)
            .asCustom(PopDeleteChargeBoxPicker(requireContext()).apply {
                setCallBack(data.hydraHomeHouseholdChargeBoxPk, okBlock = {
                    mViewModel.deleteChargeBox(it)
                })
            })
            .show()
    }


    fun showRebootChargePointPicker(data: HomeChargeBox) {
        XPopup.Builder(requireContext())
            .autoFocusEditText(false)
            .asCustom(PopRebootChargeBoxPicker(requireContext()).apply {
                setCallBack(data.hydraHomeHouseholdChargeBoxPk, okBlock = {
                    mViewModel.rebootChargeBox(it)
                })
            })
            .show()
    }


    private fun showChargeTypePicker(data: HomeChargeBox) {
        XPopup.Builder(requireContext())
            .autoFocusEditText(false)
            .asCustom(PopChargeTypePicker(requireContext()).apply {
                setViewModel(mViewModel)
                setData(data)
            })
            .show()
    }
}