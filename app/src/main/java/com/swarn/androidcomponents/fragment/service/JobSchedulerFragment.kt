package com.swarn.androidcomponents.fragment.service

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.service.JobSchedulerService
import java.util.concurrent.TimeUnit

/**
 * @author Swarn Singh.
 */

class JobSchedulerFragment : Fragment() {

    private lateinit var scheduleJobSchedulerBtn: Button
    private lateinit var stopJobSchedulerBtn: Button

    private val JOB_ID = 123;

    private val TAG = JobSchedulerFragment::class.java.canonicalName


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_scheduler, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        scheduleJobSchedulerBtn = activity!!.findViewById(R.id.schedule_job_btn)
        stopJobSchedulerBtn = activity!!.findViewById(R.id.stop_job_btn)

        scheduleJobSchedulerBtn.setOnClickListener {
            var componentName = ComponentName(requireActivity(), JobSchedulerService::class.java)
            var jobInfo = JobInfo.Builder(JOB_ID, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .build()

            var jobScheduler = activity!!.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            var resultCode = jobScheduler.schedule(jobInfo)

            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job Scheduled on Thread ID : ${Thread.currentThread().id}")
            } else {
                Log.d(TAG, "Job Scheduling Failed ")
            }
        }

        stopJobSchedulerBtn.setOnClickListener {
            var scheduler = activity!!.getSystemService(JobScheduler::class.java)
            scheduler.cancel(JOB_ID)
            Log.d(TAG, "Job Cancelled")
        }
    }
}