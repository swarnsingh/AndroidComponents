package com.swarn.androidcomponents

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.swarn.androidcomponents.di.dagger.ApplicationGraph
import com.swarn.androidcomponents.di.dagger.DaggerApplicationGraph
import com.swarn.androidcomponents.di.dagger.UserRepository
import kotlinx.android.synthetic.main.activity_navigation.fab
import kotlinx.android.synthetic.main.app_bar_main.*
import timber.log.Timber

class NavigationActivity : AppCompatActivity() {

    private lateinit var countTxtView: TextView

    private lateinit var startBtn: Button
    private lateinit var stopBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(NavigationActivity::class.java.canonicalName, "onCreate")

        setContentView(R.layout.activity_navigation)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // Create an instance of the application graph
        val applicationGraph: ApplicationGraph = DaggerApplicationGraph.create()

        // Grab an instance of UserRepository from the application graph
        val userRepository: UserRepository = applicationGraph.repository()
    }

    override fun onStart() {
        super.onStart()
        Timber.d(NavigationActivity::class.java.canonicalName, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.d(NavigationActivity::class.java.canonicalName, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Timber.d(NavigationActivity::class.java.canonicalName, "onPause")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.d(NavigationActivity::class.java.canonicalName, "onReStart")
    }

    override fun onStop() {
        super.onStop()
        Timber.d(NavigationActivity::class.java.canonicalName, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d(NavigationActivity::class.java.canonicalName, "onDestroy")
        if (fab.context != null) {
            Timber.d(NavigationActivity::class.java.canonicalName, "context is not null")
        } else {
            Timber.d(NavigationActivity::class.java.canonicalName, "context is null")
        }
        val isDestroyed = (fab.context as NavigationActivity).isDestroyed
        Timber.d(NavigationActivity::class.java.canonicalName, "Is Destroyed : $isDestroyed")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Timber.d(
            NavigationActivity::class.java.canonicalName,
            "onRestoreInstanceState " + savedInstanceState?.getString("KEY")
        )
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("KEY", "123")

        // call superclass to save any view hierarchy
        outState.let { super.onSaveInstanceState(it) }

        Timber.d(NavigationActivity::class.java.canonicalName, "onSaveInstanceState")
    }

}
