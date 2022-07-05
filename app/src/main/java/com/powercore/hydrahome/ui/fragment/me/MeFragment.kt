package com.powercore.hydrahome.ui.fragment.me

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import com.fdf.base.app.appContext
import com.fdf.base.base.BaseDbVmFragment
import com.fdf.base.ext.load
import com.fdf.base.ext.log
import com.fdf.base.ext.removeAllActivity
import com.fdf.base.utils.CacheUtil
import com.fdf.base.utils.startActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.databinding.FragmentMeBinding
import com.powercore.hydrahome.net.entity.rsp.LoginResult
import com.powercore.hydrahome.ui.activity.feedback.FeedbackActivity
import com.powercore.hydrahome.ui.activity.login.LoginActivity
import com.powercore.hydrahome.ui.imgselector.WeChatPresenter
import com.powercore.hydrahome.utils.ImageUtils
import com.powercore.hydrahome.widget.PopAvatarPicker
import com.powercore.hydrahome.widget.PopBaseContentPicker
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.selectconfig.CropConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MeFragment : BaseDbVmFragment<FragmentMeBinding, MeViewModel>() {
    //头像选择弹窗
    private val mPopAvatarPicker by lazy { PopAvatarPicker(requireActivity()) }


    //裁剪配置
    private val cropConfig by lazy {
        CropConfig()
            .apply {
                cropRectMargin = 100
                saveInDCIM(false)
                isCircle = true
                cropStyle = CropConfig.STYLE_GAP
                cropGapBackgroundColor = Color.WHITE
            }

    }

    override fun init() {
        mPopAvatarPicker.setCallBack(object : PopAvatarPicker.CallBack {

            //点击了相机
            override fun clickCamera() {
                requestPermission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) {
                    ImagePicker
                        .takePhotoAndCrop(activity, WeChatPresenter(), cropConfig) {
                            //拍照回调，主线程
                            //图片选择回调，主线程
                            it.first().cropUrl.toString().log()
                            var uri = ImageUtils.getBitmapFormUri(
                                appContext,
                                Uri.fromFile(File(it.first().cropUrl))
                            )
                            mViewModel.updateAvatar(uri)
                        }
                }
            }

            //点击了图库
            override fun clickPhoto() {
                requestPermission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) {
                    ImagePicker
                        .withMulti(WeChatPresenter())//指定presenter
                        .cropAsCircle()
                        .crop(activity) {
                            it.first().cropUrl.toString().log()
                            var uri = ImageUtils.getBitmapFormUri(
                                appContext,
                                Uri.fromFile(File(it.first().cropUrl))
                            )
                            mViewModel.updateAvatar(uri)
                        }
                }
            }
        })
    }

    override fun initClick() {
        mViewBind.apply {
            logout.setOnClickListener {
                //退出登录
                XPopup.Builder(activity).asCustom(
                    PopBaseContentPicker(
                        requireActivity(),
                        "Confirm",
                        "Are you sure you want to log out?"
                    ).apply {
                        setCallBack {
                            mViewModel.logOut()
                        }
                    }).show()
            }

            editor.setOnClickListener {
                XPopup.Builder(activity)
                    .asCustom(mPopAvatarPicker)
                    .show()

                XPopup.Builder(activity)
                    .atView(mViewBind.avatar)
                    .hasShadowBg(false)
                    .popupAnimation(PopupAnimation.NoAnimation)
                    .asCustom(mPopAvatarPicker)
                    .show()
            }
            feedback.setOnClickListener {
                startActivity<FeedbackActivity>()
            }
        }


    }

    override fun loadData() {
        val loginResult = CacheUtil.getParcelable(Constant.LOGIN_RESULT, LoginResult::class.java)
        launch(Dispatchers.IO) {
            mViewBind.avatar.load(loginResult?.avatarUrl, isCircle = true)
        }
    }

    override fun initObserver() {
        mViewModel.logout.observe(this, {
            CacheUtil.removeKey(Constant.TOKEN)
            removeAllActivity()
            val intent = Intent(appContext, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        })
        mViewModel.avatarUrl.observe(this, {
            mViewBind.avatar.load(it, isCircle = true)
        })
    }
}