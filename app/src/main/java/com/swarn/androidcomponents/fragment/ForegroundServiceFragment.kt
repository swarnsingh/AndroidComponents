package com.swarn.androidcomponents.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.service.MyForegroundService


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ForegroundServiceFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class ForegroundServiceFragment : androidx.fragment.app.Fragment() {

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var inputEditTxt: EditText

    private lateinit var startServiceBtn: Button
    private lateinit var stopServiceBtn: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        inputEditTxt = activity!!.findViewById(R.id.edit_text_input)

        startServiceBtn = activity!!.findViewById(R.id.start_service_btn)
        stopServiceBtn = activity!!.findViewById(R.id.stop_service_btn)

        startServiceBtn.setOnClickListener {
            val input = inputEditTxt.text.toString()

            val serviceIntent = Intent(activity, MyForegroundService::class.java)
            serviceIntent.putExtra("inputExtra", input)

            ContextCompat.startForegroundService(activity!!.applicationContext, serviceIntent)
        }

        stopServiceBtn.setOnClickListener {
            val serviceIntent = Intent(activity, MyForegroundService::class.java)
            activity!!.stopService(serviceIntent)
        }
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            // throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}
