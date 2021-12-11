package com.swarn.androidcomponents.fragment.location


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.textfield.TextInputEditText
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.adapter.PlaceAutoCompleteAdapter
import com.swarn.androidcomponents.api.GoogleApiClient
import com.swarn.androidcomponents.api.GoogleApiService
import com.swarn.androidcomponents.data.GoogleDirectionsApiResponse
import com.swarn.androidcomponents.data.Places
import com.swarn.androidcomponents.util.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*


const val TRIGGER_AUTO_COMPLETE = 100
const val AUTO_COMPLETE_DELAY = 300L
const val GPS_REQUEST_GOOGLE_PLACES_FRAGMENT: Int = 102

const val UPDATE_INTERVAL = 60 * 1000L
const val FASTEST_INTERVAL = 50 * 1000L

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class GooglePlacesFragment : Fragment(), OnMapReadyCallback,
    PlaceAutoCompleteAdapter.ItemClickListener {

    private var token = AutocompleteSessionToken.newInstance()

    private lateinit var addressAutoCompleteEditTxt: TextInputEditText

    private lateinit var mAddressRecyclerView: RecyclerView

    private lateinit var requestBuilder: FindAutocompletePredictionsRequest.Builder

    private lateinit var placesClient: PlacesClient

    private lateinit var adapter: PlaceAutoCompleteAdapter

    private var addresses = ArrayList<Places>()

    private var handler: Handler? = null


    private var isGPS: Boolean = false
    private var mLocationPermissionGranted = false
    private var isRouteDraw = false

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var currentLocationMarker: Marker
    private lateinit var selectedPlaceMarker: Marker

    private lateinit var markerOptions: MarkerOptions

    private lateinit var placesMapFragment: SupportMapFragment

    private var googleMap: GoogleMap? = null

    private lateinit var directionsApiRequest: DirectionsApiRequest
    private lateinit var geoApiContext: GeoApiContext

    private lateinit var googleApiService: GoogleApiService

    private lateinit var polyline: Polyline

    private val TAG = GooglePlacesFragment::class.java.canonicalName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_google_places, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()

        addressAutoCompleteEditTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                getLocations(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler?.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler?.sendEmptyMessageDelayed(
                    TRIGGER_AUTO_COMPLETE,
                    AUTO_COMPLETE_DELAY
                );
            }
        })

        handler = Handler(Handler.Callback { msg ->
            if (msg.what === TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(addressAutoCompleteEditTxt.text) && addressAutoCompleteEditTxt.text!!.length >= 2) {
                    getLocations(addressAutoCompleteEditTxt.text.toString())
                }
            }
            false
        })

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval =
                UPDATE_INTERVAL
            fastestInterval =
                FASTEST_INTERVAL //  to set an upper limit to the update rate
        }

        locationCallback = initLocationCallback()

    }

    private fun init() {
        addressAutoCompleteEditTxt = activity!!.findViewById(R.id.address_edit_txt)
        mAddressRecyclerView = activity!!.findViewById(R.id.address_recycler_view)

        mAddressRecyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        placesClient =
            com.google.android.libraries.places.api.Places.createClient(activity!!.applicationContext)

        requestBuilder = FindAutocompletePredictionsRequest.builder()
            .setCountry("IN")
            .setSessionToken(token)
            .setQuery("")

        adapter = PlaceAutoCompleteAdapter(this)
        mAddressRecyclerView.adapter = adapter

        placesMapFragment =
            childFragmentManager.findFragmentById(R.id.google_places_map) as SupportMapFragment
        placesMapFragment.getMapAsync(this)


        geoApiContext = GeoApiContext.Builder()
            .enterpriseCredentials(
                getString(R.string.google_client_id),
                getString(R.string.google_client_secret_key)
            )
            .build()

        googleApiService = GoogleApiClient.createService(GoogleApiService::class.java)
    }

    override fun onResume() {
        super.onResume()

        updateLocationUI()
    }

    override fun onPause() {
        super.onPause()

        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun getLocations(query: String) {
        val request = requestBuilder.setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.Default).launch {
                    addresses = MapUtil.getPredictionPlaces(it)

                    withContext(Dispatchers.Main) {
                        adapter.setData(addresses)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            .addOnFailureListener {
                Timber.d(GooglePlacesFragment::class.java.canonicalName, it.localizedMessage)
            }
    }

    private fun initLocationCallback(): LocationCallback {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                val mCurrentLocation = locationResult!!.lastLocation

                Timber.d("onLocationResult", mCurrentLocation.toString())

                addMarker(mCurrentLocation)
            }
        }
        return locationCallback
    }

    private fun getGPSLocation() {
        GpsUtil(activity!!).turnGPSOn(
            object : GpsUtil.OnGpsListener {
                override fun gpsStatus(isGPSEnable: Boolean) {
                    // turn on GPS
                    isGPS = isGPSEnable

                    if (isGPS) {
                        getDeviceLocation()
                    }
                }
            },
            GPS_REQUEST_GOOGLE_PLACES_FRAGMENT
        )
    }

    private fun getLocation(query: String): Address? {
        Timber.d(
            TAG,
            "Current Thread in getAddress : ${Thread.currentThread().id}"
        )

        try {
            if (ConnectivityManager.isConnected(activity!!.applicationContext)) {

                /*googleApiService.getLocationFromAddress(query, getString(R.string.google_maps_key))
                    .enqueue(object : Callback<GoogleApiResponse> {

                        override fun onResponse(call: Call<GoogleApiResponse>, response: Response<GoogleApiResponse>) {
                            Log.d(TAG, response.body().toString())
                        }

                        override fun onFailure(call: Call<GoogleApiResponse>, t: Throwable) {
                            Log.d(TAG, t.localizedMessage)
                        }
                    })*/

                val geocoder = Geocoder(activity?.applicationContext, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(query, 1)
                return addresses[0]
            }
        } catch (e: Exception) {
            Timber.d(TAG, e.toString())
            val handler = activity!!.window.decorView.handler
            handler.post {
                Toast.makeText(
                    activity!!,
                    "Unable to fetch the location. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return null
    }

    private fun getAddress(location: Location): Address? {
        Timber.d(
            TAG,
            "Current Thread in getAddress : ${Thread.currentThread().id}"
        )

        try {
            if (ConnectivityManager.isConnected(activity!!.applicationContext)) {

                /*val latLng = "" + location.latitude + "," + location.longitude

                 googleApiService.getLocationFromLatLng(latLng, getString(R.string.google_maps_key))
                     .enqueue(object : Callback<GoogleApiResponse> {

                         override fun onResponse(call: Call<GoogleApiResponse>, response: Response<GoogleApiResponse>) {
                             if (response.isSuccessful) {
                                 Log.d(TAG, response.body().toString())
                             }
                         }

                         override fun onFailure(call: Call<GoogleApiResponse>, t: Throwable) {
                             Log.d(TAG, t.localizedMessage.toString())
                         }
                     })*/

                val geocoder = Geocoder(activity?.applicationContext, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                return addresses!![0]
            }
        } catch (e: Exception) {
            Timber.d(TAG, e.toString())
        }
        return null
    }

    private fun setMarker(isCurrentLocationMarker: Boolean, address: Address, title: String) {
        Timber.d(
            TAG,
            "Current Thread in setMarker : ${Thread.currentThread().id}"
        )
        val latLng = LatLng(address.latitude, address.longitude)

        markerOptions =
            MarkerOptions().position(latLng).title(title).snippet(address.getAddressLine(0))

        if (isCurrentLocationMarker) {
            if (::currentLocationMarker.isInitialized) {
                currentLocationMarker.remove()
            }
            currentLocationMarker = googleMap?.addMarker(markerOptions)!!
        } else {
            if (::selectedPlaceMarker.isInitialized) {
                selectedPlaceMarker.remove()
            }
            selectedPlaceMarker = googleMap?.addMarker(markerOptions)!!
        }

        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 12.0F
            )
        )

        // Draw the route (Polyline) between the source & destination
        drawRoute()
    }

    override fun onItemClick(place: Places) {
        addresses.clear()
        adapter.setData(addresses)
        adapter.notifyDataSetChanged()

        addressAutoCompleteEditTxt.text = null
        requireActivity().currentFocus?.let { Util.hideKeyboardFrom(requireActivity(), it) }

        GlobalScope.launch(Dispatchers.Main) {
            val address =
                withContext(Dispatchers.IO) { getLocation(place.primaryAddress + " " + place.secondaryAddress) }
            address?.let {
                setMarker(false, address, "Selected Location")
            }
        }
    }

    private fun drawRoute() {
        if (::currentLocationMarker.isInitialized && ::selectedPlaceMarker.isInitialized) {

            val origin =
                "" + currentLocationMarker.position.latitude + "," + currentLocationMarker.position.longitude
            val destination =
                "" + selectedPlaceMarker.position.latitude + "," + selectedPlaceMarker.position.longitude
            val mode = "driving"  // default mode

            googleApiService.getDirections(
                origin,
                destination,
                mode,
                getString(R.string.google_maps_key)
            )
                .enqueue(object : Callback<GoogleDirectionsApiResponse> {

                    override fun onResponse(
                        call: Call<GoogleDirectionsApiResponse>,
                        response: Response<GoogleDirectionsApiResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (::polyline.isInitialized) {
                                polyline.remove()
                            }

                            CoroutineScope(Dispatchers.Default).launch {
                                val routes = MapUtil.parseDirections(response.body()!!)
                                val polyLineOptions = MapUtil.getPolyLines(routes)
                                withContext(Dispatchers.Main) {
                                    polyline = googleMap?.addPolyline(polyLineOptions)!!
                                }
                            }

                            Timber.d(TAG, response.body().toString())
                        } else {
                            Toast.makeText(
                                activity!!.applicationContext,
                                "No route found between source & destination",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<GoogleDirectionsApiResponse>, t: Throwable) {
                        Toast.makeText(
                            activity!!.applicationContext,
                            "Failed to get route between source & destination",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun addMarker(currentLocation: Location) {
        GlobalScope.launch(Dispatchers.Main) {
            val address = withContext(Dispatchers.IO) { getAddress(currentLocation) }
            address?.let {
                setMarker(true, it, "Current Location")
            }
        }
    }

    fun updateLocationUI() {
        if (googleMap == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                googleMap?.isMyLocationEnabled = true
                googleMap?.uiSettings?.isMyLocationButtonEnabled = true

                getGPSLocation()
            } else {
                googleMap?.isMyLocationEnabled = false
                googleMap?.uiSettings?.isMyLocationButtonEnabled = false

                getLocationPermission()
            }
        } catch (e: SecurityException) {
            e.message?.let { Log.e("Exception: %s", it) }
        }
    }

    private fun getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

                mFusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        googleMap?.uiSettings?.isMyLocationButtonEnabled = true

                        if (it != null) {
                            addMarker(it)
                        }
                    }
                    .addOnFailureListener {
                        Timber.d(TAG, "Current location is null. Using defaults.")
                        Timber.e(TAG, "Exception: %s", it)
                        //googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))
                        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
            }
        } catch (e: SecurityException) {
            e.message?.let { Timber.e("Exception: %s", it) }
        }
    }

    private fun getLocationPermission() {
        /**
         *  Request location permission, so that we can get the location of the
         *   device. The result of the permission request is handled by a callback,
         *   onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(
                activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            (ContextCompat.checkSelfPermission(
                activity!!, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            mLocationPermissionGranted = true
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            getGPSLocation()
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(
                    activity!!,
                    "Please give the permission for Access the location!",
                    Toast.LENGTH_LONG
                )
                    .show()
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
                PermissionUtils.setNeverAskAgainStatus(activity!!)
            } else {
                if (PermissionUtils.isNeverAskAgainSelected(activity!!)) {
                    // It means Never Ask again checkbox is selected. So we need to open settings dialog
                    Util.openApplicationDetailSettingDialog(
                        activity!!,
                        getString(R.string.gps_alert_dialog_msg)
                    )
                } else {
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                    )
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                    getGPSLocation()
                    mFusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null
                    )
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateLocationUI()
    }
}
