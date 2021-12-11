package com.swarn.androidcomponents.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.apptentive.android.sdk.Apptentive
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.api.APIClient
import com.swarn.androidcomponents.api.RetrofitService
import com.swarn.androidcomponents.data.OkHttpData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class RetrofitFragment : Fragment() {

    private lateinit var retrofitService: RetrofitService
    private lateinit var mProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retrofit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mProgressBar = requireActivity().findViewById(R.id.progress_bar)

        getRetrofitResponse()
    }

    private fun getRetrofitResponse() {
        Apptentive.engage(context, "retrofit_event")
        retrofitService = APIClient.createService(RetrofitService::class.java)

        retrofitService.getRetrofitData().enqueue(object : Callback<OkHttpData> {
            override fun onFailure(call: Call<OkHttpData>, t: Throwable) {
                activity?.runOnUiThread {
                    mProgressBar.visibility = View.GONE
                }
                Timber.d(RetrofitFragment::class.java.canonicalName, t.localizedMessage)
            }

            override fun onResponse(call: Call<OkHttpData>, response: Response<OkHttpData>) {

                if (response.isSuccessful) {
                    val okHttpData = response.body()
                    Timber.d(RetrofitFragment::class.java.canonicalName, okHttpData.toString())
                }

                activity?.runOnUiThread {
                    mProgressBar.visibility = View.GONE
                }
            }
        })
    }
}
