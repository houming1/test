package com.powercore.hydrahome.ui.activity.register

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
import com.fdf.base.ext.parseState
import com.fdf.base.ext.toast
import com.fdf.base.utils.CacheUtil
import com.fdf.base.utils.startActivity
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityRegisterBinding
import com.powercore.hydrahome.ui.activity.main.MainActivity

class RegisterActivity :
    BaseDbVmActivity<ActivityRegisterBinding, RegisterViewModel>(isOverrideContentView = true) {

    var timer: CountDownTimer? = null
    override fun init() {
        mViewBind.vm = mViewModel
        ImmersionBar.with(this).fitsSystemWindows(false).statusBarColor(
            R.color.white
        ).autoDarkModeEnable(true).init()
        setMarginTop(mViewBind.titleBar)

        mViewBind.apply {
            etName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
            etNote.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
        }
    }

    override fun initClick() {
        mViewBind.apply {
            btnSignUp.setOnClickListener {
                mViewModel.validUserName { mViewModel.register() }
            }
            titleBar.setOnTitleBarListener(object : OnTitleBarListener {
                override fun onLeftClick(view: View?) {
                    onBackPressed()
                }

                override fun onTitleClick(view: View?) {
                }

                override fun onRightClick(view: View?) {
                }
            })
            etName.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
                spaceDetection(etName, charSequence, start)
                change()
            }
            etEmail.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
                spaceDetection(etEmail, charSequence, start)
                change()
            }
            etNote.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
                spaceDetection(etNote, charSequence, start)
                change()
            }
            etPwd.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
                spaceDetection(etPwd, charSequence, start)
                change()
            }
            etPwd1.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
                spaceDetection(etPwd1, charSequence, start)
                change()
            }
            btnSend.setOnClickListener {
                mViewModel.sendCode()
            }
            showPwd1.setOnClickListener {
                isShowPassword()
            }
            showPwd2.setOnClickListener {
                isShowPassword1()
            }
        }
    }

    fun change() {
        mViewBind.apply {  }
        mViewModel.apply {
            registerReq.value.apply {
                if (this!!.email.isBlank() || note.isBlank() || name.isBlank() || password.isBlank() || checkedPassword.isBlank()) {
                    mViewBind.btnSignUp.isEnabled = false
                    return
                }
                mViewBind.btnSignUp.alpha = 1F
                mViewBind.btnSignUp.isEnabled = true
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

    override fun loadData() {

    }

    override fun initObserver() {
        val owner = this
        mViewModel.apply {
            sendSuccess.observe(owner, {
                if (it)
                    countDown()
            })
        }
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

    private fun countDown() {
        timer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val s = millisUntilFinished / 1000
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

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
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