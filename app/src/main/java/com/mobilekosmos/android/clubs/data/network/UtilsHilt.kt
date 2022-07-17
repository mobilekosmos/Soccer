package com.mobilekosmos.android.clubs.data.network

import android.net.ConnectivityManager
import javax.inject.Singleton

@Singleton
class UtilsHilt (private val connectivityManager: ConnectivityManager) {
    // TODO: replace deprecated code.
    fun isNetworkAvailable(): Boolean {
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}
