package com.swarn.androidcomponents.service

import android.app.IntentService
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
class MyIntentService : IntentService("MyIntentService") {

    private var mRandomNumber: Int = 0
    private var mIsRandomGeneratorOn: Boolean = false

    private val MIN = 1
    private val MAX = 100

    private val TAG = MyIntentService::class.java.canonicalName

    val GET_COUNT = 0

    private val mBinder = MyServiceBinder()

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "on Import Fragment : Thread Id : " + Thread.currentThread().id)

        //stopSelf()
        mIsRandomGeneratorOn = true
        startRandomNumberGenerator()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return mBinder
    }

    inner class MyServiceBinder : Binder() {
        fun getService(): MyIntentService {
            return this@MyIntentService
        }
    }

    private fun startRandomNumberGenerator() {
        while (mIsRandomGeneratorOn) {
            try {
                Thread.sleep(1000)
                if (mIsRandomGeneratorOn) {
                    mRandomNumber = Random().nextInt(MAX) + MIN
                    Log.d(TAG, "on Thread id: " + Thread.currentThread().id + ", Random Number: " + mRandomNumber)
                }
            } catch (e: InterruptedException) {
                Log.d(TAG, "Thread Interrupted")
            }

        }
    }


    private fun stopRandomNumberGenerator() {
        mIsRandomGeneratorOn = false
    }

    fun getRandomNumber(): Int {
        return if (mRandomNumber != 0) {
            mRandomNumber
        } else {
            Random().nextInt(MAX) + MIN
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRandomNumberGenerator()
        Log.d(TAG, "onServiceDestroy : Thread Id : " + Thread.currentThread().id)
    }

}
