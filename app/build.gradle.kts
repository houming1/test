plugins {
    id(PluginIds.application)
    kotlin(PluginIds.kotlinAndroid)
    id(PluginIds.kotlinParcelize)
    id(PluginIds.kotlinKapt)
    id("kotlin-android")
}

android {

    signingConfigs {
        getByName("debug") {
            storeFile = file("home.jks")
            storePassword = "home123456"
            keyPassword = "home123456"
            keyAlias = "home"
        }
    }
    compileSdkVersion(Version.compileSdkVersion)

    defaultConfig {
        applicationId("com.powercore.hydrahome")
        minSdkVersion(Version.minSdkVersion)
        targetSdkVersion(Version.targetSdkVersion)
        versionCode = Version.versionCode
        versionName = Version.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true

//        ndk {
//            // 设置支持的SO库架构
//            abiFilters.add("armeabi-v7a")  //'armeabi', 'x86', 'armeabi-v7a', 'x86_64', 'arm64-v8a'
//        }

    }


    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    dexOptions {
        jumboMode = true
    }

    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    packagingOptions {
        exclude("META-INF/proguard/coroutines.pro")
    }

    configurations.all {
        resolutionStrategy {
            force(Deps.Kotlin.stdLib)
        }
    }
    kapt {
        correctErrorTypes = true
    }
}

// 输出文件
android.applicationVariants.all {
    // 编译类型
    val buildType = buildType.name
    outputs.all {
        // 输出 Apk
        if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
            if (buildType == "debug") {
                this.outputFileName =
                    "HomeCharger_V${android.defaultConfig.versionName}_${Deploy.getSystem()}_D.apk"
            } else if (buildType == "release") {
                this.outputFileName =
                    "HomeCharger_V${android.defaultConfig.versionName}_${Deploy.getSystem()}_R.apk"
            }
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.annotation:annotation:1.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    testImplementation(Deps.Test.junit)
    androidTestImplementation(Deps.Test.junitExt)
    androidTestImplementation(Deps.Test.espresso)

//    implementation(Deps.Room.room_runtime)
//    kapt(Deps.Room.room_compiler)
//    implementation(Deps.Room.room_ktx)

    implementation(Deps.DataStore.preferences)
    implementation(Deps.DataStore.core)

    kapt(Deps.Moshi.moshi_kotlin_codegen)

    //图标库
    implementation(Deps.chart)
    //webSocket
    implementation(Deps.webSocket)
    //蓝牙
    implementation(Deps.blueTooth)
    //下拉刷新
    implementation(Deps.SmartRefresh.refresh)
    implementation(Deps.SmartRefresh.refreshHeader)
    //下拉选项
    implementation(Deps.spinner)
    //通用弹窗（pop）
    implementation(Deps.xpop)
    implementation(Deps.xpopext)
    //图片选择
    implementation(Deps.imgPicker)
    //二维码
    implementation(Deps.qrCode)
    //日历控件
    implementation(Deps.calenderView)
    coreLibraryDesugaring(Deps.desugarJdkLibs)
    //指示器
    implementation(Deps.magicIndicator)
    //wheelView
    implementation(Deps.wheelView)
    //baseLib 基础功能
    implementation(project(mapOf("path" to ":libBase")))

    implementation(project(mapOf("path" to ":blufilibrary")))

    //googlePlay
    implementation(Deps.googlePlay)
    //banner
    implementation(Deps.banner)

    //swipeDelMenuLayout
    implementation(Deps.swipeDelMenuLayout)

    implementation(Deps.pageindicatorview)

    implementation(Deps.switchbtn)

    implementation(Deps.shadowlayout)
    //implementation(Deps.blufi)
    implementation(Deps.glide)
    annotationProcessor(Deps.glideCompiler)
}