package com.powercore.hydrahome.ui.activity.resetpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.view.View
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.isEmail
import com.fdf.base.ext.log
import com.fdf.base.ext.toast
import com.fdf.base.utils.startActivity
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityResetPasswordBinding
import com.powercore.hydrahome.ui.activity.login.LoginActivity

class ResetPasswordActivity :
    BaseDbVmActivity<ActivityResetPasswordBinding, ResetPassworViewModel>(isOverrideContentView = true) {

    var timer: CountDownTimer? = null
    override fun init() {
        mViewBind.vm = mViewModel
        ImmersionBar.with(this).fitsSystemWindows(false).statusBarColor(
            R.color.white
        ).autoDarkModeEnable(true).init()
        setMarginTop(mViewBind.titleBar)

        mViewBind.apply {
            etNote.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
        }
    }

    override fun initClick() {
        mViewBind.apply {
            titleBar.setOnTitleBarListener(object : OnTitleBarListener {
                override fun onLeftClick(view: View?) {
                    onBackPressed()
                }

                override fun onTitleClick(view: View?) {
                }

                override fun onRightClick(view: View?) {
                }
            })

            btnSend.setOnClickListener {
                mViewModel.getEmailCode()
            }

            etEamil.doOnTextChanged { charSequence, start, _, _ ->
                spaceDetection(etEamil, charSequence, start)
                change()
            }
            etNote.doOnTextChanged { charSequence, start, _, _ ->
                spaceDetection(etNote, charSequence, start)
                change()
            }
            etPwd.doOnTextChanged { charSequence, start, _, _ ->
                spaceDetection(etPwd, charSequence, start)
                change()
            }
            etPwd1.doOnTextChanged { charSequence, start, _, _ ->
                spaceDetection(etPwd1, charSequence, start)
                change()
            }
            showPwd1.setOnClickListener {
                isShowPassword()
            }
            showPwd2.setOnClickListener {
                isShowPassword1()
            }
        }
    }

    override fun loadData() {

    }

    override fun initObserver() {
        mViewModel.sendForgetPwdCodeSuccess.observe(this) {
            if (it)
                countDown()
        }
        mViewModel.forgetLiveData.observe(this) {
            if (it) {
                startActivity<LoginActivity>()
                finish()
            }
        }
    }

    // 禁止EditText输入空格
    private fun spaceDetection(edit: EditText, charSequence: CharSequence?, start: Int) {
        if (charSequence.toString().contains(" ")) {
            val str = charSequence.toString().split(" ").toTypedArray()
            val sb = StringBuffer()
            for (i in str.indices) {
                sb.append(str[i])
            }
            edit.setText(sb.toString())
            edit.setSelection(start)
        }
    }

    private fun countDown() {
        timer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val s = millisUntilFinished / 1000
                "s=${s}".log()
                if (s == 0L) {
                    mViewBind.btnSend.text = "Send"
                    mViewBind.btnSend.isEnabled = true

                } else {
                    mViewBind.btnSend.text = "${s}s"
                    mViewBind.btnSend.isEnabled = false

                }
            }

            override fun onFinish() {
            }
        }
        timer?.start()
    }

    fun change() {
        mViewModel.apply {
            forgetPwdReq.value.apply {
                if (this!!.email.isBlank() || code.isBlank() || password.isBlank() || checkedPassword.isBlank()) {
                    mViewBind.btnConfirm.isEnabled = false
                    return
                }
                mViewBind.btnConfirm.alpha = 1F
                mViewBind.btnConfirm.isEnabled = true
            }
        }
       /* mViewBind.apply {
            if (etEamil.text.isBlank() || .isBlank() || password.isBlank() || checkedPassword.isBlank()) {
                mViewBind.btnConfirm.isEnabled = false
                return
            }
        }*/
    }

    private var isHide = true
    private fun isShowPassword() {
        if (!isHide) {
            //R.mipmap.password_show是表示显示密码的“眼睛”图标
            mViewBind.showPwd1.setImageResource(R.mipmap.icon_close_eye)
            //密文
            val method2: TransformationMethod = PasswordTransformationMethod.getInstance()
            mViewBind.etPwd.transformationMethod = method2
            isHide = true
        } else {
            //R.mipmap.password_miss是表示隐藏密码的“眼睛+斜杠”图标
            mViewBind.showPwd1.setImageResource(R.mipmap.ic_open_eye)
            //密文
            val method1 = HideReturnsTransformationMethod.getInstance()
            mViewBind.etPwd.transformationMethod = method1
            isHide = false
        }
        //重置光标位置
        val index: Int = mViewBind.etPwd.text.toString().length
        mViewBind.etPwd.setSelection(index)
    }

    private var isHide1 = true
    private fun isShowPassword1() {
        if (!isHide1) {
            //R.mipmap.password_show是表示显示密码的“眼睛”图标
            mViewBind.showPwd2.setImageResource(R.mipmap.icon_close_eye)
            //密文
            val method2: TransformationMethod = PasswordTransformationMethod.getInstance()
            mViewBind.etPwd1.transformationMethod = method2
            isHide1 = true
        } else {
            //R.mipmap.password_miss是表示隐藏密码的“眼睛+斜杠”图标
            mViewBind.showPwd2.setImageResource(R.mipmap.ic_open_eye)
            //密文
            val method1 = HideReturnsTransformationMethod.getInstance()
            mViewBind.etPwd1.transformationMethod = method1
            isHide1 = false
        }
        //重置光标位置
        val index: Int = mViewBind.etPwd1.text.toString().length
        mViewBind.etPwd1.setSelection(index)
    }
}