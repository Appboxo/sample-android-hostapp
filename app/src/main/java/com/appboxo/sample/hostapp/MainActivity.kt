package com.appboxo.sample.hostapp

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appboxo.sdk.Appboxo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        demo.setOnClickListener {
            val demoApp = Appboxo.createMiniApp("app16973", "YOUR_PAYLOAD")
            demoApp.setCustomEventListener { activity, miniApp, customEvent ->
                AlertDialog.Builder(activity)
                        .setMessage(customEvent.payload.toString())
                        .setOnCancelListener {
                            customEvent.errorType = "custom_error"
                            miniApp.sendEvent(customEvent)
                        }
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            customEvent.payload = mapOf("custom_data_key" to "custom_data_value")
                            miniApp.sendEvent(customEvent)
                        }
                        .show()
            }
            demoApp.open(this)
        }

        skyscanner.setOnClickListener {
            Appboxo.createMiniApp("id1", "YOUR_PAYLOAD").open(this)
        }

    }
}
