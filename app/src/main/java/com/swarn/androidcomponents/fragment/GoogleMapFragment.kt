package com.swarn.androidcomponents.fragment


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
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
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.util.PermissionUtils
import com.swarn.androidcomponents.util.GpsUtil


const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100

class GoogleMapFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private var isGPS: Boolean = false
    private var mLocationPermissionGranted = false

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var marker: Marker
    private lateinit var markerOptions: MarkerOptions
    private lateinit var alertDialog: AlertDialog

    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        locationCallback = initLocationCallback()

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()

        updateLocationUI()
    }

    override fun onPause() {
        super.onPause()

        if (::alertDialog.isInitialized) {
            alertDialog.dismiss()
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 20 * 1000 // 10 seconds
        //locationRequest.fastestInterval = 5 * 1000; // 5 seconds

        updateLocationUI()

        getDeviceLocation()
    }

    private fun initLocationCallback(): LocationCallback {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                val mCurrentLocation = locationResult!!.lastLocation

                Log.d("onLocationResult", mCurrentLocation.toString())

                addMarker(mCurrentLocation)
            }
        }
        return locationCallback
    }

    private fun getGPSLocation() {
        GpsUtil(activity!!).turnGPSOn(object : GpsUtil.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable

                if (isGPS) {
                    getDeviceLocation()
                }
            }
        })
    }

    private fun addMarker(currentLocation: Location) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        markerOptions = MarkerOptions().position(latLng).title("Current Location")

        if (::marker.isInitialized) {
            marker.remove()
        }

        marker = googleMap?.addMarker(
            MarkerOptions().position(latLng)
                .title("Current Location")
        )!!

        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 15.0F
            )
        )
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            Log.d("onLocationChanged : ", location.toString())

            addMarker(location)
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
            Log.e("Exception: %s", e.message)
        }
    }

    private fun getDeviceLocation() {
        /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationClient.lastLocation

                locationResult.addOnCompleteListener {
                    if (it.isSuccessful) {
                        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
                        val mLastKnownLocation = it.result

                        if (mLastKnownLocation != null) {
                            addMarker(mLastKnownLocation)
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", it.exception)
                        //googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))
                        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
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
                Toast.makeText(activity!!, "Please give the permission for Access the location!", Toast.LENGTH_LONG)
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
        val builder = AlertDialog.Builder(activity)

        builder.setMessage(
            "We need to use GPS Service to get the current location. Please permit the permission through "
                    + "Settings screen.\n\nSelect Permissions -> Enable permission"
        )
        builder.setCancelable(false)
        builder.setPositiveButton("Permit Manually") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = fromParts("package", activity!!.packageName, null)
            intent.data = uri
            context?.startActivity(intent)
        }
        builder.setNegativeButton("Cancel", null)
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
