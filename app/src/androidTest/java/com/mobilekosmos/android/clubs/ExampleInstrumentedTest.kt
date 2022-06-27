package com.mobilekosmos.android.clubs

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    // TODO: Write Espresso tests.

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals(
            "com.mobilekosmos.android.weather",
            ApplicationProvider.getApplicationContext<Context>().packageName
        )
    }
}
