package com.swarn.androidcomponents.fragment.service


import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.swarn.androidcomponents.R


/**
 * A simple [Fragment] subclass.
 *
 */
class MessengerServiceFragment : Fragment(), View.OnClickListener {


    val GET_RANDOM_NUMBER_FLAG = 0
    private var mIsBound: Boolean = false
    private var randomNumberValue: Int = 0

    private var randomNumberRequestMessenger: Messenger? = null
    private var randomNumberReceiveMessenger: Messenger? = null

    private var serviceIntent: Intent? = null

    private lateinit var bindServiceBtn: Button
    private lateinit var unBindServiceBtn: Button
    private lateinit var getRandomNoBtn: Button

    private lateinit var randomNoTxtView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_messenger, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindServiceBtn = requireActivity().findViewById(R.id.bind_service_btn)
        unBindServiceBtn = requireActivity().findViewById(R.id.unbind_service_btn)
        getRandomNoBtn = requireActivity().findViewById(R.id.get_random_no_btn)

        randomNoTxtView = requireActivity().findViewById(com.swarn.androidcomponents.R.id.random_no_txt_view)

        bindServiceBtn.setOnClickListener(this)
        unBindServiceBtn.setOnClickListener(this)
        getRandomNoBtn.setOnClickListener(this)

        serviceIntent = Intent()
        serviceIntent!!.component =
            ComponentName("com.swarn.messengerserviceapp", "com.swarn.messengerserviceapp.MessengerService")
    }

    @SuppressLint("HandlerLeak")
    internal inner class ReceiveRandomNumberHandler : Handler() {
        override fun handleMessage(msg: Message) {
            randomNumberValue = 0
            when (msg.what) {
                GET_RANDOM_NUMBER_FLAG -> {
                    randomNumberValue = msg.arg1
                    randomNoTxtView.text = "Random Number: $randomNumberValue"
                }
                else -> {
                }
            }
            super.handleMessage(msg)
        }
    }

    private var randomNumberServiceConnection: ServiceConnection? = object : ServiceConnection {
        override fun onServiceDisconnected(arg0: ComponentName) {
            randomNumberRequestMessenger = null
            randomNumberReceiveMessenger = null
            mIsBound = false
        }

        override fun onServiceConnected(arg0: ComponentName, binder: IBinder) {
            randomNumberRequestMessenger = Messenger(binder)
            randomNumberReceiveMessenger = Messenger(ReceiveRandomNumberHandler())
            mIsBound = true
        }
    }


    override fun onClick(v: View?) {
        when (v) {
            bindServiceBtn -> {
                bindToRemoteService()
            }
            unBindServiceBtn -> {
                unbindFromRemoteService()
            }
            getRandomNoBtn -> {
                fetchRandomNumber()
            }
        }
    }

    private fun bindToRemoteService() {
        randomNumberServiceConnection?.let {
            requireActivity().bindService(serviceIntent,
                it, BIND_AUTO_CREATE)
        }
        Toast.makeText(activity, "Service bound", Toast.LENGTH_SHORT).show()
    }

    private fun unbindFromRemoteService() {
        if (mIsBound) {
            randomNumberServiceConnection?.let { activity?.unbindService(it) }
            mIsBound = false
            Toast.makeText(activity, "Service Unbound", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRandomNumber() {
        if (mIsBound === true) {
            val requestMessage = Message.obtain(null, GET_RANDOM_NUMBER_FLAG)
            requestMessage.replyTo = randomNumberReceiveMessenger
            try {
                randomNumberRequestMessenger?.send(requestMessage)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        } else {
            Toast.makeText(activity, "Service Unbound, can't get random number", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        randomNumberServiceConnection = null
    }

}
