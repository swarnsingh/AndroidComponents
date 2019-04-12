package com.swarn.androidcomponents.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class PowerConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.filterEquals(Intent("android.intent.action.ACTION_POWER_CONNECTED"))) {
            Toast.makeText(context, "Power connected", Toast.LENGTH_SHORT).show()
        } else if (intent.filterEquals(Intent("android.intent.action.ACTION_POWER_DISCONNECTED"))) {
            Toast.makeText(context, "Power Disconnected", Toast.LENGTH_SHORT).show()
        }
    }
}
