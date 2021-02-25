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
        val agodaInfo = AgodaInfo()
        miniappButton.setOnClickListener {
            Appboxo.getMiniapp("miniapp", "")
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
                    Log.e("path", uri.path)
                    uri.queryParameterNames.forEach {
                        Log.e("param", "$it = ${uri.getQueryParameter(it)}")
                    }

                    with(agodaInfo) {
                        uri.getQueryParameter("city")?.let { city = it }
                        uri.getQueryParameter("cid")?.let { cid = it }
                        uri.getQueryParameter("checkIn")?.let { checkIn = it }
                        uri.getQueryParameter("checkOut")?.let { checkOut = it }
                        uri.getQueryParameter("los")?.let { los = it }
                        uri.getQueryParameter("rooms")?.let { rooms = it }
                        uri.getQueryParameter("adults")?.let { adults = it }
                        uri.getQueryParameter("children")?.let { children = it }
                        uri.getQueryParameter("userId")?.let { userId = it }
                        uri.getQueryParameter("origin")?.let { origin = it }
                        uri.getQueryParameter("currencyCode")?.let { currencyCode = it }
                        uri.getQueryParameter("textToSearch")?.let { textToSearch = it }
                    }
                    if (uri.path?.contains("/hotel") == true) {
                        with(agodaInfo) {
                            runCatching {
                                uri.lastPathSegment?.replace(".html", "")
                                    ?.split("-")?.let {
                                        cityName = it[0]
                                        countryCode = it[1]
                                    }
                                hotel =
                                    uri.pathSegments?.toMutableList()?.apply { reverse() }?.get(2)
                            }
                        }
                    }
                    if (uri.path?.contains("/thankyou") == true) {
                        agodaInfo.bookingIdUrl = uri.toString()
                        agodaInfo.bookingId = uri.getQueryParameter("bookingId")
                    }
                    println(agodaInfo)
                }
                .setCustomActionMenuItemClickListener { appboxoActivity, miniapp ->
                    CardDialog(appboxoActivity).show()
                }
                .open(this)
        }
    }
}
