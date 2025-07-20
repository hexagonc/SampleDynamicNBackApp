plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.google.devtools.ksp)
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.evolved.automata.app.dynamicnback"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.evolved.automata.app.dynamicnback"
        minSdk = 30
        targetSdk = 35
        versionCode = 2
        versionName = "Dynamic NBack 0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("${project.findProperty("noesisKeystorelocation") ?: "DefaultValue"}")
            storePassword = "${project.findProperty("noesisOrgKeystorePass") ?: "DefaultValue"}"
            keyAlias = "${project.findProperty("noeticTimerKeyAlias") ?: "DefaultValue"}"
            keyPassword = "${project.findProperty("noeticTimerSigningKeyPass") ?: "DefaultValue"}"
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-debug"
        }

        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    val nav_version = "2.9.1"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")


    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Hilt + Compose integration
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // splash
    implementation("androidx.core:core-splashscreen:1.0.0")

    // gson

    implementation("com.google.code.gson:gson:2.11.0")

    // Add viewmodel lifecycle compose support for = viewModel()
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // For coroutine unit test support
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // For rememberScaffoldState

    implementation("androidx.compose.material:material:1.6.7")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}