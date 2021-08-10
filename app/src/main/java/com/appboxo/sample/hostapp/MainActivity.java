package com.appboxo.sample.hostapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.appboxo.js.params.CustomEvent;
import com.appboxo.sdk.Appboxo;
import com.appboxo.sdk.Miniapp;
import com.appboxo.ui.main.AppboxoActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.demo)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Miniapp demo = Appboxo.INSTANCE.getMiniapp("app16973");
                        demo.setCustomEventListener(new Miniapp.CustomEventListener() {
                            @Override
                            public void handle(@NotNull AppboxoActivity activity, @NotNull final Miniapp miniApp, @NotNull final CustomEvent customEvent) {
                                new AlertDialog.Builder(activity)
                                        .setMessage(customEvent.getPayload().toString())
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                customEvent.setErrorType("custom_error");
                                                miniApp.sendEvent(customEvent);
                                            }
                                        })
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Map<String, String> payload = new HashMap<>();
                                                payload.put("custom_data_key", "custom_data_value");
                                                customEvent.setPayload(payload);
                                                miniApp.sendEvent(customEvent);
                                            }
                                        })
                                        .show();

                            }
                        });
                        demo.open(MainActivity.this);
                    }
                });

        findViewById(R.id.skyscanner)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Appboxo.INSTANCE.getMiniapp("app85076")
                                .setLifecycleListener(new Miniapp.LifecycleListener() {
                                    @Override
                                    public void onLaunch(@NotNull Miniapp miniApp) {

                                    }

                                    @Override
                                    public void onResume(@NotNull Miniapp miniApp) {

                                    }

                                    @Override
                                    public void onPause(@NotNull Miniapp miniApp) {

                                    }

                                    @Override
                                    public void onClose(@NotNull Miniapp miniApp) {

                                    }

                                    @Override
                                    public void onError(@NotNull Miniapp miniApp, @NotNull String s) {

                                    }
                                })
                                .open(MainActivity.this);
                    }
                });
    }
}
