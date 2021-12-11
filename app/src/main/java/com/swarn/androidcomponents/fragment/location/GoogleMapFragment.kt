package com.swarn.androidcomponents.fragment.location


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri.fromParts
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.apptentive.android.sdk.Apptentive
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.util.ConnectivityManager
import com.swarn.androidcomponents.util.GpsUtil
import com.swarn.androidcomponents.util.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100
const val GPS_REQUEST_GOOGLE_MAP_FRAGMENT: Int = 101

/**
 * @author Swarn Singh.
 */
class GoogleMapFragment : Fragment(), OnMapReadyCallback {

    private var isGPS: Boolean = false
    private var mLocationPermissionGranted = false

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var marker: Marker
    private lateinit var markerOptions: MarkerOptions
    private lateinit var alertDialog: AlertDialog

    private var googleMap: GoogleMap? = null

    private val TAG = GoogleMapFragment::class.java.canonicalName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 15 * 1000
            fastestInterval = 10 * 1000 //  to set an upper limit to the update rate
        }

        locationCallback = initLocationCallback()

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Apptentive.engage(context, "GoogleMapFragment_event")
    }

    override fun onResume() {
        super.onResume()

        updateLocationUI()
    }

    override fun onPause() {
        super.onPause()

        mFusedLocationClient.removeLocationUpdates(locationCallback)

        if (::alertDialog.isInitialized) {
            alertDialog.dismiss()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        updateLocationUI()
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
        GpsUtil(requireActivity()).turnGPSOn(object : GpsUtil.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable

                if (isGPS) {
                    getDeviceLocation()
                }
            }
        },
            GPS_REQUEST_GOOGLE_MAP_FRAGMENT
        )
    }

    private fun getAddress(location: Location): Address? {
        Timber.d(
            TAG,
            "Current Thread in getAddress : ${Thread.currentThread().id}"
        )

        try {
            if (ConnectivityManager.isConnected(requireActivity().applicationContext)) {
                val geocoder = Geocoder(activity?.applicationContext, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                return addresses!![0]
            }
        } catch (e: Exception) {
            Timber.d(TAG, e.toString())
        }
        return null
    }

    private fun setMarker(currentLocation: Location, address: Address) {
        Timber.d(
            TAG,
            "Current Thread in setMarker : ${Thread.currentThread().id}"
        )
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)

        val area = address.subAdminArea

        if (::marker.isInitialized) {
            marker.remove()
        }

        markerOptions = MarkerOptions().position(latLng).title("Your Location").snippet(address.getAddressLine(0))

        marker = googleMap?.addMarker(markerOptions)!!

        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 15.0F
            )
        )
    }

    private fun addMarker(currentLocation: Location) {
        GlobalScope.launch(Dispatchers.Main) {
            val address = withContext(Dispatchers.IO) { getAddress(currentLocation) }
            address?.let { setMarker(currentLocation, it) }
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
            e.message?.let { Timber.e("Exception: %s", it) }
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
            e.message?.let { Log.e("Exception: %s", it) }
        }
    }

    private fun getLocationPermission() {
        /**
         *  Request location permission, so that we can get the location of the
         *   device. The result of the permission request is handled by a callback,
         *   onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(
                requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            (ContextCompat.checkSelfPermission(
                requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            mLocationPermissionGranted = true
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            getGPSLocation()
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(requireActivity(), "Please give the permission for Access the location!", Toast.LENGTH_LONG)
                    .show()
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
                PermissionUtils.setNeverAskAgainStatus(requireActivity())
            } else {
                if (PermissionUtils.isNeverAskAgainSelected(requireActivity())) {
                    // It means Never Ask again checkbox is selected. So we need to open settings dialog
                    openApplicationDetailSettingDialog()
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

    private fun openApplicationDetailSettingDialog() {
        if (::alertDialog.isInitialized) {
            alertDialog.dismiss()
        }
        val builder = AlertDialog.Builder(activity).apply {
            setMessage(
                "We need to use GPS Service to get the current location. Please permit the permission through "
                        + "Settings screen.\n\nSelect Permissions -> Enable permission"
            )
            setCancelable(false)
            setPositiveButton("Permit Manually") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                context?.startActivity(intent)
            }
            setNegativeButton("Cancel", null)
        }
        alertDialog = builder.create()
        alertDialog.show()
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
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                }
            }
        }
    }
}
