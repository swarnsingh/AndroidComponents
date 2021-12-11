package com.swarn.androidcomponents

import android.app.Activity
import android.app.Application
import android.os.Bundle
import org.koin.core.component.KoinComponent
import timber.log.Timber

open class LunaActivityLifecycleHandler(private val activity: Application) :
    Application.ActivityLifecycleCallbacks,
    KoinComponent {

    override fun onActivityStarted(currentActivity: Activity) {
        Timber.d("onActivityStarted at ${currentActivity.localClassName}")
    }

    override fun onActivitySaveInstanceState(currentActivity: Activity, p1: Bundle) {
        Timber.d("onActivitySaveInstanceState at ${currentActivity.localClassName}")
    }

    override fun onActivityCreated(currentActivity: Activity, p1: Bundle?) {
        Timber.d("onActivityCreated at ${currentActivity.localClassName}")
    }

    override fun onActivityDestroyed(currentActivity: Activity) {
        Timber.d("onActivityDestroyed at ${currentActivity.localClassName}")
    }

    override fun onActivityStopped(currentActivity: Activity) {
        Timber.d("onActivityStopped at ${currentActivity.localClassName}")
    }

    override fun onActivityResumed(currentActivity: Activity) {
        Timber.d("onActivityResumed at ${currentActivity.localClassName}")
    }

    override fun onActivityPaused(currentActivity: Activity) {
        Timber.d("onActivityPaused at ${currentActivity.localClassName}")
    }
}
