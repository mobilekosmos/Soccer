package com.mobilekosmos.android.clubs

import android.app.Application
import android.content.Context

// Attention: before deleting this class, since we have a custom application class in the debug folder
// we want to make sure we don't forget to extend from this class if used in the future.
// TODO: The warning seem to be a false positive: need to check in a release build if true.
open class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // There are many alternatives to saving the application context here, but for simplicity
        // and this demo app we use this approach. The only issue with this approach would be if there is static initialization
        // somewhere referencing this var while onCreate() was not called yet, which is at least in our current
        // code state not the case.
        // TODO: maybe replace by an "safer" approach like this one: https://bladecoder.medium.com/kotlin-singletons-with-argument-194ef06edd9e
        //  but that approach also looks like an overkill.
        instance = this
    }

    companion object {
        // We use this because we need a context in the retrofit singleton.
        lateinit var instance: MyApplication
            private set

        fun getApplicationContext(): Context? {
            return instance.applicationContext
        }
    }
}
