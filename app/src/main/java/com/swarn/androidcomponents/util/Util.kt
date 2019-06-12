package com.swarn.androidcomponents.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * @author Swarn Singh.
 */
object Util {

    private lateinit var alertDialog: AlertDialog

    fun openApplicationDetailSettingDialog(activity: Activity, message: String) {
        if (::alertDialog.isInitialized) {
            alertDialog.dismiss()
        }
        val builder = AlertDialog.Builder(activity).apply {
            setMessage(message)
            setCancelable(false)
            setPositiveButton("Permit Manually") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                context?.startActivity(intent)
            }
            setNegativeButton("Cancel", null)
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}