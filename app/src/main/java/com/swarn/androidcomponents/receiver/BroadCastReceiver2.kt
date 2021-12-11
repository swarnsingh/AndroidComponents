package com.swarn.androidcomponents.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import timber.log.Timber


class BroadCastReceiver2 : BroadcastReceiver() {

    private val TAG = BroadCastReceiver2::class.java.simpleName

    private val BREAD_CRUMB = "Breadcrumb"

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = getResultExtras(true)
        var trail = bundle.getString(BREAD_CRUMB)
        trail = if (trail == null) "Start->$TAG" else "$trail->$TAG"
        bundle.putString(BREAD_CRUMB, trail)
        Timber.i(TAG, "BroadCastReceiver2 triggered: $trail")
    }
}
