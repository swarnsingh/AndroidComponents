package com.swarn.androidcomponents.fragment.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.service.MyIntentService
import kotlinx.android.synthetic.main.fragment_gallery.*
import timber.log.Timber


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [IntentServiceFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [IntentServiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class IntentServiceFragment : androidx.fragment.app.Fragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var startServiceBtn: Button
    private lateinit var stopServiceBtn: Button
    private lateinit var bindServiceBtn: Button
    private lateinit var unbindServiceBtn: Button
    private lateinit var getRandomBtn: Button

    private lateinit var intentServiceIntent: Intent

    private var isServiceConnected = false

    private var myIntentService: MyIntentService? = null

    private val TAG: String? = IntentServiceFragment::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onCreate")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onCreateView")
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(value: String) {
        listener?.onFragmentInteraction(value)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onAttach")
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val txt = activity?.findViewById<TextView>(R.id.gallery_txt_view)
        txt?.setOnClickListener {
            listener?.onFragmentInteraction("message from fragment")
        }

        startServiceBtn = activity!!.findViewById(R.id.start_service_btn)
        stopServiceBtn = activity!!.findViewById(R.id.stop_service_btn)

        bindServiceBtn = activity!!.findViewById(R.id.bind_service_btn)
        unbindServiceBtn = activity!!.findViewById(R.id.unbind_service_btn)

        getRandomBtn = activity!!.findViewById(R.id.random_gen_btn)

        startServiceBtn.setOnClickListener(this)
        stopServiceBtn.setOnClickListener(this)
        bindServiceBtn.setOnClickListener(this)
        unbindServiceBtn.setOnClickListener(this)
        getRandomBtn.setOnClickListener(this)

        intentServiceIntent = Intent(activity, MyIntentService::class.java)

        Timber.d(IntentServiceFragment::class.java.canonicalName, "onActivityCreated")
    }

    override fun onClick(v: View?) {
        when (v) {
            startServiceBtn -> {
                Timber.d(TAG, "on Gallery Fragment : Thread Id : " + Thread.currentThread().id)
                activity?.startService(intentServiceIntent)
            }
            stopServiceBtn -> {
                Timber.d(TAG, "on Gallery Fragment : Thread Id : " + Thread.currentThread().id)
                activity?.stopService(intentServiceIntent)
            }
            bindServiceBtn -> {
                bindService()
            }
            unbindServiceBtn -> {
                unbindService()
            }
            getRandomBtn -> {
                setRandomNumber()
            }
        }
    }


    /**
     * monitors the connection with the service.
     */
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyIntentService.MyServiceBinder

            myIntentService = binder.getService()

            isServiceConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceConnected = false
        }

    }

    private fun bindService() {
        activity?.bindService(intentServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        activity?.startService(intentServiceIntent)
    }

    private fun unbindService() {
        if (isServiceConnected) {
            activity?.unbindService(serviceConnection)
            isServiceConnected = false
        }
    }

    private fun setRandomNumber() {
        if (isServiceConnected) {
            gallery_txt_view.text = myIntentService?.getRandomNumber().toString()
        } else {
            gallery_txt_view.text = "on Service Not Bound"
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.d(IntentServiceFragment::class.java.canonicalName, "onDetach")
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
        fun onFragmentInteraction(value: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IntentServiceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IntentServiceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
