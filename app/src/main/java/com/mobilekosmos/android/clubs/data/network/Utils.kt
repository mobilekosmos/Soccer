package com.mobilekosmos.android.clubs.data.network

import android.content.Context
import android.net.ConnectivityManager
import com.mobilekosmos.android.clubs.MyApplication


class Utils {
    companion object {
        // TODO: replace deprecated code.
        fun isNetworkAvailable(): Boolean {
            val connectivityManager = MyApplication.getApplicationContext()
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }
}
