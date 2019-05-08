package com.swarn.androidcomponents.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.service.MyJobIntentService


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
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

            Log.d(
                JobIntentServiceFragment::class.java.canonicalName,
                "onNewJob - Current Thread Id : " + Thread.currentThread().id
            )
            MyJobIntentService.enqueueWork(activity!!, serviceIntent)
        }
    }
}
