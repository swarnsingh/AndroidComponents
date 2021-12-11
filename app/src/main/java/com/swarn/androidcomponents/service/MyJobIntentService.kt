package com.swarn.androidcomponents.service

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.SystemClock
import android.widget.Toast
import androidx.core.app.JobIntentService
import timber.log.Timber


object MyJobIntentService : JobIntentService() {

    private val TAG = MyJobIntentService::class.java.canonicalName


    fun enqueueWork(context: Context, work: Intent) {
        enqueueWork(context, MyJobIntentService::class.java, 123, work)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d(TAG, "onCreate");
    }

    override fun onHandleWork(intent: Intent) {
        Timber.d(TAG, "onHandleWork")

        val input = intent.getStringExtra("inputExtra")

        for (i in 0..9) {
            Timber.d(
                TAG,
                "onHandleWork $input - $i : Current Thread Id - " + Thread.currentThread().id
            )

            if (isStopped) return

            SystemClock.sleep(1000)
        }
    }

    override fun onStopCurrentWork(): Boolean {
        Timber.d(TAG, "onStopCurrentWork")
        return super.onStopCurrentWork()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d(TAG, "onDestroy")

        val mHandler = Handler()
        mHandler.post {
            Toast.makeText(this, "Job completed", Toast.LENGTH_LONG).show()
        }
    }
}
