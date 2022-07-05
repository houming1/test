package com.powercore.hydrahome.ui.activity.chargingprofile

import android.view.View
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.fdf.base.ext.toast
import com.gk.net.utils.fromJson
import com.gk.net.utils.toJson
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityCreateAprofileBinding
import com.powercore.hydrahome.net.entity.rsp.ProfileItemRsp
import com.powercore.hydrahome.net.entity.rsp.WeekRsp
import com.powercore.hydrahome.widget.PopProfileTimePicker

class CreateAProfileActivity :
    BaseDbVmActivity<ActivityCreateAprofileBinding, CreateAProfileViewModel>(
        title = "Create A Profile",
        titleBarColor = getColor(R.color.white)
    ) {
    var type = 0;
    var data: ProfileItemRsp? = null

    var weeks = arrayListOf<WeekRsp>()
    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
        var d = intent.getStringExtra("data") ?: ""
        if (!d.isNullOrBlank()){
            data = d.fromJson()
        }
        type = intent.getIntExtra("type", type)!!
        initView()
    }

    fun initView() {
        initWeek()
        if (type == 0) {

        } else {
            mViewBind.apply {
                confitm.visibility = View.GONE
                getTitleBar()!!.rightTitle = "Confirm"
                getTitleBar()!!.rightView.setTextColor(resources.getColor(R.color.black))
                getTitleBar()!!.rightView.setOnClickListener {
                    updateProfile()
                }

            }
            mViewModel.apply {
                name.value = data!!.hydraHomeHouseholdProfileName
                dayStartTime.value = data!!.hydraHomeHouseholdProfileDayStart
                dayEndTime.value = data!!.hydraHomeHouseholdProfileDayStop
                nightStartTime.value = data!!.hydraHomeHouseholdProfileNightStart
                nightEndTime.value = data!!.hydraHomeHouseholdProfileNightStop
            }
        }

    }

    override fun initClick() {
        mViewModel.weekRvConfig.apply {
            getAdapter().setOnItemClickListener { adapter, view, position ->
                var datas = getAdapter().data as MutableList<WeekRsp>
                weeks.get(position).isSel = !datas[position].isSel!!
                getAdapter().setList(weeks)
            }
        }
        mViewBind.apply {
            dayStartTimeLl.setOnClickListener {
                XPopup.Builder(this@CreateAProfileActivity).asCustom(
                    PopProfileTimePicker(this@CreateAProfileActivity).apply {
                        setCallBack(okBlock = {
                            mViewModel.dayStartTime.value = it
                        })
                    }).show()
            }
            dayEndTimeLl.setOnClickListener {
                XPopup.Builder(this@CreateAProfileActivity).asCustom(
                    PopProfileTimePicker(this@CreateAProfileActivity).apply {
                        setCallBack(okBlock = {
                            mViewModel.dayEndTime.value = it
                        })
                    }).show()
            }
            nightStartTimeLl.setOnClickListener {
                XPopup.Builder(this@CreateAProfileActivity).asCustom(
                    PopProfileTimePicker(this@CreateAProfileActivity).apply {
                        setCallBack(okBlock = {
                            mViewModel.nightStartTime.value = it
                        })
                    }).show()
            }
            nightEndTimeLl.setOnClickListener {
                XPopup.Builder(this@CreateAProfileActivity).asCustom(
                    PopProfileTimePicker(this@CreateAProfileActivity).apply {
                        setCallBack(okBlock = {
                            mViewModel.nightEndTime.value = it
                        })
                    }).show()
            }
            confitm.setOnClickListener {
                mViewModel.apply {
                    if (name.value.isNullOrBlank()) {
                        "Enter Profile Name".toast()
                        return@setOnClickListener
                    }
                    if (dayStartTime.value.isNullOrBlank()) {
                        "Select the day start time".toast()
                        return@setOnClickListener
                    }
                    if (dayEndTime.value.isNullOrBlank()) {
                        "Select the day end time".toast()
                        return@setOnClickListener
                    }
                    if (nightStartTime.value.isNullOrBlank()) {
                        "Select the night start time".toast()
                        return@setOnClickListener
                    }
                    if (nightEndTime.value.isNullOrBlank()) {
                        "Select the night end time".toast()
                        return@setOnClickListener
                    }
                    var daynumber = 0
                    var days = arrayListOf<Int>()
                    for (i in 0 until weeks.size) {
                        if (weeks[i].isSel!!) {
                            daynumber++
                            days.add(i + 1)
                        }
                    }
                    if (daynumber == 0) {
                        "Select the days".toast()
                        return@setOnClickListener
                    }
                    addProfile(days)
                }

            }
        }


    }

    fun updateProfile() {
        mViewModel.apply {
            if (name.value.isNullOrBlank()) {
                "Enter Profile Name".toast()
                return
            }
            if (dayStartTime.value.isNullOrBlank()) {
                "Select the day start time".toast()
                return
            }
            if (dayEndTime.value.isNullOrBlank()) {
                "Select the day end time".toast()
                return
            }
            if (nightStartTime.value.isNullOrBlank()) {
                "Select the night start time".toast()
                return
            }
            if (nightEndTime.value.isNullOrBlank()) {
                "Select the night end time".toast()
                return
            }
            var daynumber = 0
            var days = arrayListOf<Int>()
            for (i in 0 until weeks.size) {
                if (weeks[i].isSel!!) {
                    daynumber++
                    days.add(i + 1)
                }
            }
            if (daynumber == 0) {
                "Select the days".toast()
                return
            }
            updateProfile(days, data!!.hydraHomeHouseholdProfilePk!!)
        }

    }

    override fun loadData() {

    }

    fun initWeek() {
        weeks.add(WeekRsp(weekNmae = "MON"))
        weeks.add(WeekRsp(weekNmae = "TUE"))
        weeks.add(WeekRsp(weekNmae = "WED"))
        weeks.add(WeekRsp(weekNmae = "THU"))
        weeks.add(WeekRsp(weekNmae = "FRI"))
        weeks.add(WeekRsp(weekNmae = "SAT"))
        weeks.add(WeekRsp(weekNmae = "SUN"))
        if (type != 0) {
            for (i in 0 until data!!.hydraHomeHouseholdProfileWeeks.size) {
                weeks[data!!.hydraHomeHouseholdProfileWeeks[i] - 1].isSel = true
            }
        }

        mViewModel.weekRvConfig.setList(weeks)
    }

    override fun initObserver() {
        mViewModel.apply {
            addProfileLiveData.observe(this@CreateAProfileActivity, {
                if (it) {
                    "Success".toast()
                    this@CreateAProfileActivity.finish()
                }
            })
        }
    }
}