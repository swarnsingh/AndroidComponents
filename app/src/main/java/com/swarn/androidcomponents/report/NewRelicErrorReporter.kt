package com.swarn.androidcomponents.report

import android.content.Context

class NewRelicErrorReporter(
    private val context: Context,
    private val nextReporter: ErrorReporter? = null
) : ErrorReporter {

    override fun start() {
        nextReporter?.start()
    }

    override fun reportHandledException(throwable: Throwable, attributes: Map<String, Any>?) {
        nextReporter?.reportHandledException(throwable, attributes)
    }

    override fun reportMessage(message: String, attributes: Map<String, Any>?) {
        nextReporter?.reportMessage(message, attributes)
    }

    private fun fetchNewRelicKey(): String {
        return EMPTY_STRING
    }

    companion object {
        private const val NEW_RELIC_KEY = "new_relic_key"
        private const val DEF_TYPE_STRING = "string"
        private const val EMPTY_STRING = ""
    }
}
