plugins {
    id(PluginIds.library)
    kotlin(PluginIds.kotlinAndroid)
    id(PluginIds.kotlinParcelize)
    id(PluginIds.kotlinKapt)
}
apply {
    plugin("kotlin-android")
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
    testImplementation(Deps.Test.junit)
    androidTestImplementation(Deps.Test.junitExt)
    androidTestImplementation(Deps.Test.espresso)

    api(Deps.Coroutines.android)
    api(Deps.Coroutines.core)
    api(Deps.Coroutines.coreJvm)

    api(Deps.Lifecycle.runtimeKtx)
    api(Deps.Lifecycle.commonJava8)
    api(Deps.Lifecycle.liveDataKtx)
    api(Deps.Lifecycle.viewModelKtx)
    api(Deps.Lifecycle.extensions)

    api(Deps.Retrofit2.retrofit)
    api(Deps.Retrofit2.adapter)
    api(Deps.Retrofit2.converter_moshi)
    api(Deps.Retrofit2.urlManager)

    api(Deps.OkHttp3.loggingInterceptor)

    api(Deps.liveDataBus)


    api(Deps.Moshi.moshi)
    kapt(Deps.Moshi.moshi_kotlin_codegen)

}