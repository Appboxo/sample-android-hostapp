package com.appboxo.sample.hostapp;

import android.app.Application;

import com.appboxo.log.DefaultLogger;
import com.appboxo.sdk.Appboxo;
import com.appboxo.sdk.Config;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Appboxo.INSTANCE.init(this)
                .setConfig(new Config.Builder()
                        .setClientId("YOUR_CLIENT_ID")
                        .build())
                .setLogger(new DefaultLogger(BuildConfig.DEBUG));
    }
}
