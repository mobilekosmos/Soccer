package com.mobilekosmos.android.clubs.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.mobilekosmos.android.clubs.MyApplication

class NetworkUtils {

    // TODO: there are too many opinions on the internet how this should look and the official Google IO code still uses a deprecated code, so we stick with that till Google publishes a new sample code.
//    fun isNetworkAvailable(applicationContext: Context): Boolean {
//        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
//        val capabilities = manager?.getNetworkCapabilities(manager.activeNetwork) ?: return false
//        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
//                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
//                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
//                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
//    }

    fun isNetworkAvailable(applicationContext: Context): Boolean {
        val connectivityManager = applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}