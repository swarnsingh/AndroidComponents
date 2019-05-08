package com.swarn.androidcomponents.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import java.util.*


/**
 * @author Swarn Singh.
 */
class MyService : Service() {

    private var mRandomNumber: Int = 0
    private var mIsRandomGeneratorOn: Boolean = false

    private val MIN = 1
    private val MAX = 100

    private val TAG = MyService::class.java.canonicalName

    val GET_COUNT = 0

    private val mBinder = MyServiceBinder()

    private val randomNumberMessenger = Messenger(RandomNumberRequestHandler())

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return mBinder
    }


    inner class MyServiceBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }

    private fun startRandomNumberGenerator() {
        while (mIsRandomGeneratorOn) {
            try {
                Thread.sleep(2000)
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
        return mRandomNumber
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRandomNumberGenerator()
        Log.d(TAG, "onServiceDestroy : Thread Id : " + Thread.currentThread().id)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "on Import Fragment : Thread Id : " + Thread.currentThread().id)

        //stopSelf()
        mIsRandomGeneratorOn = true
        Thread(Runnable { startRandomNumberGenerator() }).start()
        return START_STICKY
    }

    @SuppressLint("HandlerLeak")
    private inner class RandomNumberRequestHandler : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(TAG, "Message intercepted")
            when (msg.what) {
                GET_COUNT -> {
                    val messageSendRandomNumber = Message.obtain(null, GET_COUNT)
                    messageSendRandomNumber.arg1 = getRandomNumber()
                    try {
                        Log.i(TAG, "Replaying with random number to requester")
                        msg.replyTo.send(messageSendRandomNumber)
                    } catch (e: RemoteException) {
                        Log.i(TAG, "" + e.message)
                    }

                }
            }
            super.handleMessage(msg)
        }
    }

}