
package com.swarn.androidcomponents

import android.app.Application
import com.kochava.base.Tracker

/**
 * @author Swarn Singh.
 */
class AndroidComponents : Application() {

    override fun onCreate() {
        super.onCreate()
        Tracker.configure(
            Tracker.Configuration(applicationContext)
            .setAppGuid("abc")
        )
    }
}