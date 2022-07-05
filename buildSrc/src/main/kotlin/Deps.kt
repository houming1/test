/**
 *    Created by Administrator on 2021/11/10.
 *    Desc :
 */
object Deps {

    object GradlePlugin {
        const val gradle = "com.android.tools.build:gradle:${Version.gradleToolVersion}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlinVersion}"
        const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Version.hiltVersion}"
    }

    object Kotlin {
        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlinVersion}"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Version.kotlinVersion}"
        const val stdlib_jdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Version.kotlinVersion}"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val junitExt = "androidx.test.ext:junit:1.1.2"
        const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
    }


    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.1"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
        const val viewpager2 = "androidx.viewpager2:viewpager2:1.0.0"
        const val material = "com.google.android.material:material:1.4.0"
        const val flexBox = "com.google.android:flexbox:2.0.1"
        const val coreKtx = "androidx.core:core-ktx:1.3.2"
        const val activityKtx = "androidx.activity:activity-ktx:1.2.2"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.3"
        const val cardView = "androidx.cardview:cardview:1.0.0"
        const val multidex = "androidx.multidex:multidex:2.0.1"
    }

    /**
     * 协程
     */
    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3"
        const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.3"
    }

    object Lifecycle {
        const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.3.1"
        const val commonJava8 = "androidx.lifecycle:lifecycle-common-java8:2.3.1"
        const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.1"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1"
        const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
    }

    object Navigation {
        const val runtimeKtx = "androidx.navigation:navigation-runtime-ktx:2.3.5"
        const val commonKtx = "androidx.navigation:navigation-common-ktx:2.3.5"
        const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:2.3.5"
        const val uiKtx = "androidx.navigation:navigation-ui-ktx:2.3.5"
    }

    object Startup {
        const val runtime = "androidx.startup:startup-runtime:1.0.0"
    }

    object Moshi {
        const val moshi = "com.squareup.moshi:moshi:1.12.0"
        const val moshi_kotlin_codegen = "com.squareup.moshi:moshi-kotlin-codegen:1.12.0"
    }

    object Retrofit2 {
        const val converter_moshi = "com.squareup.retrofit2:converter-moshi:2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"

        //        const val converterGson = "com.squareup.retrofit2:converter-gson:2.9.0"
        const val adapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"

        // 动态替换 BaseUrl 库：https://github.com/JessYanCoding/RetrofitUrlManager
        const val urlManager = "me.jessyan:retrofit-url-manager:1.4.0"

    }

    object OkHttp3 {
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:3.4.0"

        /** Cookies 自动持久化 */
        const val persistentCookieJar = "com.github.franmontiel:PersistentCookieJar:v1.0.1"
    }

    object Coil {
        const val coil = "io.coil-kt:coil:1.2.0"
        const val gif = "io.coil-kt:coil-gif:1.2.0"
        const val svg = "io.coil-kt:coil-svg:1.2.0"
        const val video = "io.coil-kt:coil-video:1.2.0"
        const val transformations = "com.github.Commit451.coil-transformations:transformations:1.0.0"
        const val transformationsGpu = "com.github.Commit451.coil-transformations:transformations-gpu:1.0.0"
        const val transformationsFaceDetection =
            "com.github.Commit451.coil-transformations:transformations-face-detection:1.0.0"
    }

    object Room {
        const val room_runtime = "androidx.room:room-runtime:2.2.6"
        const val room_compiler = "androidx.room:room-compiler:2.2.6"
        const val room_ktx = "androidx.room:room-ktx:2.2.6"
    }

    object SmartRefresh {
        const val refresh = "com.scwang.smart:refresh-layout-kernel:2.0.3"
        const val refreshHeader = "com.scwang.smart:refresh-header-classics:2.0.3"
        const val refreshFooter = "com.scwang.smart:refresh-footer-classics:2.0.3"
    }

    object DataStore {
        // Preferences DataStore（可以直接使用）
        const val preferences = "androidx.datastore:datastore-preferences:1.0.0-alpha05"

        // Preferences DataStore （没有Android依赖项，包含仅适用于 Kotlin 的核心 API）
        const val core = "androidx.datastore:datastore-preferences-core:1.0.0-alpha05"
    }

    object Immersionbar {
        //状态栏
        const val immersionbar = "com.gyf.immersionbar:immersionbar:3.0.0"
        const val immersionbar_ktx = "com.gyf.immersionbar:immersionbar-ktx:3.0.0"
    }


    object Hilt {
        const val hilt_android = "com.google.dagger:hilt-android:${Version.hiltVersion}"
        const val hilt_android_compiler = "com.google.dagger:hilt-android-compiler:${Version.hiltVersion}"
    }

    //今日头条屏幕适配
    const val autoSize = "me.jessyan:autosize:1.2.1"

    //利用LiveData发送消息
    const val unPeekLiveData = "com.kunminx.archi:unpeek-livedata:4.5.0-beta1"

    //各种圆角
    const val RWidgetHelper = "com.ruffian.library:RWidgetHelper:1.1.18"

    //日志
    const val logger = "com.orhanobut:logger:2.2.0"

    //titleBar
    const val titleBar = "com.github.getActivity:TitleBar:9.2"

    //liveDataBus
    const val liveDataBus = "io.github.jeremyliao:live-event-bus-x:1.8.0"

    //utilCodex
    const val utilCodex = "com.blankj:utilcodex:1.31.0"

    const val adapter = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4"

    const val permissionX = "com.guolindev.permissionx:permissionx:1.6.1"

    //底部导航依赖
    const val bottomNavEx = "com.github.ittianyu:BottomNavigationViewEx:2.0.4"

    //图表库
    const val chart = "com.github.PhilJay:MPAndroidChart:v3.1.0"

    const val mmkv = "com.tencent:mmkv:1.2.11"

    //WebSocket
    const val webSocket = "org.java-websocket:Java-WebSocket:1.5.2"

    //蓝牙
    const val blueTooth = "com.inuker.bluetooth:library:1.4.0"

    //下拉
    const val spinner = "com.github.skydoves:powerspinner:1.1.9"

    //xpop
    const val xpop = "com.github.li-xiaojun:XPopup:2.7.6"
    const val xpopext = "com.github.li-xiaojun:XPopupExt:0.0.8"

    //图片选择
    const val imgPicker = "com.ypx.yimagepicker:androidx:3.1.4"

    //二维码
    const val qrCode = "com.github.jenly1314:zxing-lite:2.1.1"//AndroidX 版本

    //日期选择器
    const val calenderView = "com.haibin:calendarview:3.7.1"
    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:1.1.5"

    //指示器
    const val magicIndicator = "com.github.hackware1993:MagicIndicator:1.7.0"

    //wheelVIew
    const val wheelView = "com.github.duanhong169:picker-view:1.0.0"

    //googlePlay
    const val googlePlay = "com.google.android.play:core-ktx:1.7.0"

    const val flowLayout = "com.nex3z:flow-layout:1.3.3"

    //banner
    const val banner = "io.github.youth5201314:banner:2.2.2"

    //    侧滑删除
    const val swipeDelMenuLayout = "com.github.mcxtzhang:SwipeDelMenuLayout:V1.3.0"

    const val pageindicatorview = "com.romandanylyk:pageindicatorview:1.0.3"

    const val switchbtn = "com.kyleduo.switchbutton:library:2.1.0"

    const val blufi = "com.github.EspressifApp:lib-blufi-android:2.3.4"

    const val shadowlayout ="com.github.lihangleo2:ShadowLayout:3.2.4"

    const val glide = "com.github.bumptech.glide:glide:4.13.0"
    const val glideCompiler = "com.github.bumptech.glide:compiler:4.13.0"
}