// Top-level build file where you can add configuration options common to all sub-projects/modules.
// TODO FIX: Duplicate content roots detected
//			Path [C:/Workspace/Github/Soccer/app/build/generated/source/kapt/greenFlavorDebug] of module [Soccer.app.main] was removed from modules [Soccer.app.main]"
//			Also 2 more paths were deduplicated. See idea log for details
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.ANDROID_GRADLE_PLUGIN}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.HILT_AGP}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}