package com.swarn.androidcomponents.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class JobSchedulerService : JobService() {

    private val TAG = JobSchedulerService::class.java.canonicalName
    private var isJobCancelled = false

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Job Started on Thread ID : ${Thread.currentThread().id}")
        doBackgroundWork(params)

        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Job Cancelled before completion")
        isJobCancelled = true
        return false
    }

    private fun doBackgroundWork(params: JobParameters?) {

        Thread(Runnable {
            Log.d(TAG, "Job Running on Thread ID : ${Thread.currentThread().id}")

            for (i in 1..10) {
                Log.d(TAG, " run : $i")

                if (isJobCancelled) return@Runnable

                Thread.sleep(1000)
            }

            Log.d(TAG, " Job Finished")
            jobFinished(params, false)
        }).start()
    }

}
