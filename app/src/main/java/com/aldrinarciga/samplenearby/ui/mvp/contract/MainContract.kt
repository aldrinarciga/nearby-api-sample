package com.aldrinarciga.samplenearby.ui.mvp.contract

import com.aldrinarciga.samplenearby.model.Endpoint

/**
 * Created by aldrinarciga on 8/2/2017.
 */
interface MainContract {
    interface View : BaseContract.View {
        fun showMessageViews(visible : Boolean)
        fun showEndpoint(endpoint: Endpoint)
        fun showLatestMessage(message: String)
    }

    interface Presenter {
        fun startAdvertising()
        fun stopAdvertising()
        fun startDiscovering()
        fun stopDiscovering()
        fun initiateConnection(endpoint: Endpoint)
        fun sendMessageToEndpoints(message: String)
    }
}