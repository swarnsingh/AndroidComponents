package com.swarn.androidcomponents.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.data.OkHttpData
import okhttp3.*
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 *
 */

const val URL = "https://api.letsbuildthatapp.com/youtube/home_feed"

class OkHttpFragment : Fragment() {

    private lateinit var mProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ok_http, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mProgressBar = activity!!.findViewById(R.id.progress_bar)

        getOkHttpResponse()
    }

    private fun getOkHttpResponse() {
        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
            .url(URL)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.d(OkHttpFragment::class.java.canonicalName, e.localizedMessage)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val value = response.body()?.string()
                    val gson = GsonBuilder().create()

                    val okHttpData = gson.fromJson<OkHttpData>(value, OkHttpData::class.java)

                    activity?.runOnUiThread {
                        mProgressBar.visibility = View.GONE
                    }

                    Log.d(OkHttpFragment::class.java.canonicalName, okHttpData.toString())
                }
            }

        })

    }
}
