package com.fdf.base.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.fdf.base.BaseConfig
import com.fdf.base.R
import com.fdf.base.ext.getViewBinding
import com.fdf.base.ext.log
import com.fdf.base.widget.CustomDialogFragment
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import me.jessyan.autosize.utils.ScreenUtils


/**
 *    Created by Administrator on 2021/11/10.
 *    Desc :
 */
abstract class BaseDbActivity<VB : ViewDataBinding>(
    protected var title: String = "Title",
    @ColorInt private var titleBarColor: Int = -1,
    private var rightIcon: Drawable? = null,
    private var isOverrideContentView: Boolean = false,
    private var rightClick: () -> Unit = {},
) : AppCompatActivity(),
    CoroutineScope by MainScope() {


    private var mInputMethodManager: InputMethodManager? = null
    protected var mTitleBar: TitleBar? = null
    private val mThemeColor = -1

    //状态栏高度
    private var mStatusBarHeight = 0

    val mViewBind: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        getViewBinding(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStatusBarHeight = ScreenUtils.getStatusBarHeight()
        if (isOverrideContentView) {
            setContentView(mViewBind.root)
        } else {
            setContentView(R.layout.activity_base)
            val rootView = findViewById<LinearLayout>(R.id.root_layout)
            initToolBar(rootView)
            rootView.addView(this.mViewBind.root, ViewGroup.LayoutParams(-1, -1))
        }
        mViewBind.lifecycleOwner = this
        init()
        initClick()
        initObserver()

        lifecycleScope.launchWhenResumed {
            loadData()
        }

        LiveEventBus.get<String>("show_toast").observe(this, {
            it.log()
        })
    }
    protected fun setMarginTop(view: View) {
        when (val lp = view.layoutParams) {
            is LinearLayout.LayoutParams -> {
                lp.setMargins(0, mStatusBarHeight, 0, 0)
            }
            is RelativeLayout.LayoutParams -> {
                lp.setMargins(0, mStatusBarHeight, 0, 0)
            }
            is ConstraintLayout.LayoutParams -> {
                lp.setMargins(0, mStatusBarHeight, 0, 0)
            }
        }
    }
    protected fun initToolBar(rootView: ViewGroup) {
        val toolbarView = getToolbar()
        rootView.addView(toolbarView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mTitleBar = toolbarView.findViewById(R.id.title_bar)
        mTitleBar?.title = title
        mTitleBar?.setBackgroundColor(if (titleBarColor == -1) Color.parseColor("#FFFFFF") else titleBarColor)
        mTitleBar?.leftIcon = BaseConfig.config?.getTitleBarLeftIcon()
        if (rightIcon != null)
            mTitleBar?.rightIcon = rightIcon

        mTitleBar?.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(view: View?) {
                onBackPressed()
            }

            override fun onTitleClick(view: View?) {

            }

            override fun onRightClick(view: View?) {
                rightClick.invoke()
            }
        })
    }

    private fun getToolbar(): View = layoutInflater.inflate(R.layout.tool_bar, null)

    protected fun getTitleBar() = mTitleBar

    abstract fun init()

    abstract fun initClick()

    abstract fun loadData()

    abstract fun initObserver()

    override fun finish() {
        hideSoftKeyBoard()
        super.finish()
    }

    protected fun hideSoftKeyBoard() {
        val localView = currentFocus
        if (mInputMethodManager == null)
            mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (localView != null && mInputMethodManager != null)
            mInputMethodManager!!.hideSoftInputFromWindow(localView.windowToken, 2)
    }

    protected fun requestPermission(vararg permissions: String, block: () -> Unit) {
        PermissionX.init(this)
            .permissions(*permissions)
            .setDialogTintColor(Color.parseColor("#1972e8"), Color.parseColor("#8ab6f5"))
            .onExplainRequestReason { scope, deniedList ->
                val message = "The ${getString(R.string.app_name)} requires you to grant the following permissions to use it properly"
                val dialog = CustomDialogFragment(message, deniedList)
                scope.showRequestReasonDialog(dialog)
            }
            //转到设置中去手动改开启
            .onForwardToSettings { scope, deniedList ->
                val message = "Please allow the following permissions in settings"
                val dialog = CustomDialogFragment(message, deniedList)
                scope.showForwardToSettingsDialog(dialog)
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    "已授予所有权限".log()
                    block.invoke()
                } else {
                    "这些权限被拒绝: $deniedList".log()
                }
            }

    }
}