package com.swarn.androidcomponents.report

import android.annotation.SuppressLint
import android.util.Log
import com.swarn.androidcomponents.BuildConfig
import timber.log.Timber

@SuppressLint("CheckResult")
class ErrorReportingTree(
    private val errorReporter: ErrorReporter
) : Timber.Tree() {

    private var logLevel = Log.ERROR

    init {
        logLevel = Log.ERROR
    }

    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        // Invoking super.log() outputs to Logcat. Should call when this is the only Tree plan/*ted/**priority/*/ted.
        if (Timber.treeCount == 1) {
            super.log(priority, tag, message, throwable)
        }

        // Report logs as handled exceptions or breadcrumbs based on logLevel defined in Config.
        // Default log level will only report on Log.ERROR and Log.ASSERT.
        // Note: Decrementing log level will increase the number of handled exceptions and breadcrumbs.
        if (priority >= logLevel) {
            val attributes = mapOf(
                "logLevel" to toLogLevelString(priority),
                "tag" to (tag ?: "")
            )

            if (null != throwable) {
                errorReporter.reportHandledException(throwable, attributes)
            } else {
                errorReporter.reportMessage(message, attributes)
            }
        }
    }

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return if (BuildConfig.BUILD_TYPE.equals("debug", true)) {
            true
        } else {
            priority >= logLevel
        }
    }

    private fun toLogLevelString(priority: Int) = when (priority) {
        Log.VERBOSE -> "Verbose"
        Log.DEBUG -> "Debug"
        Log.INFO -> "Info"
        Log.WARN -> "Warn"
        Log.ERROR -> "Error"
        Log.ASSERT -> "Assert"
        else -> "Unknown"
    }

    companion object {
        fun newInstance(
            errorReporter: ErrorReporter
        ): Timber.Tree {
            return if (BuildConfig.BUILD_TYPE.equals("debug", true))
                NoOpErrorReportingTree() else ErrorReportingTree(errorReporter)
        }
    }
}

private class NoOpErrorReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val s = 0
    }
}
