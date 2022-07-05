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
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}
