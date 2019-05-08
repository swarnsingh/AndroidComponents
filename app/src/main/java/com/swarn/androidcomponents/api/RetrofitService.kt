package com.swarn.androidcomponents.api

import com.swarn.androidcomponents.data.OkHttpData
import retrofit2.Call
import retrofit2.http.GET

/**
 * @author Swarn Singh.
 */
interface RetrofitService {
    @GET("/youtube/home_feed")
    fun getRetrofitData(): Call<OkHttpData>
}