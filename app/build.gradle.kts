plugins {
    alias(libs.plugins.android.application)
}

android {

    namespace = "com.app.cloudchat_volunteer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.cloudchat_volunteer"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters += setOf("armeabi", "armeabi-v7a", "arm64-v8a")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
    ndkVersion = "29.0.13113456 rc1"
}

dependencies {
    // 本地 aar
    implementation(fileTree("libs") {
        include("*.aar")
    })

    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.camera:camera-core:1.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-view:1.1.0")

    // 如果你在版本目录里定义了 v231 后缀，可以直接使用：
    implementation(libs.lifecycle.viewmodel.ktx.v231)
    implementation(libs.lifecycle.livedata.ktx.v231)

    // 也可以直接写常规依赖
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("com.google.guava:guava:31.1-jre")
}

