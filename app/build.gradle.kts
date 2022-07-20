plugins {
    kotlin("android")
    kotlin("kapt")
    id("com.android.application")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Versions.COMPILE_SDK
    defaultConfig {
        applicationId = "com.mobilekosmos.android.clubs"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = Versions.versionCodeMobile
        versionName = Versions.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            // TODO: before release, must make sure all work with these flags.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        dataBinding = true
    }
    flavorDimensions.add("market")
    productFlavors {
        create("greenFlavor") {
            dimension = "market"
            buildConfigField(
                "String",
                "HOST_URL",
                "\"https://62b9bbd641bf319d22841c93.mockapi.io/api/v1/clubs_europe\""
            )
        }
        // The new flavor, redFlavor, needs to be a different app. Therefore, it needs a different app ID.
        create("redFlavor") {
            dimension = "market"
            applicationId = "com.mobilekosmos.android.clubs2"
            buildConfigField(
                "String",
                "HOST_URL",
                "\"https://62b9bbd641bf319d22841c93.mockapi.io/api/v1/clubs_southamerica\""
            )
        }
    }
}

// TODO: Move all dependencies to Libs.kt or at least the versions.
//  why does the Google IO App declares only a few dependencies in Libs and all others in another place?
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN}")
    implementation("androidx.appcompat:appcompat:1.6.0-alpha05")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha03")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("io.coil-kt:coil:2.1.0")

    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.9")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")

    implementation("androidx.fragment:fragment-ktx:1.5.0")
    implementation("androidx.activity:activity-ktx:1.5.0")
    implementation("androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}")
    implementation("androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0")

    // Dagger Hilt
    implementation(Libs.HILT_ANDROID)
    androidTestImplementation(Libs.HILT_TESTING)
    kapt(Libs.HILT_COMPILER)
    kaptAndroidTest(Libs.HILT_COMPILER)

    testImplementation(Libs.JUNIT)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha07")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
}
