package com.appboxo.sample.hostapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appboxo.sdk.Appboxo
import com.appboxo.sdk.Miniapp
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        demo.setOnClickListener {
            Appboxo.getMiniapp("app16973", "YOUR_AUTH_PAYLOAD")
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
                        }
                        .show()
                }
                .open(this)
        }

        skyscanner.setOnClickListener {
            Appboxo.getMiniapp("app85076", "YOUR_AUTH_PAYLOAD")
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

        appboxo_store.setOnClickListener {
            Appboxo.getMiniapp("app36902", "YOUR_AUTH_PAYLOAD")
                .setCustomEventListener { activity, miniapp, customEvent ->
                    val price = customEvent.payload["price"] as Int
                    val name = customEvent.payload["name"] as String
                    val currency = customEvent.payload["currency"] as String
                    StripeDialog(activity, price, currency, name).apply {
                        doOnSuccess = {
                            customEvent.payload = mapOf("payment" to "received")
                            miniapp.sendEvent(customEvent)
                        }
                        activity.doOnActivityResult { requestCode, resultCode, data ->
                            this.onActivityResult(requestCode, resultCode, data)
                        }
                    }.show()
                }
                .open(this)
        }

        checkPush(intent)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) return@OnCompleteListener
                val token = task.result?.token
                Log.d("TOKEN", token)
            })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        checkPush(intent)
    }

    private fun checkPush(intent: Intent) {
        val appId = intent.getStringExtra("miniapp_id") ?: ""
        if (appId.isNotBlank()) {
            Appboxo.getMiniapp(appId, "YOUR_AUTH_PAYLOAD").open(this)
        }
    }

}
