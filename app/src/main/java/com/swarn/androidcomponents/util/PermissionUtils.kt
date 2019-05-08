package com.swarn.androidcomponents.util

import android.Manifest.permission
import android.app.Activity
import android.content.Context


/**
 * @author Swarn Singh.
 */
class PermissionUtils {

    companion object {
        fun isNeverAskAgainSelected(activity: Activity): Boolean {
            val genPrefs = activity.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE)
            return genPrefs.getBoolean(permission.ACCESS_FINE_LOCATION, false)
        }

        fun setNeverAskAgainStatus(context: Context) {
            val genPrefs = context.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE)
            val editor = genPrefs.edit()
            editor.putBoolean(permission.ACCESS_FINE_LOCATION, true)
            editor.commit()
        }
    }
}