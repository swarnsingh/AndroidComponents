package com.swarn.androidcomponents.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * @author Swarn Singh.
 */
object ConnectivityManager {

    @SuppressLint("ServiceCast")
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork!!.isConnected
    }
}