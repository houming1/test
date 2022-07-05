package com.powercore.hydrahome.ui.activity.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.NetworkUtils
import com.fdf.base.base.BaseDbActivity
import com.fdf.base.ext.getColor
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.fdf.base.utils.startActivity
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivitySplashBinding
import com.powercore.hydrahome.ui.activity.login.LoginActivity
import com.powercore.hydrahome.ui.activity.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseDbActivity<ActivitySplashBinding>(isOverrideContentView = true) {


    override fun init() {
        launch(Dispatchers.IO) {
            if (!NetworkUtils.isAvailable()) {
                launch(Dispatchers.Main) {
                    "Network Unavailable".toast()
                    startActivity<LoginActivity>()
                    finish()
                }
            } else {
                lifecycleScope.launch {
                    delay(200)
                    //如果没有Token,证明未登录,跳转到登录页面
                    //否则跳转欢迎页
                    launch(Dispatchers.Main) {
                        if (CacheUtil.getString(Constant.TOKEN)?.isNotBlank() == true) {
                            startActivity<MainActivity>()
                            finish()
                        } else {
                            startActivity<LoginActivity>()
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun initClick() {

    }

    override fun loadData() {

    }

    override fun initObserver() {

    }
}