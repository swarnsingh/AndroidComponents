package com.swarn.androidcomponents.fragment


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView
import com.swarn.androidcomponents.R


const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 20

class ContentProviderFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private val TAG = ContentProviderFragment::class.java.canonicalName

    private lateinit var mRecyclerView: RecyclerView

    private val mColumnProjection = arrayOf(
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        ContactsContract.Contacts.CONTACT_STATUS,
        ContactsContract.Contacts.HAS_PHONE_NUMBER
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mRecyclerView = activity!!.findViewById(R.id.content_provider_recycler_view)
        getContacts()
    }

    private fun getContacts() {
        if (checkPermission()) {
           // loaderManager.initLoader(1, null, this);
            LoaderManager.getInstance<Fragment>(this).initLoader(1, null, this);
        } else {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.WRITE_CONTACTS
                ),
                MY_PERMISSIONS_REQUEST_READ_CONTACTS
            )
        }
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            activity!!, android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(
            activity!!, android.Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED))
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoaderManager.getInstance<Fragment>(this).initLoader(1, null, this);
                }
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        return CursorLoader(
            activity!!,
            ContactsContract.Contacts.CONTENT_URI,
            mColumnProjection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                Log.d(
                    TAG,
                    cursor.getString(0) + " , " + cursor.getString(1) + " , " + cursor.getString(2) + "\n"
                )
            }
        } else {
            Log.d(TAG, "No Contacts")
        }

        cursor?.close()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
