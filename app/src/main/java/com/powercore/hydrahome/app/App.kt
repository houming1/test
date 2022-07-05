package com.powercore.hydrahome.app

import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import com.fdf.base.BaseConfig
import com.fdf.base.app.BaseApplication
import com.fdf.base.base.IBaseConfig
import com.gk.net.NetConfig
import com.gk.net.base.INetConfig
import com.powercore.hydrahome.R
import com.powercore.hydrahome.utils.BlueToothUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout


/**
 *    Created by Administrator on 2021/11/10.
 *    Desc :
 */
//val wsService: WsService? = App.wsService

class App : BaseApplication(), INetConfig, IBaseConfig {
    companion object {
        lateinit var appContext: Context

    }

    init {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.transparent, R.color.theme_color) //全局设置主题颜色
            ClassicsHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setDrawableSize(20f)
        }
    }

//    companion object {
//        var wsService: WsService? = null
//    }

//    private var binder: WsService.WsClientBinder? = null
//    private val wsServiceConnection: ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
//            //服务与活动成功绑定
//            "ws服务与活动成功绑定".log()
//            binder = iBinder as WsService.WsClientBinder
//            wsService = binder?.service!!
//        }
//
//        override fun onServiceDisconnected(componentName: ComponentName) {
//            //服务与活动断开
//            "ws服务与活动成功断开".log()
//        }
//    }

    /**
     * 开启并绑定WebSocket服务
     */
//    fun startWebSocketService() {
//        Intent(this, WsService::class.java).also {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(it)
//            } else {
//                startService(it)
//            }
//            bindService(it, wsServiceConnection, BIND_AUTO_CREATE).log()
//        }
//
//    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
//        startWebSocketService()
        appContext=this
        NetConfig.config = this
        BaseConfig.config = this
        registerActivityLifecycleCallbacks(KtxActivityLifecycleCallbacks())
        BlueToothUtils.init(this)
    }

    //网络请求成功的code
    override fun getRspSuccessCode(): Int {
        return 0
    }

    override fun getCacheId(): String {
        return "mmkv_nkr"
    }

    override fun getTitleBarLeftIcon(): Drawable {
        return ContextCompat.getDrawable(this, R.mipmap.ic_left_black)!!
    }
}