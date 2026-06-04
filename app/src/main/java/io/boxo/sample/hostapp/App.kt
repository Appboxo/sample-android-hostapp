package io.boxo.sample.hostapp

import android.app.Application
import io.boxo.sdk.Boxo
import io.boxo.sdk.Config

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Boxo.init(this)
            .setConfig(
                Config.Builder()
                    .setClientId("CLIENT_ID")
                    .setLanguage("en")
                    .setTheme(Config.Theme.SYSTEM)
                    .build()
            )
    }
}