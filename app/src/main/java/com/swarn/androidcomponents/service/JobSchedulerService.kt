package com.swarn.androidcomponents.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import timber.log.Timber

class JobSchedulerService : JobService() {

    private val TAG = JobSchedulerService::class.java.canonicalName
    private var isJobCancelled = false

    override fun onStartJob(params: JobParameters?): Boolean {
        Timber.d(TAG, "Job Started on Thread ID : ${Thread.currentThread().id}")
        doBackgroundWork(params)

        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Timber.d(TAG, "Job Cancelled before completion")
        isJobCancelled = true
        return false
    }

    private fun doBackgroundWork(params: JobParameters?) {

        Thread(Runnable {
            Timber.d(TAG, "Job Running on Thread ID : ${Thread.currentThread().id}")

            for (i in 1..10) {
                Timber.d(TAG, " run : $i")

                if (isJobCancelled) return@Runnable

                Thread.sleep(1000)
            }

            Timber.d(TAG, " Job Finished")
            jobFinished(params, false)
        }).start()
    }
}
