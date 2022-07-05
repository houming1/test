package com.powercore.hydrahome.ui.activity.bluetooth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import androidx.lifecycle.lifecycleScope
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.fdf.base.ext.toast
import com.fdf.base.getString
import com.fdf.base.utils.CacheUtil
import com.gk.net.ext.showToast
import com.gk.net.utils.MoshiUtils
import com.gyf.immersionbar.ImmersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.bean.ConnectWifiBean
import com.powercore.hydrahome.databinding.ActivityChargeWhiteListEditBinding
import com.powercore.hydrahome.ext.hiddenLoading
import com.powercore.hydrahome.ext.showLoading
import com.powercore.hydrahome.utils.BlufiUtils
import com.powercore.hydrahome.widget.PopWifiName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChargeWhiteListEditActivity : BaseDbVmActivity<ActivityChargeWhiteListEditBinding, ChargeWhiteListEditViewModel>(
        title = "Set Up Network",
        titleBarColor = getColor(R.color.white)
    ) {


    private var mCacheWifiNameList: MutableList<ConnectWifiBean> = mutableListOf()
    private var isRememberWifi = false
    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
        isShowPassword()
    }

    override fun initClick() {
        mViewBind.toLockPassword.setOnClickListener {
            isShowPassword()

        }
        mViewBind.llRemember.setOnClickListener {
            //记住密码
            mViewModel.isRemember.value = !mViewModel.isRemember.value!!
            mViewBind.cbRemember.isChecked = mViewModel.isRemember.value!!
        }
        mViewBind.btnNetx.setOnClickListener {
            //下一步
            toConfirm()
        }

        mViewBind.ivDropDown.setOnClickListener {
            if (mCacheWifiNameList.isEmpty()) {
                "No wifi configuration records".toast()
                return@setOnClickListener
            }
            XPopup.Builder(this)
                .atView(mViewBind.emailCode)
                .hasShadowBg(false)
                .popupAnimation(PopupAnimation.NoAnimation)
                .asCustom(PopWifiName(this).apply {
                    setCallBack(object : PopWifiName.CallBack {
                        override fun onClickItem(name: String) {
                            mViewModel.wifiName.value = name
                            mViewBind.emailCode.setSelection(mViewBind.emailCode.text.toString().length)
                        }
                    })
                })
                .show()
        }
    }

    override fun loadData() {
        isRememberWifi = CacheUtil.getBoolean(Constant.REMEMBER_WIFI_NAME)

        getCacheWifi()

        mViewBind.cbRemember.isChecked = isRememberWifi
        mViewModel.isRemember.value = isRememberWifi

        if (isRememberWifi && mCacheWifiNameList.isNotEmpty()) {
            mViewModel.wifiName.value = mCacheWifiNameList.first().ssid
            mViewModel.password.value = mCacheWifiNameList.first().pwd
        }
    }

    override fun initObserver() {
        LiveEventBus.get<Boolean>(LiveDataBusKeys.BLU_DISCONNECT).observe(this, {
            if (it) {
                "Connection failed".toast()
                this.finish()
            }
        })
    }

    private fun getCacheWifi() {
        mCacheWifiNameList.clear()
        val wifiNameJson = CacheUtil.getString(Constant.WIFI_NAME_KEY)
        if (!wifiNameJson.isNullOrBlank()) {
            val wifiBeanList = MoshiUtils.listFromJson<ConnectWifiBean>(wifiNameJson)
            mCacheWifiNameList.addAll(wifiBeanList)
        }
    }
    private fun isShowPassword() {
        if (!mViewModel.passwordVisible.value!!) {
            //R.mipmap.password_show是表示显示密码的“眼睛”图标
            mViewBind.toLockPassword.setImageResource(R.mipmap.icon_close_eye)
            //密文
            val method2: TransformationMethod = PasswordTransformationMethod.getInstance()
            mViewBind.etPwd.transformationMethod = method2
            mViewModel.passwordVisible.value = true
        } else {
//            //R.mipmap.password_miss是表示隐藏密码的“眼睛+斜杠”图标
            mViewBind.toLockPassword.setImageResource(R.mipmap.ic_open_eye)
            //密文
            val method1 = HideReturnsTransformationMethod.getInstance()
            mViewBind.etPwd.transformationMethod = method1
            mViewModel.passwordVisible.value = false
        }
        //重置光标位置
        val index: Int = mViewBind.etPwd.text.toString().length
        mViewBind.etPwd.setSelection(index)
    }

    private fun saveWifi() {
        getCacheWifi()
        val wifiBean = ConnectWifiBean(mViewModel.wifiName.value!!, mViewModel.password.value!!)
        if (mCacheWifiNameList.map { it.ssid }.contains(mViewModel.wifiName.value))
            return
        if (mCacheWifiNameList.size >= 10) {
            mCacheWifiNameList.removeLast()
        }
        mCacheWifiNameList.add(0, wifiBean)
        CacheUtil.save(Constant.WIFI_NAME_KEY, MoshiUtils.toJson(mCacheWifiNameList))
    }

    private fun toConfirm() {
        if (TextUtils.isEmpty(mViewModel.wifiName.value)) {
            "wifi name is empty".showToast()
            return
        }
        saveWifi()
        showLoading(this, "configuring...")
        BlufiUtils.configure(mViewModel.wifiName.value!!.trim(), mViewModel.password.value!!.trim(),
            success = {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(3000)
                    BlufiUtils.requestDevStatus(
                        success = {
                            hiddenLoading()
                            val popupView = XPopup.Builder(this@ChargeWhiteListEditActivity)
                                .asConfirm(
                                    "Tips", "Network configuration is successful! return now?",
                                    "Cancel", "Confirm", {
                                        LiveEventBus.get<Int>(LiveDataBusKeys.BLU_CONFIG_WIFI_STATUS).post(2)
                                        this@ChargeWhiteListEditActivity.finish()
                                    }, null, true
                                )
                            popupView.cancelTextView.setTextColor(0x8193AE)
                            popupView.confirmTextView.setTextColor(0x15CD80)
                            popupView.show()
                        }, error = {
                            hiddenLoading()
                        })
                }
//                hiddenLoading()
                if (mViewModel.isRemember.value!!) {
                    CacheUtil.save(Constant.REMEMBER_WIFI_NAME, mViewModel.isRemember.value!!)
                }


            }, error = {
                hiddenLoading()
                val popupView = XPopup.Builder(this)
                    .asConfirm(
                        "Tips", "Network configuration failed! Do you need to try again?",
                        "Cancel", "Confirm", {

                        }, {
                            LiveEventBus.get<Int>(LiveDataBusKeys.BLU_CONFIG_WIFI_STATUS).post(3)
                            this.finish()
                        }, true
                    )
                popupView.cancelTextView.setTextColor(0x8193AE)
                popupView.confirmTextView.setTextColor(0x15CD80)
                popupView.show()
            })

    }
}