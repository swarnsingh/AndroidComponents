package com.swarn.androidcomponents.service

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityRecognitionResult
import com.swarn.androidcomponents.fragment.TRANSITION_RECEIVER


class ActivityRecognitionIntentService : IntentService("ActivityRecognitionIntentService") {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onHandleIntent(intent: Intent?) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            if (result != null) {

                val i = Intent(TRANSITION_RECEIVER)
                val bundle = Bundle()
                bundle.putParcelable(TRANSITION_RECEIVER, result)
                i.putExtras(bundle)
                sendBroadcast(i)
            }
        }
    }
}
