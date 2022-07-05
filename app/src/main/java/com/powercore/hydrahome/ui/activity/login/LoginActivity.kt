package com.powercore.hydrahome.ui.activity.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.log
import com.fdf.base.ext.parseState
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.fdf.base.utils.startActivity
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.jeremyliao.liveeventbus.LiveEventBus
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityLoginBinding
import com.powercore.hydrahome.ui.activity.main.MainActivity
import com.powercore.hydrahome.ui.activity.register.RegisterActivity
import com.powercore.hydrahome.ui.activity.resetpassword.ResetPasswordActivity
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder

class LoginActivity :
    BaseDbVmActivity<ActivityLoginBinding, LoginViewModel>(isOverrideContentView = true) {
    var imageList = ArrayList<Int>()
    private var isHide = true
    override fun init() {
        mViewBind.vm = mViewModel
        ImmersionBar.with(this).fitsSystemWindows(false).init()
        mViewBind.editAccount.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格
            if (charSequence.toString().contains(" ")) {
                val str = charSequence.toString().split(" ").toTypedArray()
                val sb = StringBuffer()
                for (i in str.indices) {
                    sb.append(str[i])
                }
                mViewBind.editAccount.setText(sb.toString())
                mViewBind.editAccount.setSelection(start)
            }
        }
        mViewBind.editPwd.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格
            if (charSequence.toString().contains(" ")) {
                val str = charSequence.toString().split(" ").toTypedArray()
                val sb = StringBuffer()
                for (i in str.indices) {
                    sb.append(str[i])
                }
                mViewBind.editPwd.setText(sb.toString())
                mViewBind.editPwd.setSelection(start)
            }
        }
    }

    override fun initClick() {
        mViewBind.apply {
            btnForgetPwd.setOnClickListener {
                startActivity<ResetPasswordActivity>()
            }
            tvSignUp.setOnClickListener {
                startActivity<RegisterActivity>()
            }
            loginIsShowPwd.setOnClickListener {
                isShowPassword()
            }

        }
    }

    override fun loadData() {
        imageList.add(R.mipmap.banner_1)
        imageList.add(R.mipmap.banner_2)
        val ada = object : BannerImageAdapter<Int>(imageList) {
            override fun onBindView(
                holder: BannerImageHolder,
                data: Int,
                position: Int,
                size: Int
            ) { //图片加载自己实现
                holder.imageView.setImageResource(data)
            }
        }
        mViewBind.banner.addBannerLifecycleObserver(this)
            .setAdapter(ada)
            .setUserInputEnabled(false)
        mViewBind.banner.setIndicator(mViewBind.indicator, false)
    }

    override fun initObserver() {
        LiveEventBus.get<Boolean>(LiveDataBusKeys.RELOGIN).observe(this, {
            if (it)
                "Login timed out and need to log in again.".toast()
        })
        val owner = this
        mViewModel.apply {
            loginResultLiveData.observe(owner, { result ->
                parseState(result,
                    {
                        "token->${it.ssoticket}".log()
                        "refreshToken->${it.refreshToken}".log()
                        CacheUtil.save(Constant.EMAIL, it.email ?: "")
                        CacheUtil.save(Constant.TOKEN, it.ssoticket ?: "")
                        CacheUtil.save(Constant.REFRESH_TOKEN, it.refreshToken ?: "")
                        CacheUtil.saveParcelable(Constant.LOGIN_RESULT, it)
                        startActivity<MainActivity>()
                        finish()
                    }, { code, msg ->
                        msg.toast()
                    })
            })
        }
    }


    private fun isShowPassword() {
        if (!isHide) {
            //R.mipmap.password_show是表示显示密码的“眼睛”图标
            mViewBind.loginIsShowPwd.setImageResource(R.mipmap.icon_close_eye)
            //密文
            val method2: TransformationMethod = PasswordTransformationMethod.getInstance()
            mViewBind.editPwd.transformationMethod = method2
            isHide = true
        } else {
            //R.mipmap.password_miss是表示隐藏密码的“眼睛+斜杠”图标
            mViewBind.loginIsShowPwd.setImageResource(R.mipmap.ic_open_eye)
            //密文
            val method1 = HideReturnsTransformationMethod.getInstance()
            mViewBind.editPwd.transformationMethod = method1
            isHide = false
        }
        //重置光标位置
        val index: Int = mViewBind.editPwd.text.toString().length
        mViewBind.editPwd.setSelection(index)
    }
}