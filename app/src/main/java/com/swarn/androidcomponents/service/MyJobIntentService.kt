package com.swarn.androidcomponents.service

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.JobIntentService
import android.util.Log


class MyJobIntentService : JobIntentService() {

    private val TAG = MyJobIntentService::class.java.canonicalName


    companion object {
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, MyJobIntentService::class.java, 123, work)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate");
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "onHandleWork")

        val input = intent.getStringExtra("inputExtra")

        for (i in 0..9) {
            Log.d(TAG, "onHandleWork $input - $i : Current Thread Id - " + Thread.currentThread().id)

            if (isStopped) return

            SystemClock.sleep(1000)
        }
    }

    override fun onStopCurrentWork(): Boolean {
        Log.d(TAG, "onStopCurrentWork")
        return super.onStopCurrentWork()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy");
    }
}
