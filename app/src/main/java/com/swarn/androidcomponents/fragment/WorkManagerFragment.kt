package com.swarn.androidcomponents.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.background.EXTRA_OUTPUT_MESSAGE
import com.swarn.androidcomponents.background.MyWorkManager


/**
 * A simple [Fragment] subclass.
 *
 */
const val TASK_KEY = "TASK_KEY"

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

        startWorkManagerBtn = activity!!.findViewById(R.id.start_work_manager_btn)

        workManagerStatusTxtView = activity!!.findViewById(R.id.work_status_txt_view)

        val data = Data.Builder()
            .putString(TASK_KEY, "I am sending the data")
            .build()

        val workRequest = OneTimeWorkRequest.Builder(MyWorkManager::class.java)
            .setInputData(data)
            .build()

        startWorkManagerBtn.setOnClickListener {
            WorkManager.getInstance().enqueue(workRequest)
        }

        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, Observer {
                workManagerStatusTxtView.append(it.state.name + "\n")

                if (it != null && it.state.isFinished) {
                    workManagerStatusTxtView.append(it.outputData.getString(EXTRA_OUTPUT_MESSAGE))
                }
            })
    }

}
