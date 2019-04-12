package com.swarn.androidcomponents.fragment


import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.receiver.PowerConnectionReceiver


/**
 * A simple [Fragment] subclass.
 *
 */
class BroadcastReceiverFragment : Fragment() {

    private lateinit var startBroadcastBtn: Button
    private lateinit var stopReceiverBtn: Button

    private lateinit var powerConnectionReceiver: PowerConnectionReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_broadcast_receiver, container, false)
    }

    override fun onStart() {
        super.onStart()

        var intentFilter = IntentFilter("android.intent.action.ACTION_POWER_CONNECTED").apply {
            addAction("android.intent.action.ACTION_POWER_DISCONNECTED")
        }
        activity!!.registerReceiver(powerConnectionReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        activity!!.unregisterReceiver(powerConnectionReceiver)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        startBroadcastBtn = activity!!.findViewById(R.id.start_broadcast_btn)
        stopReceiverBtn = activity!!.findViewById(R.id.stop_receiver_btn)

        powerConnectionReceiver = PowerConnectionReceiver()

        /*startBroadcastBtn.setOnClickListener {
            val intent = Intent()
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.action = "com.BroadCastReceiver1"

            val intentFilter = IntentFilter("com.BroadCastReceiver1")
            activity!!.registerReceiver(BroadCastReceiver1(), intentFilter)
            activity!!.sendBroadcast(intent, "com.BroadCastReceiver1")
        }*/

    }

}
