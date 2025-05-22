plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.almadistefano.finantrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.almadistefano.finantrack"
        minSdk = 27
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
        viewBinding = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.filament.android)
    implementation(libs.google.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Coroutines (para llamadas asíncronas)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.glide)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.retrofit.v2100)
    implementation(libs.converter.gson.v2100)

    // Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.room.compiler)



    //Libreria graficos
    implementation (libs.mpandroidchart)
    implementation(libs.androidx.recyclerview)
    implementation(libs.logging.interceptor)



}
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}3