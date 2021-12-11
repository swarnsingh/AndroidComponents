package com.swarn.androidcomponents

import android.app.Application
import com.swarn.androidcomponents.di.koin.appModule
import com.swarn.androidcomponents.report.ErrorReporterInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


/**
 * @author Swarn Singh.
 */
class AndroidComponents : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AndroidComponents)
            modules(listOf(appModule))
        }

        if (BuildConfig.BUILD_TYPE.equals("debug", true) && Timber.treeCount == 0) Timber.plant(Timber.DebugTree())

        registerActivityLifecycleCallbacks(ErrorReporterInitializer())
        registerActivityLifecycleCallbacks(
            LunaActivityLifecycleHandler(
                this
            )
        )

        /**
        val configuration = ApptentiveConfiguration(
        "ANDROID-ATVE-b90c25b46a56",
        "827b48e3eab32f974fa91897ede67266"
        )
        Apptentive.register(this, configuration)

        Tracker.configure(
        Tracker.Configuration(applicationContext)
        .setAppGuid("abc")
        )
         */
    }
}