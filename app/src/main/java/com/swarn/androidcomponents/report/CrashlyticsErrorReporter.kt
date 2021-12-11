package com.swarn.androidcomponents.report

import android.annotation.SuppressLint
import timber.log.Timber

@SuppressLint("CheckResult")
class CrashlyticsErrorReporter(
    private val nextReporter: ErrorReporter? = null
) : ErrorReporter {

    init {

    }

    override fun start() {
        // Note: Firebase Crashlytics is automatically started when it is added to a build target
        nextReporter?.start()
    }

    override fun reportHandledException(throwable: Throwable, attributes: Map<String, Any>?) {
        nextReporter?.reportHandledException(throwable, attributes)
    }

    override fun reportMessage(message: String, attributes: Map<String, Any>?) {
        println("Crashlytics : $message")
        nextReporter?.reportMessage(message, attributes)
    }
}
