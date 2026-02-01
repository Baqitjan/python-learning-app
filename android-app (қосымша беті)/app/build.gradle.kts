
import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)


}

android {
    namespace = "com.example.mobileapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mobileapp"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

dependencies {
    // Сіздің барлық dependencies...
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // WorkManager
    implementation(libs.work.runtime.ktx)

    // Glide
    implementation(libs.glide)

    // CardView
    implementation(libs.cardview)

    // CircleImageView
    implementation(libs.circleimageview)

    // Sora Code Editor
    implementation(platform(libs.sora.editor.bom))
    implementation(libs.sora.editor.editor)
    implementation(libs.sora.editor.textmate)
    implementation(libs.gson)
    coreLibraryDesugaring(libs.desugar.jdk.libs)


    // Navigation Component
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Markwon
    implementation(libs.markwon.core)
    implementation(libs.markwon.image)
    implementation(libs.markwon.image.glide)
}
    