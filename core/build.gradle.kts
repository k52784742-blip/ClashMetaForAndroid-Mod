plugins {
    kotlin("android")
    id("com.android.library")
    id("kotlinx-serialization")
}

// golang-android plugin removed - using pre-built libmihomo.so in app/src/main/jniLibs

android {
    // golang cmake arguments removed - using pre-built .so
}

dependencies {
    implementation(project(":common"))

    implementation(libs.androidx.core)
    implementation(libs.kotlin.coroutine)
    implementation(libs.kotlin.serialization.json)
}