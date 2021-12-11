package com.swarn.androidcomponents.fragment.service


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.service.MyJobIntentService
import timber.log.Timber


class JobIntentServiceFragment : androidx.fragment.app.Fragment() {

    private lateinit var inputEditText: EditText
    private lateinit var startServiceBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_intent, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        inputEditText = activity!!.findViewById(R.id.edit_text_input)

        startServiceBtn = activity!!.findViewById(R.id.start_service_btn)

        startServiceBtn.setOnClickListener {
            val input = inputEditText.text.toString()

            val serviceIntent = Intent(activity!!, MyJobIntentService::class.java)
            serviceIntent.putExtra("inputExtra", input)

            Timber.d(
                JobIntentServiceFragment::class.java.canonicalName,
                "onNewJob - Current Thread Id : " + Thread.currentThread().id
            )
            MyJobIntentService.enqueueWork(activity!!, serviceIntent)
        }
    }
}
