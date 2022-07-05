package com.powercore.hydrahome.ui.activity.changepassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.fdf.base.base.BaseDbVmActivity
import com.fdf.base.ext.getColor
import com.gyf.immersionbar.ImmersionBar
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.ActivityChangePasswordBinding
import com.powercore.hydrahome.databinding.ActivitySettingBinding
import com.powercore.hydrahome.ui.activity.setting.SettingViewModel

class ChangePasswordActivity : BaseDbVmActivity<ActivityChangePasswordBinding, ChangePassworViewModel>(
    title = "Change Password",
    titleBarColor = getColor(R.color.white)
) {


    override fun init() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.white)
            .autoDarkModeEnable(true).init()
        mViewBind.vm = mViewModel
    }

    override fun initClick() {
        mViewBind.apply {
            etPwd1.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
                spaceDetection(etPwd1, charSequence, start)

            }
            etPwd2.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
                spaceDetection(etPwd2, charSequence, start)

            }
            etPwd3.doOnTextChanged { charSequence, start, _, _ ->
                // 禁止EditText输入空格
                spaceDetection(etPwd3, charSequence, start)

            }
            showPwd1.setOnClickListener {
                isShowPassword1()
            }
            showPwd2.setOnClickListener {
                isShowPassword2()
            }
            showPwd3.setOnClickListener {
                isShowPassword3()
            }
        }

    }

    override fun loadData() {

    }

    override fun initObserver() {

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

    private var isHide1 = true
    private fun isShowPassword1() {
        if (!isHide1) {
            //R.mipmap.password_show是表示显示密码的“眼睛”图标
            mViewBind.showPwd1.setImageResource(R.mipmap.icon_close_eye)
            //密文
            val method2: TransformationMethod = PasswordTransformationMethod.getInstance()
            mViewBind.etPwd1.transformationMethod = method2
            isHide1 = true
        } else {
            //R.mipmap.password_miss是表示隐藏密码的“眼睛+斜杠”图标
            mViewBind.showPwd1.setImageResource(R.mipmap.ic_open_eye)
            //密文
            val method1 = HideReturnsTransformationMethod.getInstance()
            mViewBind.etPwd1.transformationMethod = method1
            isHide1 = false
        }
        //重置光标位置
        val index: Int = mViewBind.etPwd1.text.toString().length
        mViewBind.etPwd1.setSelection(index)
    }

    private var isHide2 = true
    private fun isShowPassword2() {
        if (!isHide2) {
            //R.mipmap.password_show是表示显示密码的“眼睛”图标
            mViewBind.showPwd2.setImageResource(R.mipmap.icon_close_eye)
            //密文
            val method2: TransformationMethod = PasswordTransformationMethod.getInstance()
            mViewBind.etPwd2.transformationMethod = method2
            isHide2 = true
        } else {
            //R.mipmap.password_miss是表示隐藏密码的“眼睛+斜杠”图标
            mViewBind.showPwd2.setImageResource(R.mipmap.ic_open_eye)
            //密文
            val method1 = HideReturnsTransformationMethod.getInstance()
            mViewBind.etPwd2.transformationMethod = method1
            isHide2 = false
        }
        //重置光标位置
        val index: Int = mViewBind.etPwd2.text.toString().length
        mViewBind.etPwd2.setSelection(index)
    }
    private var isHide3 = true
    private fun isShowPassword3() {
        if (!isHide3) {
            //R.mipmap.password_show是表示显示密码的“眼睛”图标
            mViewBind.showPwd3.setImageResource(R.mipmap.icon_close_eye)
            //密文
            val method2: TransformationMethod = PasswordTransformationMethod.getInstance()
            mViewBind.etPwd3.transformationMethod = method2
            isHide3 = true
        } else {
            //R.mipmap.password_miss是表示隐藏密码的“眼睛+斜杠”图标
            mViewBind.showPwd3.setImageResource(R.mipmap.ic_open_eye)
            //密文
            val method1 = HideReturnsTransformationMethod.getInstance()
            mViewBind.etPwd3.transformationMethod = method1
            isHide3 = false
        }
        //重置光标位置
        val index: Int = mViewBind.etPwd3.text.toString().length
        mViewBind.etPwd3.setSelection(index)
    }
}