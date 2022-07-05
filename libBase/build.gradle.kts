plugins {
    id(PluginIds.library)
    kotlin(PluginIds.kotlinAndroid)
    id(PluginIds.kotlinParcelize)
    id(PluginIds.kotlinKapt)
}

android {
    compileSdkVersion(Version.compileSdkVersion)

    defaultConfig {
        minSdkVersion(Version.minSdkVersion)
        targetSdkVersion(Version.targetSdkVersion)
        versionCode = Version.versionCode
        versionName = Version.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
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

dependencies {
    api(fileTree("libs"))

    testImplementation(Deps.Test.junit)
    androidTestImplementation(Deps.Test.junitExt)
    androidTestImplementation(Deps.Test.espresso)

    api(project(mapOf("path" to ":libNet")))

    //AndroidX
    api(Deps.AndroidX.coreKtx)
    api(Deps.AndroidX.appcompat)
    api(Deps.AndroidX.constraintLayout)
    api(Deps.AndroidX.material)
    api(Deps.AndroidX.recyclerView)
    api(Deps.AndroidX.viewpager2)
    api(Deps.AndroidX.activityKtx)
    api(Deps.AndroidX.fragmentKtx)
    api(Deps.AndroidX.flexBox)
    api(Deps.AndroidX.cardView)
    api(Deps.AndroidX.multidex)

    api(Deps.autoSize) {
        exclude(group = "com.android.support")
    }

    implementation(Deps.Room.room_runtime)
    kapt(Deps.Room.room_compiler)
    implementation(Deps.Room.room_ktx)

    implementation(Deps.DataStore.preferences)
    implementation(Deps.DataStore.core)

    api(Deps.Coroutines.android)
    api(Deps.Coroutines.core)
    api(Deps.Coroutines.coreJvm)

    api(Deps.Lifecycle.runtimeKtx)
    api(Deps.Lifecycle.commonJava8)
    api(Deps.Lifecycle.liveDataKtx)
    api(Deps.Lifecycle.viewModelKtx)
    api(Deps.Lifecycle.extensions)

    api(Deps.titleBar)
    //圆角view
    api(Deps.RWidgetHelper) {
        exclude(group = "com.android.support")
    }

    api(Deps.utilCodex)
    //imageloader
    api(Deps.Coil.coil)
    api(Deps.Coil.transformations)

    api(Deps.unPeekLiveData)

    api(Deps.autoSize)

    api(Deps.logger)

    api(Deps.adapter)

    api(Deps.permissionX)

    api(Deps.Immersionbar.immersionbar)
    api(Deps.Immersionbar.immersionbar_ktx)

    api(Deps.mmkv)
}