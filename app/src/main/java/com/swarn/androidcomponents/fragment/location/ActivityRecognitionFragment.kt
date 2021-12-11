package com.swarn.androidcomponents.fragment.location


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.ActivityRecognitionResult
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.adapter.ActivityRecognitionAdapter
import com.swarn.androidcomponents.transition.DetectedActivity
import com.swarn.androidcomponents.transition.Track
import com.swarn.androidcomponents.transition.TransitionRecognition
import timber.log.Timber


const val TRANSITION_RECEIVER = "com.swarn.androidcomponents.fragment.transitionReceiver"

class ActivityRecognitionFragment : Fragment() {

    private lateinit var mActivityRecognitionAdapter: ActivityRecognitionAdapter
    private lateinit var mRecyclerView: RecyclerView
    private var mTracks = ArrayList<Track>()

    private lateinit var mStartTrackBtn: Button
    private lateinit var mStopTrackBtn: Button

    private var isTrackingStarted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_recognition, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initActivityRecognitionData()

        mStartTrackBtn = requireActivity().findViewById(R.id.track_activity_btn)
        mStopTrackBtn = requireActivity().findViewById(R.id.stop_tracking_btn)

        mRecyclerView = requireActivity().findViewById(R.id.activities_recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        mActivityRecognitionAdapter = ActivityRecognitionAdapter()
        mActivityRecognitionAdapter.setData(mTracks)

        mRecyclerView.adapter = mActivityRecognitionAdapter


        mStartTrackBtn.setOnClickListener {
            isTrackingStarted = true
            initTransitionRecognition()

            mStartTrackBtn.isClickable = false
            mStopTrackBtn.isClickable = true
        }

        mStopTrackBtn.setOnClickListener {
            isTrackingStarted = false
            stopTransitionRecognition()

            mStartTrackBtn.isClickable = true
            mStopTrackBtn.isClickable = false
        }
    }

    override fun onResume() {
        super.onResume()

        if (isTrackingStarted) {
            val intentFilter = IntentFilter(TRANSITION_RECEIVER)
            requireActivity().registerReceiver(transitionReceiver, intentFilter)
            initTransitionRecognition()
        }
    }

    override fun onPause() {
        super.onPause()

        if (isTrackingStarted) {
            stopTransitionRecognition()
        }
    }

    private val transitionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.extras != null) {
                val result =
                    intent.extras!!.get(TRANSITION_RECEIVER) as ActivityRecognitionResult
                Timber.d("Transition Receiver", "if $result")
                processTransitionResult(result)
            }
        }
    }

    private fun initActivityRecognitionData() {
        mTracks.clear()
        val values = DetectedActivity.values()

        for (item in values) {
            val track = Track(item, 0)
            mTracks.add(track)
        }
    }

    /**
     * INIT TRANSITION RECOGNITION
     */
    private fun initTransitionRecognition() {
        if (isTrackingStarted) {
            val intentFilter = IntentFilter(TRANSITION_RECEIVER)
            requireActivity().registerReceiver(transitionReceiver, intentFilter)
        }
        TransitionRecognition.startTracking(requireActivity().applicationContext)
    }

    private fun stopTransitionRecognition() {
        requireActivity().unregisterReceiver(transitionReceiver)
        TransitionRecognition.stopTracking(requireActivity().applicationContext)
    }

    fun processTransitionResult(result: ActivityRecognitionResult) {

        initActivityRecognitionData()

        val activityMap = HashMap<String, Int>()

        for (event in result.probableActivities) {
            if (event.type in 0..8) {
                val key = DetectedActivity.values()[event.type].name
                activityMap[key] = event.confidence
            }
        }

        mTracks.forEachIndexed { index, track ->
            if (activityMap.containsKey(track.type.toString())) {
                track.confidence = activityMap[track.type.toString()]!!
                mTracks[index] = track
            }
        }

        mActivityRecognitionAdapter.setData(mTracks)
        mActivityRecognitionAdapter.notifyDataSetChanged()
    }
}
