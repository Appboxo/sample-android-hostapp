package com.appboxo.sample.hostapp

import android.app.Application
import com.appboxo.log.DefaultLogger
import com.appboxo.sdk.Appboxo
import com.appboxo.sdk.Config

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Appboxo.init(this)
            .setConfig(
                Config.Builder()
                    .setClientId(BuildConfig.APPBOXO_CLIENT_ID)
                    .multitaskMode(true)
                    .setTheme(Config.Theme.SYSTEM)
                    .build()
            )
            .setLogger(DefaultLogger(BuildConfig.DEBUG))
    }
}