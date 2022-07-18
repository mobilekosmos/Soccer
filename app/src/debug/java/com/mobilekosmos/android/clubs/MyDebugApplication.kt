package com.mobilekosmos.android.clubs

import android.os.StrictMode
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger

class MyDebugApplication : MyApplicationHilt(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        StrictMode.enableDefaults()
    }

    // TAG used in Logcat: RealImageLoader
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .logger(DebugLogger())
            .build()
    }
}