package com.appboxo.sample.hostapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appboxo.data.models.MiniappData
import com.appboxo.sdk.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Appboxo.getMiniapps(object : MiniappListCallback {
            override fun onFailure(e: Exception) {

            }

            override fun onSuccess(miniapps: List<MiniappData>) {
                miniapps.forEach {
                    Log.e("Miniapp", "${it.name}-${it.category}")
                    print(it.appId)
                    print(it.name)
                    print(it.logo)
                    print(it.description)
                    print(it.category)
                }
            }
        })
        val miniapp = Appboxo.getMiniapp("app16973")
        demo.setOnClickListener {
            miniapp.setConfig(
                MiniappConfig.Builder()
                    .setExtraUrlParams(mapOf("customQuery" to "value"))
                    .setCustomActionMenuItem(R.drawable.ic_site_settings)
                    .build()
            )
                .setCustomEventListener { activity, miniapp, customEvent ->
                    AlertDialog.Builder(activity)
                        .setMessage(customEvent.payload.toString())
                        .setOnCancelListener {
                            customEvent.errorType = "custom_error"
                            miniapp.sendEvent(customEvent)
                        }
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            customEvent.payload = mapOf("custom_data_key" to "custom_data_value")
                            miniapp.sendEvent(customEvent)
                            miniapp.hideCustomActionMenuItem()
                        }
                        .show()
                }
                .setPaymentEventListener { appboxoActivity, miniapp, paymentData ->
                    //show payment dialog and send result
                    miniapp.sendPaymentResult(paymentData.apply {
                        this.status = "success"
                        this.hostappOrderId = "123456"
                    })
                }
                .setLifecycleListener(object : Miniapp.LifecycleListener {
                    override fun onLaunch(miniapp: Miniapp) {
                        Log.e("Demo Miniapp", "onLaunch ${miniapp.appId}")
                    }

                    override fun onResume(miniapp: Miniapp) {
                        Log.e("Demo Miniapp", "onResume ${miniapp.appId}")
                        miniapp.showCustomActionMenuItem()
                    }

                    override fun onPause(miniapp: Miniapp) {
                        Log.e("Demo Miniapp", "onPause ${miniapp.appId}")
                    }

                    override fun onClose(miniapp: Miniapp) {
                        Log.e("Demo Miniapp", "onClose ${miniapp.appId}")
                    }

                    override fun onError(miniapp: Miniapp, message: String) {
                    }
                })
                .setUrlChangeListener { appboxoActivity, miniapp, uri ->
                    Log.e(
                        "Demo Miniapp - ${miniapp.appId}",
                        "onUrlChangeListener ${uri.toString()}  "
                    )
                }
                .open(this)
        }

        skyscanner.setOnClickListener {
            Appboxo.getMiniapp("app85076")
                .setLifecycleListener(object : Miniapp.LifecycleListener {
                    override fun onLaunch(miniapp: Miniapp) {
                        //Called when the miniapp will launch with Appboxo.open(...)
                    }

                    override fun onResume(miniapp: Miniapp) {
                        //Called when the miniapp will start interacting with the user
                    }

                    override fun onPause(miniapp: Miniapp) {
                        //Called when the miniapp loses foreground state
                    }

                    override fun onClose(miniapp: Miniapp) {
                        //Called when clicked close button in miniapp or when destroyed miniapp activity
                    }

                    override fun onError(miniapp: Miniapp, message: String) {
                    }
                })
                .open(this)
        }

    }

}
