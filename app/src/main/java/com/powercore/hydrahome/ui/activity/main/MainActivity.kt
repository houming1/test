package com.powercore.hydrahome.ui.activity.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.log
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.fdf.base.utils.startActivity
import com.gk.net.ext.toRequestBody
import com.gk.net.utils.toJson
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gyf.immersionbar.ImmersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityMainBinding
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.ui.activity.charge.ChargingActivity
import com.powercore.hydrahome.ui.activity.chargingprofile.CreateAProfileActivity
import com.powercore.hydrahome.ui.fragment.home.HomeFragment
import com.powercore.hydrahome.ui.fragment.me.MeFragment
import com.powercore.hydrahome.ui.fragment.record.RecordFragment
import com.powercore.hydrahome.widget.PopProgress
import com.powercore.hydrahome.ws.WsService
import com.powercore.hydrahome.ws.entity.BaseWsResponse
import com.powercore.hydrahome.ws.entity.WsData
import kotlin.system.exitProcess

class MainActivity :
    BaseDbVmActivity<ActivityMainBinding, MainViewModel>(isOverrideContentView = true) {
    private var mCurrent: Fragment? = null
    private var fragmentList: ArrayList<Fragment> = arrayListOf()
    private val progressView by lazy { PopProgress(this) }
    private val mProgressPop by lazy {
        XPopup.Builder(this)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .asCustom(progressView)


    }

    private val mLoadingDialog by lazy {
        XPopup.Builder(this).asLoading("loading...", R.layout.dialog_loading)
    }
    //Tab ids
    private var ids = mutableListOf(R.id.nav_home, R.id.nav_record, R.id.nav_me).toMutableList()
    override fun init() {
        changeStatusBarColor(0)
        mViewBind.vm = mViewModel
        fragmentList.add(HomeFragment())
        fragmentList.add(RecordFragment())
        fragmentList.add(MeFragment())
        switchContent(fragmentList[0])
        //添加的id与配置文件中要绝对一致(包括顺序)
        clearToast(mViewBind.bottomNav, ids)
        mViewBind.bottomNav.itemIconTintList = null

        //启动预加载WebView服务
        Intent(this, WsService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    override fun initClick() {
        mViewBind.apply {
            bottomNav.apply {
                //禁止底部导航切换动画
                isItemHorizontalTranslationEnabled = false
                //点击底部导航
                //这里因为设计图中间的item不显示文字且显示尺寸不同于别的item
                //所以在bottomNavbarView加了一层，用于显示中的item
                //点击操作做了单独处理 如下
                setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.nav_home -> {

                            switchContent(fragmentList[0])
                            changeStatusBarColor(0)
                        }
                        R.id.nav_record -> {
                            switchContent(fragmentList[1])
                            changeStatusBarColor(1)
                        }
                        R.id.nav_me -> {
                            switchContent(fragmentList[2])
                            changeStatusBarColor(1)
                        }
                    }
                    true
                }
            }

        }
    }
    private fun changeStatusBarColor(index: Int) {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(
            if (index == 1) R.color.white else R.color.act_bg_color
        ).autoDarkModeEnable(true).init()
    }
    override fun loadData() {

    }

    override fun initObserver() {
        LiveEventBus.get<BaseWsResponse<WsData>>(LiveDataBusKeys.MSG_ARRIVED).observe(this, { it ->
            "ws 消息->${it.data?.toJson()}".log()
            "ws 消息->${!it.success}".log()
            if (!it.success)
                it.data?.message?.toast()
            isChargeWsResponse = true
            if (it.data?.api == "remoteStart") {
                //接收到第一个百分比值,开始计时
                //开始等待90s 如再次接收到非100不计时
                //超过90s没有收到100 提示超时 否则即为成功

                if (!mProgressPop.isShow) {
                    mProgressPop.show()
                }
                it.data?.percent?.toInt()?.let {
                    mCurrChargePercent = it
                    progressView.setProgress(it)
                    if (it == 100) {
                        mProgressPop.dismiss()
                      startActivity<ChargingActivity>()
                        LiveEventBus.get<Boolean>(LiveDataBusKeys.REFRESH_CHARGE_BOX).post(true)
                        LiveEventBus.get<Boolean>(LiveDataBusKeys.REFRESH_CHARGING).post(true)
                    } else {
                        waitChargeWsResponsePercent() {
                            mProgressPop.smartDismiss()
                            mViewModel.getHomeChargeBox()
                        }
                    }
                }
            } else if (it.data?.api == "remoteStop") {
                LiveEventBus.get<Boolean>(LiveDataBusKeys.REFRESH_CHARGE_BOX).post(true)
                LiveEventBus.get<Boolean>(LiveDataBusKeys.REFRESH_CHARGING).post(true)
                LiveEventBus.get<Long>(LiveDataBusKeys.REFRESH_CHARGE_RECORD).post(System.currentTimeMillis())
                switchContent(fragmentList[1])
                mViewBind.bottomNav.selectedItemId = R.id.nav_record
            }
        })
        LiveEventBus.get<String>(LiveDataBusKeys.MSG_ERROR).observe(this, {
            "ws 消息 错误->${it}".log()
            it.toast()
            isChargeWsResponse = true
            mLoadingDialog.dismiss()
            //没有等待到15秒就失败  提前结束15s倒计时
            mWaitChargeWsResponseCountDownTimer?.cancel()
            it.toast()
        })

        LiveEventBus.get<Boolean>(LiveDataBusKeys.NAVIGATION_TO_RECORD).observe(this, {
            if (it) {

                switchContent(fragmentList[1])
                mViewBind.bottomNav.selectedItemId = R.id.nav_record
            }
        })
//        LiveEventBus.get<Int>(LiveDataBusKeys.NAVIGATION_TO_MAIN).observe(this,{
//            changeStatusBarColor(it)
//            switchContent(fragmentList[it])
//        })
        LiveEventBus.get<Boolean>(LiveDataBusKeys.CHARGE_REQUEST_SUCCESS).observe(this, {
            isChargeWsResponse = false
            isCountDownNinetySecond = false
            if (it) {
                waitChargeWsResponse()
                mLoadingDialog.show()
            } else {
                mLoadingDialog.smartDismiss()
            }
        })
    }

    /**
     * 开始倒计时
     * 如果在一定时间内
     */
    var isChargeWsResponse = false
    var mWaitChargeWsResponseCountDownTimer: CountDownTimer? = null
    private fun waitChargeWsResponse() {
        mWaitChargeWsResponseCountDownTimer = object : CountDownTimer(15 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isChargeWsResponse) {
                    mLoadingDialog.smartDismiss()
                    mWaitChargeWsResponseCountDownTimer?.cancel()
                }
            }

            override fun onFinish() {
                "remote charge timeout".toast()
                mLoadingDialog.smartDismiss()
                mViewModel.getHomeChargeBox()
            }
        }
        mWaitChargeWsResponseCountDownTimer?.start()
    }
    /**
     * 接收到第一个百分比后开始倒计时
     * 如已在计时中，则无视
     */
    var isCountDownNinetySecond = false
    var mNinetySecondCountDownTimer: CountDownTimer? = null
    var mCurrChargePercent = 0
    private fun waitChargeWsResponsePercent(countDownFinish: () -> Unit) {
        if (isCountDownNinetySecond) return
        isCountDownNinetySecond = true
        mNinetySecondCountDownTimer = object : CountDownTimer(90 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (mCurrChargePercent == 100) {
                    mLoadingDialog.smartDismiss()
                    mNinetySecondCountDownTimer?.cancel()
                }
            }

            override fun onFinish() {
                "remote charge timeout".toast()
                isCountDownNinetySecond = false
                countDownFinish.invoke()
            }
        }
        mNinetySecondCountDownTimer?.start()
    }

    /*
 * 清除长按时的toast
 * @param bottomNavigationView 当前BottomNavigationView
 * @param ids                  与配置文件中对应的所有id
 */
    private fun clearToast(bottomNavigationView: BottomNavigationView, ids: MutableList<Int>) {
        val bottomNavigationMenuView: ViewGroup = (bottomNavigationView.getChildAt(0) as ViewGroup)
        //遍历子View,重写长按点击事件
        for (position in 0 until ids.size) {
            bottomNavigationMenuView.getChildAt(position)
                .findViewById<View>((ids[position] as Int?)!!)
                .setOnLongClickListener { true }
        }
    }

    /**
     * 修改显示的内容，不会销毁与重建
     * to 要切换的fragment
     * mCurrent 当前fragment
     */
    private fun switchContent(to: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        if (mCurrent != null && mCurrent != to) {
            if (!to.isAdded) { // 判断是否被add过
                // 隐藏当前的fragment，将 下一个fragment 添加进去
                transaction.hide(mCurrent!!).add(R.id.fragment_container_view, to)
            } else {
                // 隐藏当前的fragment，显示下一个fragment
                transaction.hide(mCurrent!!).show(to)
            }
            mCurrent = to;
        } else if (mCurrent == null) {
            mCurrent = to
            if (!to.isAdded) {
                transaction.add(R.id.fragment_container_view, to)
            } else {
                transaction.show(to)
            }
        }
        transaction.setMaxLifecycle(to, Lifecycle.State.RESUMED)
        transaction.commitAllowingStateLoss()
    }

    private var exitTime = 0L
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            "Press again to exit".toast()
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            exitProcess(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    override fun onDestroy() {
        WsService.isStop = stopService(Intent(this, WsService::class.java))
        super.onDestroy()
    }


}