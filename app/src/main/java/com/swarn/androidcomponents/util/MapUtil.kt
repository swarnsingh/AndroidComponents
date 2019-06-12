package com.swarn.androidcomponents.util

import android.graphics.Color
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.swarn.androidcomponents.data.GoogleDirectionsApiResponse
import com.swarn.androidcomponents.data.Places

/**
 * @author Swarn Singh.
 */

object MapUtil {

    fun parseDirections(result: GoogleDirectionsApiResponse): ArrayList<List<HashMap<String, String>>> {
        val routes = ArrayList<List<HashMap<String, String>>>()

        result.routes.forEach { route ->
            route.legs.forEach { leg ->
                val path = java.util.ArrayList<HashMap<String, String>>()

                leg.steps.forEach { step ->
                    decodePoly(step.polyline.points).forEach { latlng ->
                        val hm = HashMap<String, String>()
                        hm["lat"] = "" + latlng.latitude
                        hm["lng"] = "" + latlng.longitude

                        path.add(hm)
                    }
                    routes.add(path)
                }
            }
        }
        return routes
    }

    fun getPolyLines(routes: ArrayList<List<HashMap<String, String>>>): PolylineOptions {
        var lineOptions = PolylineOptions()
        var points: ArrayList<LatLng>

        routes.forEach { path ->
            points = ArrayList()
            lineOptions = PolylineOptions()


            path.forEach { point ->
                val lat = point["lat"]!!.toDouble()
                val lng = point["lng"]!!.toDouble()
                val latlng = LatLng(lat, lng)

                points.add(latlng)
            }

            lineOptions.addAll(points)

            lineOptions.width(18.0F)
            lineOptions.color(Color.MAGENTA)
        }

        return lineOptions
    }

    fun getPredictionPlaces(it: FindAutocompletePredictionsResponse): ArrayList<Places> {
        val addresses = ArrayList<Places>()
        for (prediction in it.autocompletePredictions) {
            val place = Places(prediction.placeId, prediction.b(), prediction.c())
            addresses.add(place)
            Log.d(MapUtil::class.java.canonicalName, prediction.toString())
        }

        return addresses
    }

    private fun decodePoly(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }
}