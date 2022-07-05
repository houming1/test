// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Deps.GradlePlugin.gradle)
        classpath(Deps.GradlePlugin.kotlin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
//        classpath(Deps.GradlePlugin.hilt)
    }
}

allprojects {
    repositories {
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/google") }
        maven { setUrl("https://maven.aliyun.com/repository/central") }
        maven { setUrl("https://jitpack.io") }
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}