package com.aldrinarciga.samplenearby.ui.mvp.presenter

import android.util.Log
import com.aldrinarciga.samplenearby.extensions.toEndpointIdList
import com.aldrinarciga.samplenearby.model.Constants
import com.aldrinarciga.samplenearby.model.Endpoint
import com.aldrinarciga.samplenearby.ui.mvp.contract.MainContract
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.nio.charset.Charset

/**
 * Created by aldrinarciga on 8/2/2017.
 */
class MainPresenter(val googleApiClient: GoogleApiClient,
                    val view: MainContract.View?) : MainContract.Presenter {


    val TAG = "MainPresenter"

    var endpoints : MutableList<Endpoint> = arrayListOf()
    var isDiscoverer = false

    override fun startAdvertising() {
        Log.d(TAG, "connected: ${googleApiClient.isConnected}")
        isDiscoverer = false
        Nearby.Connections.startAdvertising(googleApiClient, Constants.ADVERTISER, Constants.SERVICE_ID, getConnLifecycle(), AdvertisingOptions(Strategy.P2P_STAR))
                .setResultCallback {
                    if(it.status.isSuccess) {
                        view?.showMessage("Advertising successful!")
                        view?.showMessageViews(true)
                    } else {
                        view?.showMessage("Error advertising")
                        Log.d(TAG, "${it.status.statusCode}")
                    }
                }
    }

    override fun stopAdvertising() {
        Nearby.Connections.stopAdvertising(googleApiClient)
    }

    override fun startDiscovering() {
        isDiscoverer = true
        Nearby.Connections.startDiscovery(googleApiClient, Constants.SERVICE_ID, getEndpointDiscovery(), DiscoveryOptions(Strategy.P2P_STAR))
                .setResultCallback {
                    if(it.status.isSuccess) {
                        view?.showLoadingDialog("Searching for endpoints")
                    } else {
                        view?.showMessage("Error discovering")
                    }
                }
    }

    override fun stopDiscovering() {
        Nearby.Connections.stopDiscovery(googleApiClient)
    }

    override fun initiateConnection(endpoint: Endpoint) {
        view?.showLoadingDialog("Initiating connection")
        Nearby.Connections.requestConnection(googleApiClient, "Someone", endpoint.endpointId, getConnLifecycle())
                .setResultCallback {
                    if(it.status.isSuccess) {
                        //success
                    } else {
                        view?.hideLoadingDialog()
                        view?.showMessage("Error")
                        Log.d(TAG, "error: ${it.status.statusCode}")
                    }
                }
    }

    override fun sendMessageToEndpoints(message: String) {
        if(endpoints.size > 0) {
            Nearby.Connections.sendPayload(googleApiClient, endpoints.toEndpointIdList(), Payload.fromBytes(message.toByteArray(Charsets.UTF_8)))
        }
    }

    fun getConnLifecycle() : ConnectionLifecycleCallback {
        return object : ConnectionLifecycleCallback() {
            override fun onConnectionResult(endpointId: String?, info: ConnectionResolution?) {
                view?.hideLoadingDialog()
                var mess = when(info?.status?.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        endpoints.add(Endpoint(endpointId, null))
                        view?.showMessageViews(true)
                        "Connection OK"
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> "Rejected"
                    else -> "Some other reason"
                }

                Log.d(TAG, mess)
            }

            override fun onDisconnected(endpointId: String?) {
                if(isDiscoverer) {
                    stopDiscovering()
                } else {
                    stopAdvertising()
                }
                view?.hideLoadingDialog()
                view?.showMessageViews(false)
                view?.showMessage("Disconnected")
            }

            override fun onConnectionInitiated(endpointId: String?, info: ConnectionInfo?) {
                view?.hideLoadingDialog()
                //auto accept for both
                Nearby.Connections.acceptConnection(googleApiClient, endpointId, getPayloadCallback())
            }
        }
    }

    fun getPayloadCallback() : PayloadCallback {
        return object : PayloadCallback(){
            override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
                if(payload?.type == Payload.Type.BYTES) {
                    val message = kotlin.text.String(payload.asBytes()!!, Charset.forName("UTF-8"))
                    view?.showMessage(message)
                    view?.showLatestMessage(message)
                }
            }

            override fun onPayloadTransferUpdate(p0: String?, p1: PayloadTransferUpdate?) {

            }
        }
    }

    fun getEndpointDiscovery() : EndpointDiscoveryCallback {
        return object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String?, info: DiscoveredEndpointInfo?) {
                Log.d(TAG, "onEndpointFound: $endpointId")
                stopDiscovering()
                view?.hideLoadingDialog()
                view?.showEndpoint(Endpoint(endpointId, info))
            }

            override fun onEndpointLost(endpointId: String?) {

            }
        }
    }
}