package com.swarn.androidcomponents.report

/**
 * Base interface for any error reporting tool.
 */
interface ErrorReporter {

    /**
     * Initialize and start error reporting.
     */
    fun start()

    /**
     * Report a non-fatal error or exception handled by the app.
     *
     * @param throwable error or exception being handled
     * @param attributes custom attributes appended to the handled error or exception
     */
    fun reportHandledException(throwable: Throwable, attributes: Map<String, Any>? = null)

    /**
     * Log message which in turn gets appended to a future error report. Typically used to better
     * understand and debug the interactions and events leading to an error.
     *
     * @param message recorded message appended to a future error report
     * @param attributes custom attributes appended to a future error report
     */
    fun reportMessage(message: String, attributes: Map<String, Any>? = null)
}
