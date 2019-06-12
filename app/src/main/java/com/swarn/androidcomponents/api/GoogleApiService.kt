package com.swarn.androidcomponents.api

import com.swarn.androidcomponents.data.GoogleApiResponse
import com.swarn.androidcomponents.data.GoogleDirectionsApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Swarn Singh.
 */
interface GoogleApiService {

    @GET("geocode/json")
    fun getLocationFromLatLng(@Query("latlng") latlng: String, @Query("key") apiKey: String): Call<GoogleApiResponse>

    @GET("geocode/json")
    fun getLocationFromAddress(@Query("address") address: String, @Query("key") apiKey: String): Call<GoogleApiResponse>

    @GET("directions/json")
    fun getDirections(
        @Query("origin") origin: String, @Query("destination") destination: String,
        @Query("mode") mode: String, @Query("key") apiKey: String
    ): Call<GoogleDirectionsApiResponse>
}