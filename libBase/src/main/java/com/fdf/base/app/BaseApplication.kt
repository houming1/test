package com.fdf.base.app

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDexApplication
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import coil.util.CoilUtils
import coil.util.DebugLogger
import com.fdf.base.BuildConfig
import com.hjq.bar.TitleBar
import com.hjq.bar.style.LightBarStyle
import com.jeremyliao.liveeventbus.LiveEventBus
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.mmkv.MMKV
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.utils.ScreenUtils
import okhttp3.Dispatcher
import okhttp3.OkHttpClient


/**
 *    Created by Administrator on 2021/11/10.
 *    Desc :
 */

//全局上下文
val appContext: BaseApplication by lazy { BaseApplication.instance }

val appViewModelProvider: ViewModelProvider by lazy { BaseApplication.viewModelProvider }

//屏幕宽度
val screenWidth: Int by lazy { ScreenUtils.getScreenSize(appContext)[0] }

//屏幕高度
val screenHeight: Int by lazy { ScreenUtils.getScreenSize(appContext)[1] }

//状态栏高度
val statusBarHeight: Int by lazy { ScreenUtils.getStatusBarHeight() }


open class BaseApplication : MultiDexApplication(), ViewModelStoreOwner, ImageLoaderFactory {

    companion object {
        lateinit var instance: BaseApplication
        lateinit var viewModelProvider: ViewModelProvider
    }

    private var mFactory: ViewModelProvider.Factory? = null
    private lateinit var mAppViewModelStore: ViewModelStore

    override fun onCreate() {
        super.onCreate()
        instance = this
        mAppViewModelStore = ViewModelStore()
        viewModelProvider = getAppViewModelProvider()
        init()
    }

    private fun init() {
        TitleBar.setDefaultStyle(LightBarStyle())
        initAutoSize()
        Logger.addLogAdapter(AndroidLogAdapter())
        MMKV.initialize(this)
        LiveEventBus
            .config()
            .autoClear(false)
            .enableLogger(BuildConfig.DEBUG)
            .lifecycleObserverAlwaysActive(true);
    }

    private fun initAutoSize() {
        AutoSize.initCompatMultiProcess(this)
        AutoSizeConfig.getInstance()
            .setExcludeFontScale(true)
            .setUseDeviceSize(true)
            .setAutoAdaptStrategy { target, activity ->

            }
    }

    /**
     *获取一个全局的ViewModel
     */
    private fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, this.getAppFactory())
    }

    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore
    }

    private fun getAppFactory(): ViewModelProvider.Factory {
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mFactory as ViewModelProvider.Factory
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .availableMemoryPercentage(0.25) // Use 25% of the application's available memory.
            .bitmapPoolPercentage(0.25)
            .crossfade(true) // Show a short crossfade when loading images from network or disk.
            .crossfade(200)
            .okHttpClient {
                // Create a disk cache with "unlimited" size. Don't do this in production.
                // To create the an optimized Coil disk cache, use CoilUtils.createDefaultCache(context).
                val cache = CoilUtils.createDefaultCache(this)
                // Don't limit concurrent network requests by host.
                val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

                // Lazily create the OkHttpClient that is used for network operations.
                OkHttpClient.Builder()
                    .cache(cache)
                    .dispatcher(dispatcher)
                    .build()
            }
            .apply {
                // Enable logging to the standard Android log if this is a debug build.
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger(Log.VERBOSE))
                }
            }
            .build()
    }
}