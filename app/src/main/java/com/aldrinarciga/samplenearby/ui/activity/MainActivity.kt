package com.aldrinarciga.samplenearby.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.aldrinarciga.samplenearby.R
import com.aldrinarciga.samplenearby.model.Endpoint
import com.aldrinarciga.samplenearby.ui.mvp.contract.MainContract
import com.aldrinarciga.samplenearby.ui.mvp.presenter.MainPresenter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.nearby.Nearby



class MainActivity : BaseActivity(), MainContract.View, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    override var TAG = "MainActivity"

    val MY_PERMISSION_REQUEST_LOCATION = 123

    var presenter : MainContract.Presenter? = null
    var googleApiClient : GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupGoogleApiClient()
        setupViews()
    }

    private fun setupGoogleApiClient() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSION_REQUEST_LOCATION)
            return
        }


        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build()
        presenter = MainPresenter(googleApiClient!!, this)
    }

    override fun onStart() {
        super.onStart()
        googleApiClient?.connect()
    }

    override fun onStop() {
        super.onStop()
        googleApiClient?.let {
            if(it.isConnected) {
                it.disconnect()
            }
        }
    }

    fun setupViews() {
        btnAdvertise.setOnClickListener { presenter?.startAdvertising() }
        btnDiscover.setOnClickListener { presenter?.startDiscovering() }
        btnSend.setOnClickListener { presenter?.sendMessageToEndpoints(edtMessage.text.toString()) }
    }

    override fun showMessageViews(visible: Boolean) {
        Log.d(TAG, "showMessageViews")
        initialContainer.visibility = if(!visible) View.VISIBLE else View.GONE
        messagesContainer.visibility = if(visible) View.VISIBLE else View.GONE
    }

    override fun showEndpoint(endpoint: Endpoint) {
        val alertDialog = AlertDialog.Builder(this)
                .setMessage("Endpoint found: ${endpoint.endpointId}, do you want to connect?")
                .setPositiveButton("Yes") { _, _ -> presenter?.initiateConnection(endpoint) }
                .setNegativeButton("No", null)
                .create()

        alertDialog.show()
    }

    override fun showLatestMessage(message: String) {
        txtLatest.text = "Latest Message: $message"
    }

    /**
     * Google Client Callbacks
     */

    override fun onConnected(p0: Bundle?) {
        Log.d(TAG, "Google client success")
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d(TAG, "Google client suspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "Google client failed")
    }
}
