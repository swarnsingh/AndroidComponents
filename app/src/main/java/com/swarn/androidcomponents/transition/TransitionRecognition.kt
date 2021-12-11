package com.swarn.androidcomponents.transition

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognition
import com.swarn.androidcomponents.service.ActivityRecognitionIntentService
import timber.log.Timber

/**
 * @author Swarn Singh.
 */

object TransitionRecognition : TransitionRecognitionAbstract() {
    private val TAG = TransitionRecognition::class.java.simpleName

    override fun startTracking(context: Context) {
        launchTransitionsTracker(context)
    }

    override fun stopTracking(context: Context) {
        val intent = Intent(context, ActivityRecognitionIntentService::class.java)
        val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (pendingIntent != null) {
            ActivityRecognition.getClient(context).removeActivityUpdates(pendingIntent)
                .addOnSuccessListener {
                    Timber.d(
                        TAG,
                        "De registered for activity transition updates"
                    )
                    pendingIntent.cancel()
                }
                .addOnFailureListener { e ->
                    Timber.e(
                        TAG,
                        "Transitions could not be unregistered: $e"
                    )
                }
        }
    }

    private fun launchTransitionsTracker(context: Context) {
        val intent = Intent(context, ActivityRecognitionIntentService::class.java)
        val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        ActivityRecognition.getClient(context).apply {
            requestActivityUpdates(3000L, pendingIntent)
                .addOnSuccessListener {
                    Timber.d(TAG, "requestActivityUpdates addOnSuccessListener")
                }

                .addOnFailureListener {
                    Timber.d(TAG, "requestActivityUpdates addOnFailureListener : ${it.localizedMessage}")
                }
        }
    }
}
