package com.swarn.androidcomponents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.api.Places
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.swarn.androidcomponents.fragment.*
import com.swarn.androidcomponents.fragment.location.*
import com.swarn.androidcomponents.fragment.service.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    IntentServiceFragment.OnFragmentInteractionListener {

    private val TAG = MainActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        if (savedInstanceState != null) {
            Log.d(TAG, "on savedInstanceState != null")
            Log.d(TAG, "on " + savedInstanceState.getString("KEY"))
        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        navigateFragment(ServiceFragment(), false)
    }

    override fun onFragmentInteraction(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onReStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    /**
     * This callback is called only when there is a saved instance that is previously saved by using
     * onSaveInstanceState(). We restore some state in onCreate(), while we can optionally restore
     * other state here, possibly usable after onStart() has completed.
     * The savedInstanceState Bundle is same as the one used in onCreate().
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        Log.d(TAG, ": onRestoreInstanceState " + savedInstanceState?.getString("KEY"))
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("KEY", "123")

        // call superclass to save any view hierarchy

        Log.d(TAG, ": onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, NavigationActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateFragment(fragment: Fragment, addToBackStack: Boolean) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(
            R.id.frame_container,
            fragment,
            fragment::class.java.canonicalName
        )

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment::class.java.canonicalName)
        }
        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_navigation -> {
                Intent(this, NavigationActivity::class.java).apply {
                    startActivity(this)
                }
            }
            R.id.nav_camera -> navigateFragment(ServiceFragment(), false)

            R.id.nav_gallery -> navigateFragment(IntentServiceFragment(), true)

            R.id.nav_slideshow -> navigateFragment(ForegroundServiceFragment(), true)

            R.id.nav_manage -> navigateFragment(JobIntentServiceFragment(), true)

            R.id.nav_job_scheduler -> navigateFragment(JobSchedulerFragment(), true)

            R.id.nav_work_manager -> navigateFragment(WorkManagerFragment(), true)

            R.id.nav_okhttp -> navigateFragment(OkHttpFragment(), true)

            R.id.nav_retrofit -> navigateFragment(RetrofitFragment(), true)

            R.id.nav_rx_java -> navigateFragment(RxFragment(), true)

            R.id.nav_google_map -> navigateFragment(GoogleMapFragment(), true)

            R.id.nav_google_geofence_map -> navigateFragment(GeofencesFragment(), true)

            R.id.nav_activity_recognition -> navigateFragment(ActivityRecognitionFragment(), true)

            R.id.nav_google_places -> navigateFragment(GooglePlacesFragment(), true)

            R.id.nav_share -> navigateFragment(MessengerServiceFragment(), true)

            R.id.nav_send -> navigateFragment(BroadcastReceiverFragment(), true)

            R.id.nav_content_provider -> navigateFragment(ContentProviderFragment(), true)

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST_GOOGLE_MAP_FRAGMENT) {

                val googleMapFragment =
                    supportFragmentManager.findFragmentById(R.id.frame_container) as GoogleMapFragment
                googleMapFragment.updateLocationUI()
            } else if (requestCode == GPS_REQUEST_GOOGLE_PLACES_FRAGMENT) {
                val googlePlacesFragment =
                    supportFragmentManager.findFragmentById(R.id.frame_container) as GooglePlacesFragment
                googlePlacesFragment.updateLocationUI()
            } else if (requestCode == REQUEST_SPEECH_RECOGNIZER) {
                val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (results != null && results.size > 0) {
                    Log.d(TAG, results[0].toString())
                }
            }
        }
    }
}
