package com.aldrinarciga.samplenearby.ui.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.aldrinarciga.samplenearby.ui.mvp.contract.BaseContract

/**
 * Created by aldrinarciga on 8/2/2017.
 */
abstract class BaseActivity : AppCompatActivity(), BaseContract.View {
    open var TAG = "BaseActivity"
    var progressDialog : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressDialog = ProgressDialog(this)
        progressDialog?.setCancelable(false)
    }

    override fun showLoadingDialog(message: String) {
        progressDialog?.setMessage(message)
        progressDialog?.show()
    }

    override fun hideLoadingDialog() {
        progressDialog?.let {
            if(it.isShowing) {
                it.cancel()
            }
        }
    }

    override fun showMessage(message: String) {
        Log.d(TAG, message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}