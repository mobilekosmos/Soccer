package com.mobilekosmos.android.clubs

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Attention: before deleting this class, since we have a custom application class in the debug folder
// we want to make sure we don't forget to extend from this class if used in the future.
@HiltAndroidApp
open class MyApplicationHilt : Application()
