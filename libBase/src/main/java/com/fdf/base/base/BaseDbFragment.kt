package com.fdf.base.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fdf.base.R
import com.fdf.base.ext.getViewBinding
import com.fdf.base.ext.log
import com.fdf.base.widget.CustomDialogFragment
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


/**
 *    Created by Administrator on 2021/11/10.
 *    Desc :
 */
abstract class BaseDbFragment<VB : ViewDataBinding> : Fragment(), CoroutineScope by MainScope() {

    protected lateinit var mViewBind: VB


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBind = getViewBinding(inflater, container)
        mViewBind.lifecycleOwner = this
        return mViewBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initClick()
        initObserver()

        lifecycleScope.launchWhenResumed {
            loadData()
        }
    }

    abstract fun init()

    abstract fun initClick()

    abstract fun loadData()

    abstract fun initObserver()

    override fun onDestroy() {
        super.onDestroy()
        //此处记得取消绑定，避免内存泄露
        if (::mViewBind.isInitialized) {
            mViewBind.unbind()
        }
    }
    protected fun requestPermission(vararg permissions: String,block: () -> Unit) {
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