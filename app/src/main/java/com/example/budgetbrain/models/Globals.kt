package com.example.budgetbrain.models

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Globals {
    var SessionUser: SessionUser? = null;
    fun clear() {
        SessionUser = null
    }
    var biometricsAvailable: Boolean = false;

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
}