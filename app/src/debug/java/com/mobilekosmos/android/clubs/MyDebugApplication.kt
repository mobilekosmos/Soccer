package com.mobilekosmos.android.clubs

import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger

class MyDebugApplication : MyApplication(), ImageLoaderFactory {

    // TODO: Only use in debug builds, TAG used in Logcat: RealImageLoader
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .logger(DebugLogger())
            .build()
    }
}