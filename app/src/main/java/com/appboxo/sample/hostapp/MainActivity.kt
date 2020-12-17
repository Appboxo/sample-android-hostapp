package com.appboxo.sample.hostapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appboxo.sdk.Appboxo
import com.appboxo.sdk.MiniappConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        miniappButton.setOnClickListener {
            Appboxo.getMiniapp("[MINIAPP_ID]", "")
                .setConfig(
                    MiniappConfig.Builder()
                        .setExtraUrlParams(
                            mapOf(
                                "extra_param_1" to "test",
                                "extra_param_2" to "test_2"
                            )
                        )
                        .setCustomActionMenuItem(R.drawable.ic_custom_menu)
                        .build()
                )
                .setUrlChangeListener { appboxoActivity, miniapp, uri ->
                    Log.e("URL_Path", uri.path)
                    uri.queryParameterNames.forEach {
                        Log.e("URL_param", "$it = ${uri.getQueryParameter(it)}")
                    }
                    if (uri.path?.contains("/book") == true)
                        miniapp.showCustomActionMenuItem()
                    else
                        miniapp.hideCustomActionMenuItem()
                }
                .setCustomActionMenuItemClickListener { appboxoActivity, miniapp ->
                    CardDialog(appboxoActivity).show()
                }
                .open(this)
        }
    }
}
