object Versions {
    val versionName = "1.0.0" // X.Y.Z; X = Major, Y = minor, Z = Patch level
    private val versionCodeBase = 70150 // XYYZZM; M = Module (tv, mobile)
    val versionCodeMobile = versionCodeBase + 3

    const val COMPILE_SDK = 33
    const val TARGET_SDK = 33
    const val MIN_SDK = 29

    const val ANDROID_GRADLE_PLUGIN = "7.2.0"
    const val BENCHMARK = "1.1.0-rc02"
    const val COMPOSE = "1.1.1"
    const val FIREBASE_CRASHLYTICS = "2.3.0"
    const val GOOGLE_SERVICES = "4.3.3"
    const val HILT_AGP = "2.42"
    const val KOTLIN = "1.7.10"
    const val NAVIGATION = "2.4.1"
    const val PROFILE_INSTALLER = "1.2.0-beta01"

    // TODO: Remove this once the version for
    //  "org.threeten:threetenbp:${Versions.threetenbp}:no-tzdb" using java-platform in the
    //  depconstraints/build.gradle.kts is defined
    const val THREETENBP = "1.3.6"
}
