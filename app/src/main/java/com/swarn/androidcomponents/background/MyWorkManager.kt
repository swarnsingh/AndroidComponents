package com.swarn.androidcomponents.background

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.swarn.androidcomponents.fragment.service.TASK_KEY


/**
 * @author Swarn Singh.
 */
const val EXTRA_OUTPUT_MESSAGE: String = "EXTRA_OUTPUT_MESSAGE"
const val CHANNEL_ID = "MyChannelId"
const val CHANNEL_NAME = "MyChannel"
const val NOTIFICATION_ID = 1

class MyWorkManager(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val data = inputData
        displayNotification("My Worker", data.getString(TASK_KEY))

        val output = Data.Builder()
            .putString(EXTRA_OUTPUT_MESSAGE, "I have come from MyWorker!")
            .build()

        return Result.success(output)
    }


    private fun displayNotification(title: String, task: String?) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(task)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.sym_def_app_icon)

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }
}