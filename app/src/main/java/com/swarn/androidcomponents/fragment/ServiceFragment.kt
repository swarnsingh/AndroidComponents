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


class ServiceFragment : androidx.fragment.app.Fragment(), View.OnClickListener {

    private lateinit var startServiceBtn: Button
    private lateinit var stopServiceBtn: Button
    private lateinit var bindServiceBtn: Button
    private lateinit var unbindServiceBtn: Button
    private lateinit var getRandomBtn: Button

    private lateinit var serviceIntent: Intent

    private var isServiceBound = false

    var myService: MyService? = null

    private val TAG = ServiceFragment::class.java.canonicalName

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
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

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
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
}
