package com.swarn.androidcomponents.transition

import android.content.Context

/**
 * @author Swarn Singh.
 */
abstract class TransitionRecognitionAbstract {
    abstract fun startTracking(context: Context)
    abstract fun stopTracking(context: Context)
}
