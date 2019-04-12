package com.swarn.androidcomponents

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log

import kotlinx.android.synthetic.main.activity_send.*

class SendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(SendActivity::class.java.canonicalName, "onCreate")
        setContentView(R.layout.activity_send)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(SendActivity::class.java.canonicalName, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(SendActivity::class.java.canonicalName, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(SendActivity::class.java.canonicalName, "onPause")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(SendActivity::class.java.canonicalName, "onReStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(SendActivity::class.java.canonicalName, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(SendActivity::class.java.canonicalName, "onDestroy")
    }

    /**
     * This callback is called only when there is a saved instance that is previously saved by using
     * onSaveInstanceState(). We restore some state in onCreate(), while we can optionally restore
     * other state here, possibly usable after onStart() has completed.
     * The savedInstanceState Bundle is same as the one used in onCreate().
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        Log.d(SendActivity::class.java.canonicalName, "onRestoreInstanceState "+savedInstanceState?.getString("KEY"))
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    /*override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("KEY", "123")

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState)

        Log.d(SendActivity::class.java.canonicalName, "onSaveInstanceState")
    }*/

}
