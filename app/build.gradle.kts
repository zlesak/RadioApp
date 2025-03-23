plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("io.objectbox")
}

android {
    namespace = "cz.uhk.fim.zlesak.radioapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "cz.uhk.fim.zlesak.radioapp"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.gms.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    //Datastore
    implementation(libs.objectbox.android)
    implementation(libs.androidx.datastore.preferences)
    //AsyncImage
    implementation(libs.coil.compose)

    //Icons
    implementation(libs.androidx.material.icons.extended)
    //Location
    implementation(libs.play.services.location)
}