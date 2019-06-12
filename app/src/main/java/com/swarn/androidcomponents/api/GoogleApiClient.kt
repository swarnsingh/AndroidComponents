package com.swarn.androidcomponents.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Swarn Singh.
 */
object GoogleApiClient {

    private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

    private var adapterBuilder: retrofit2.Retrofit.Builder? = null

    private fun createDefaultAdapter() {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        adapterBuilder = retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
    }

    fun <S> createService(serviceClass: Class<S>): S {
        if (adapterBuilder == null) {
            createDefaultAdapter()
        }
        return this.adapterBuilder!!.build().create(serviceClass)
    }
}