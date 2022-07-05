package com.powercore.hydrahome.ui.activity.family.add

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.fdf.base.app.appContext
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.bind.ViewBindAdapter.afterTextChanged
import com.fdf.base.ext.getDrwable
import com.fdf.base.ext.getColor
import com.fdf.base.ext.toast
import com.fdf.base.getString
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityNewHouseholdBinding
import com.powercore.hydrahome.net.entity.rsp.HomeSettingRsp
import com.powercore.hydrahome.widget.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewHouseholdActivity() : BaseDbVmActivity<ActivityNewHouseholdBinding, NewHouseholdViewModel>(
    title = getString(R.string.new_household),
    titleBarColor = getColor(R.color.act_bg_color),
), Parcelable {

    private var mConfirmEnable = false


    constructor(parcel: Parcel) : this() {
        mConfirmEnable = parcel.readByte() != 0.toByte()
    }

    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.act_bg_color)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel

        setBtnConfirmState()
    }

    override fun initClick() {
        mViewBind.apply {
            //点击添加按钮
            rclAddHome.setOnClickListener {
                XPopup.Builder(this@NewHouseholdActivity)
                    .asCustom(
                        PopAddHomeMemberEmail(this@NewHouseholdActivity)
                            .apply {
                                setCallBack(object : PopAddHomeMemberEmail.CallBack {
                                    override fun add(memberEmail: String) {
                                        mViewModel.apply {
                                            verifyEmail.value = memberEmail
                                            homeVerify()
                                        }
                                    }
                                })
                            })
                    .show()
            }
            //提交
            btnConfirm.setOnClickListener {
                if (!btnConfirm.isEnabled) return@setOnClickListener
                if (mViewModel.type == 1) {
                    mViewModel.apply {
                        updateHousehold()
                    }
                } else {
                    // mViewModel.getHomePk {  }
                    mViewModel.addHome("")
                }
            }
            etPower.doAfterTextChanged {
                tvKwh.text = if (it.toString().isNotEmpty()) "Amps" else "--Amps"
            }
            etDayRateTariff.doAfterTextChanged {
                tvPKwh.text = if (it.toString().isNotBlank()) "p" else "--p"
            }
            etNightRateTariff.doAfterTextChanged {
                tvPKwh2.text = if (it.toString().isNotBlank()) "p" else "--p"
            }
            etRandomisedDelay.doAfterTextChanged {
                tvS.text = if (it.toString().isNotBlank()) "s" else "--s"
            }
            clMaxPower.setOnClickListener {
                mViewModel.apply {
                    if (type==1&& !getHomeLiveData.value!!.isCreator()){
                        "No permission".toast()
                        return@setOnClickListener
                    }
                }
                XPopup.Builder(this@NewHouseholdActivity).asCustom(
                    PopHomeMaxPower(this@NewHouseholdActivity).apply {
                        setEndInputBlock(mViewModel.addHomeParams.value!!.householdMaxLoad) {
                            if (mViewModel.type == 0) {
                                mConfirmEnable = true
                            }
                            mViewBind.etPower.text = it
                            setBtnConfirmState()
                            mViewModel.apply {
                                if (type == 1) {
                                    updateSetting()
                                }
                            }
                        }
                    }).show()
            }
            tvHomeName.setOnClickListener {
                mViewModel.apply {
                    if (type==1&& !getHomeLiveData.value!!.isCreator()){
                        "No permission".toast()
                        return@setOnClickListener
                    }
                }
                XPopup.Builder(this@NewHouseholdActivity).asCustom(
                    PopAddHomeFamilyName(this@NewHouseholdActivity).apply {
                        setViewModel(mViewModel)
                        setEndInputBlock {
                            mConfirmEnable = true
                            mViewBind.tvHomeName.text = it
                            setBtnConfirmState()
                            mViewModel.apply {
                                if (type == 1) {
                                    update {}
                                }
                            }
                        }
                    }).show()
            }
            note.afterTextChanged {
//                if (mViewModel.type == 1) {
//                    mConfirmEnable = true
//                }
            }
            getTitleBar()?.setOnTitleBarListener(object : OnTitleBarListener {
                override fun onLeftClick(view: View?) {
                    onBackPressed()
                }

                override fun onTitleClick(view: View?) {
                }

                override fun onRightClick(view: View?) {
                    var popupView =
                        XPopup.Builder(this@NewHouseholdActivity)
                            .asConfirm(
                                "Tips", "Are you sure you want to delete the current family?",
                                "Cancel", "Confirm",
                                {
                                    mViewModel.delHome()
                                }, null, false
                            )
                    popupView.cancelTextView.setTextColor(0x8193AE)
                    popupView.confirmTextView.setTextColor(0x15CD80)
                    popupView.show()

                }
            })
            clRandomisedDelay.setOnClickListener {
                XPopup.Builder(this@NewHouseholdActivity).asCustom(
                    PopRandomisedDelay(this@NewHouseholdActivity).apply {
                        setCallBack(mViewModel.addHomeParams.value!!.delay) {

                            mViewBind.etRandomisedDelay.text = it
                            mConfirmEnable = true
                            setBtnConfirmState()

                        }
                    }).show()
            }
            clNightRateTariff.setOnClickListener {
                XPopup.Builder(this@NewHouseholdActivity).asCustom(
                    PopNightRateTariff(this@NewHouseholdActivity).apply {
                        setCallBack(mViewModel.addHomeParams.value!!.nightRate,
                            mViewModel.addHomeParams.value!!.nightStartTime,
                            mViewModel.addHomeParams.value!!.nightStopTime, okBlock = { p, time ->
                                etNightRateTariff.text = p
                                var times = time.split("-")
                                mViewModel.addHomeParams.value.apply {
                                    this!!.nightStartTime = times[0].trim()
                                    this!!.nightStopTime = times[1].trim()
                                }
                                mConfirmEnable = true
                                setBtnConfirmState()
                            })
                    }).show()
            }
            clDayRateTariff.setOnClickListener {
                XPopup.Builder(this@NewHouseholdActivity).asCustom(
                    PopDayRateTariff(this@NewHouseholdActivity).apply {
                        setCallBack(mViewModel.addHomeParams.value!!.dayRate,
                            mViewModel.addHomeParams.value!!.dayStartTime,
                            mViewModel.addHomeParams.value!!.dayStopTime, okBlock = { p, time ->
                                etDayRateTariff.text = p
                                var times = time.split("-")
                                mViewModel.addHomeParams.value.apply {
                                    this!!.dayStartTime = times[0].trim()
                                    this!!.dayStopTime = times[1].trim()
                                }
                                mConfirmEnable = true
                                setBtnConfirmState()
                            }
                        )
                    }).show()
            }
        }
        mViewModel.apply {
            memberRvConfig.getAdapter().apply {
                addChildClickViewIds(R.id.iv_del)
                setOnItemChildClickListener { adapter, view, position ->
                    //  if (type == 0) {
                    addMemberLiveData.value!!.removeAt(position)
                    adapter.removeAt(position)
                    adapter.notifyDataSetChanged()
//                    } else {
//                        val data = adapter.data[position] as HomeSettingRsp.VerifyMemberRsp
//                        if (data.isCreator()) {
//                            val popupView =
//                                XPopup.Builder(this@NewHouseholdActivity)
//                                    .asConfirm(
//                                        "Tips", "Are you sure to delete the member?",
//                                        "Cancel", "Confirm",
//                                        {
//                                            val data =
//                                                adapter.data[position] as HomeSettingRsp.VerifyMemberRsp
//                                            delete(data.hydraHomeUserPk ?: "") {
//
//                                            }
//
//                                        }, null, false
//                                    )
//                            popupView.cancelTextView.setTextColor(0x8193AE)
//                            popupView.confirmTextView.setTextColor(0x15CD80)
//                            popupView.show()
//                        } else if (data.isMe()) {
//                            val popupView =
//                                XPopup.Builder(this@NewHouseholdActivity)
//                                    .asConfirm(
//                                        "Tips", "Are you sure you want to leave the family?",
//                                        "Cancel", "Confirm",
//                                        {
//                                            val data =
//                                                adapter.data[position] as HomeSettingRsp.VerifyMemberRsp
//                                            delete(data.hydraHomeUserPk ?: "") {
//                                                LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME)
//                                                    .post(true)
//                                                finish()
//                                            }
//
//                                        }, null, false
//                                    )
//                            popupView.cancelTextView.setTextColor(0x8193AE)
//                            popupView.confirmTextView.setTextColor(0x15CD80)
//                            popupView.show()
//                        }
//                    }
                }
            }
        }
    }

    override fun loadData() {
        mViewModel.apply {
            homePk = intent.getStringExtra("homePk") ?: ""
            type = intent.getIntExtra("type", mViewModel.type)!!
            if (type == 1) {
                getTitleBar()?.title = "Edit Household"
                getTitleBar()?.rightIcon = getDrwable(R.mipmap.ic_charge_schedule_del)
                //getHomePk {  }
                getHomeSetting()
            } else {
                getTitleBar()?.title = "New Household"
                getTitleBar()?.rightIcon = null
            }

        }


    }

    override fun initObserver() {
        mViewModel.apply {
            homeLiveData.observe(this@NewHouseholdActivity, Observer {
                mViewBind.apply {
                    tvHomeName.text = it.householdName
                    etPower.text = it.householdMaxLoad
                    etDayRateTariff.text = it.dayRate
                    etNightRateTariff.text = it.nightRate
                    etRandomisedDelay.text = it.delay
                    mViewModel.addHomeParams.value.apply {
                        this!!.dayStartTime = it.dayStartTime
                        this!!.dayStopTime = it.dayStopTime
                        this!!.nightStartTime = it.nightStartTime
                        this!!.nightStopTime = it.nightStopTime
                    }
                    note.setText(it.note)
                    setBtnConfirmState()
                }

                lifecycleScope.launch {
                    delay(500)
                    mViewBind.note.afterTextChanged {
                        if (mViewModel.type == 1) {
                            mConfirmEnable = true
                            setBtnConfirmState()
                        }
                    }
                }
            })

            isUpdateSuccess.observe(this@NewHouseholdActivity) {
                if (it) {
                    var popupView =
                        XPopup.Builder(this@NewHouseholdActivity)
                            .asConfirm(
                                "Tips",
                                "After editing, do you want to go back to the previous page?",
                                "Cancel",
                                "Confirm",
                                {
                                    onBackPressed()

                                },
                                null,
                                false
                            )
                    popupView.cancelTextView.setTextColor(0x8193AE)
                    popupView.confirmTextView.setTextColor(0x15CD80)
                    popupView.show()
                }
            }
        }

        LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME).observe(this, {
            this.finish()
        })

        mViewModel.delHomeResult.observe(this, {
            if (it) {
                LiveEventBus.get<Boolean>(LiveDataBusKeys.UPDATE_HOME_NAME).post(true)
                this.finish()
            }
        })
    }

    private fun setBtnConfirmState() {
        mViewModel.apply {
            mViewBind.apply {
                btnConfirm.isEnabled = if (mViewModel.type == 1) {
                    addHomeParams.value!!.householdName.isNotBlank() && mConfirmEnable
                } else {
                    addHomeParams.value!!.householdName.isNotBlank() && addHomeParams.value!!.householdMaxLoad.isNotBlank() && mConfirmEnable && addHomeParams.value!!.dayRate.isNotBlank() && addHomeParams.value!!.nightRate.isNotBlank() && addHomeParams.value!!.delay.isNotBlank()
                }
                btnConfirm.helper.backgroundColorNormal =
                    if (mViewBind.btnConfirm.isEnabled)
                        ContextCompat.getColor(appContext, R.color.theme_color)
                    else
                        ContextCompat.getColor(appContext, R.color.common_grey)
            }
        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (mConfirmEnable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewHouseholdActivity> {
        override fun createFromParcel(parcel: Parcel): NewHouseholdActivity {
            return NewHouseholdActivity(parcel)
        }

        override fun newArray(size: Int): Array<NewHouseholdActivity?> {
            return arrayOfNulls(size)
        }
    }
}