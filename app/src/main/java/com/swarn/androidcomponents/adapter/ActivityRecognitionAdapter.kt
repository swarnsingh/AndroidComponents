package com.swarn.androidcomponents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.transition.Track

/**
 * @author Swarn Singh.
 */
class ActivityRecognitionAdapter : RecyclerView.Adapter<ActivityRecognitionAdapter.ActivityRecognitionViewHolder>() {

    private var mTracks: ArrayList<Track>

    init {
        mTracks = ArrayList()
    }

    fun setData(tracks: ArrayList<Track>) {
        mTracks = tracks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityRecognitionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detected_activity, parent, false)
        return ActivityRecognitionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mTracks.size
    }

    override fun onBindViewHolder(holder: ActivityRecognitionViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ActivityRecognitionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mActivityTypeTxtView = itemView.findViewById<TextView>(R.id.activity_type_txt_view)
        private var mConfidenceTxtView = itemView.findViewById<TextView>(R.id.confidence_percentage_txt_view)

        fun onBind(position: Int) {
            val track = mTracks[position]

            mActivityTypeTxtView.text = track.type.name
            mConfidenceTxtView.text = track.confidence.toString()  +"%"
        }
    }
}