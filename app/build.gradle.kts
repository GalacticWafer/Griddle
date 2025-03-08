import java.io.FileInputStream
import java.util.Properties

fun getLocalProperties(project: Project): Properties {
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use { fis ->
            properties.load(fis)
        }
    }
    return properties
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" // this version matches your Kotlin version
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

android {
    compileSdk = 35

    val majorVersion = 1
    val minorVersion = 1
    val iteration = 2
    defaultConfig {
        applicationId = "com.galacticware.keyboards.griddle"
        minSdk = 26
        targetSdk = 34
        versionCode = 53
        versionName = "$majorVersion.$minorVersion.$iteration"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val localProperties = getLocalProperties(project)
            keyAlias = localProperties.getProperty("KEY_ALIAS")
            keyPassword = localProperties.getProperty("KEY_PASSWORD")
            storeFile = file(localProperties.getProperty("GRIDDLE_KEYSTORE_PATH"))
            storePassword = localProperties.getProperty("KEY_STORE_PASSWORD")
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "Griddle-${variant.versionName}${if(variant.baseName == "release") "" else "(debug)"}.apk"
                println("OutputFileName: $outputFileName,")
                output.outputFileName = outputFileName
            }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
        }

        debug {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = true
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    namespace = "com.galacticware.griddle"
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
    //buildToolsVersion = "30.0.3"
    ndkVersion = "27.1.12297006"
}


dependencies {
    /**
     * IMPORTANT!!!!
     * Choose one of the following, but not both (or your own lib with some other IGestureDetector
     * implementation).
     */
    implementation(files("libs/messageasestylegesturedetector.aar"))
    //    implementation(files("libs/defaultgesturedetector.aar"))


    // Kotlin standard library
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.json)

    // AndroidX
    implementation(libs.androidx.appcompat)

    // Dagger-Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Gson
    implementation(libs.gson)
    implementation(libs.material)

    // Compose
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.common.java8)

    // Room
    // https://mvnrepository.com/artifact/androidx.room/room-compiler
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.testing)

    // Datastore
    implementation(libs.androidx.datastore.core.android)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // preferences datastore
    implementation(libs.androidx.preference.ktx)

    androidTestImplementation(libs.androidx.navigation.testing)

    // Shared preferences
    implementation(libs.androidx.preference.ktx.v111)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.splitties.views)
}