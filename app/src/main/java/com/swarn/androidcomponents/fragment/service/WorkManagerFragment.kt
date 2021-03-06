package com.swarn.androidcomponents.fragment.service


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.background.EXTRA_OUTPUT_MESSAGE
import com.swarn.androidcomponents.background.MyWorkManager


/**
 * A simple [Fragment] subclass.
 *
 */
const val TASK_KEY = "TASK_KEY"
const val REQUEST_SPEECH_RECOGNIZER = 500

class WorkManagerFragment : Fragment() {


    private lateinit var startWorkManagerBtn: Button

    private lateinit var workManagerStatusTxtView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work_manager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        startWorkManagerBtn = requireActivity().findViewById(R.id.start_work_manager_btn)

        workManagerStatusTxtView = requireActivity().findViewById(R.id.work_status_txt_view)

        val data = workDataOf(TASK_KEY to "I am sending the data")

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<MyWorkManager>()
            .setInputData(data)
            .setConstraints(constraints)
            .addTag(WorkManagerFragment::class.java.canonicalName.toString())
            .build()

        startWorkManagerBtn.setOnClickListener {
            WorkManager.getInstance(requireActivity().applicationContext).enqueue(workRequest)
        }

        WorkManager.getInstance(requireActivity().applicationContext)
            .getWorkInfoByIdLiveData(workRequest.id)
            .observe(viewLifecycleOwner, Observer {
                workManagerStatusTxtView.append(it.state.name + "\n")

                if (it != null && it.state.isFinished) {
                    workManagerStatusTxtView.append(it.outputData.getString(EXTRA_OUTPUT_MESSAGE))
                }
            })
    }
}
