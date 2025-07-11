plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mobileproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mobileproject"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.generativeai)
    implementation(libs.guava)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.play.services.mlkit.barcode.scanning)
    implementation(libs.recyclerview)
    implementation(libs.media3.common)
    implementation(libs.swiperefreshlayout)
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-ai")
    implementation("com.google.firebase:firebase-analytics")
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    implementation("com.google.android.material:material:<latest_version>")
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")



}