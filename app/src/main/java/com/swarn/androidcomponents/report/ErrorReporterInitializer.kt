package com.swarn.androidcomponents.report

import android.app.Activity
import android.app.Application
import android.os.Bundle
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ErrorReporterInitializer : Application.ActivityLifecycleCallbacks, KoinComponent {

    private val errorReporter by inject<ErrorReporter>()

    init {
        val errorReportingTree by inject<Timber.Tree>()
        Timber.plant(errorReportingTree)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        errorReporter.start()
    }

    override fun onActivityStarted(activity: Activity) {
        // no-op
    }

    override fun onActivityResumed(activity: Activity) {
        // no-op
    }

    override fun onActivityPaused(activity: Activity) {
        // no-op
    }

    override fun onActivityStopped(activity: Activity) {
        // no-op
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // no-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        // no-op
    }
}
