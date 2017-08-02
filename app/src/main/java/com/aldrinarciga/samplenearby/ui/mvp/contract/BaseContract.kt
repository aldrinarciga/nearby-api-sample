package com.aldrinarciga.samplenearby.ui.mvp.contract

/**
 * Created by aldrinarciga on 8/2/2017.
 */
interface BaseContract {
    interface View {
        fun showLoadingDialog(message: String)
        fun hideLoadingDialog()
        fun showMessage(message: String)
    }
}