package com.appboxo.sample.hostapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.appboxo.js.params.CustomEvent;
import com.appboxo.sdk.Appboxo;
import com.appboxo.sdk.MiniApp;

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
                        MiniApp demo = Appboxo.INSTANCE.createMiniApp("app16973", "YOUR_PAYLOAD");
                        demo.setCustomEventListener(new MiniApp.CustomEventListener() {
                            @Override
                            public void handle(@NotNull Activity activity, @NotNull final MiniApp miniApp, @NotNull final CustomEvent customEvent) {
                                new AlertDialog.Builder(activity)
                                        .setMessage(customEvent.payload.toString())
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                customEvent.errorType = "custom_error";
                                                miniApp.sendEvent(customEvent);
                                            }
                                        })
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Map<String, String> payload = new HashMap<>();
                                                payload.put("custom_data_key", "custom_data_value");
                                                customEvent.payload = payload;
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
                        Appboxo.INSTANCE.createMiniApp("id1", "YOUR_PAYLOAD").open(MainActivity.this);
                    }
                });
    }
}
