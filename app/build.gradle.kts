plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Keep only the necessary plugins
}

android {
    namespace = "com.example.cyclingtracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cyclingtracker"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 34
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
    buildFeatures {
        viewBinding =true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // For Google Maps
    implementation(libs.google.maps)

    // For Location
    implementation(libs.google.location)

    // Firebase BOM (Bill of Materials)
    implementation(platform(libs.firebase.bom))

    // Firebase Realtime Database
    implementation(libs.firebase.database)

    // Firebase Analytics
    implementation(libs.firebase.analytics)
    implementation(libs.play.services.ads)
    implementation(libs.osmdroid.android)




    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
