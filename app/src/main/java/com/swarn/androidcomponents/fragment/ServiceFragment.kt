package com.swarn.androidcomponents.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.service.MyService
import kotlinx.android.synthetic.main.fragment_import.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ServiceFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ServiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ServiceFragment : androidx.fragment.app.Fragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var startServiceBtn: Button
    private lateinit var stopServiceBtn: Button
    private lateinit var bindServiceBtn: Button
    private lateinit var unbindServiceBtn: Button
    private lateinit var getRandomBtn: Button

    private lateinit var serviceIntent: Intent

    private var isServiceBound = false

    var myService: MyService? = null

    private val TAG = ServiceFragment::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
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
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_import, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        startServiceBtn = activity!!.findViewById(R.id.start_service_btn)
        stopServiceBtn = activity!!.findViewById(R.id.stop_service_btn)

        bindServiceBtn = activity!!.findViewById(R.id.bind_service_btn)
        unbindServiceBtn = activity!!.findViewById(R.id.unbind_service_btn)

        getRandomBtn = activity!!.findViewById(R.id.random_gen_btn)

        serviceIntent = Intent(activity, MyService::class.java)

        startServiceBtn.setOnClickListener(this)
        stopServiceBtn.setOnClickListener(this)
        bindServiceBtn.setOnClickListener(this)
        unbindServiceBtn.setOnClickListener(this)
        getRandomBtn.setOnClickListener(this)


        Log.d(TAG, "onActivityCreated")
    }

    override fun onClick(v: View?) {
        when (v) {
            startServiceBtn -> {
                Log.d(TAG, "on Import Fragment : Thread Id : " + Thread.currentThread().id)
                activity?.startService(serviceIntent)
            }
            stopServiceBtn -> {
                Log.d(TAG, "on Import Fragment : Thread Id : " + Thread.currentThread().id)
                activity?.stopService(serviceIntent)
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

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MyService.MyServiceBinder
            myService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isServiceBound = false
        }
    }

    private fun bindService() {
        activity?.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        activity?.startService(serviceIntent)
    }

    private fun unbindService() {
        if (isServiceBound) {
            activity?.unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    private fun setRandomNumber() {
        if (isServiceBound) {
            import_txt_view.text = myService?.getRandomNumber().toString()
        } else {
            import_txt_view.text = "on Service Not Bound"
            Toast.makeText(activity!!, "Service is not bounded", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ServiceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ServiceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
