package com.android.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class MainActivity extends AppCompatActivity {

    //MQTT
    private MqttAndroidClient client;
    private String TAG = "MainActivity";
    private PahoMqttClient pahoMqttClient;
    StickySwitch s1,s2;
    RelativeLayout r1;
    FloatingActionButton f1,f2,f3,f4;

    String topic = "bellax/ack";

    //Broadcast Recieve
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String string = bundle.getString("message");
                StatusCheck(string);
            } else {
                Toast.makeText(MainActivity.this, "RIP",
                        Toast.LENGTH_LONG).show();

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        r1 = findViewById(R.id.main_activity);
        s1 = findViewById(R.id.switch_suction);
        s2 = findViewById(R.id.switch_pick);
        f1 = findViewById(R.id.floatingActionButton);
        f2 = findViewById(R.id.floatingActionButton2);
        f3 = findViewById(R.id.floatingActionButton3);
        f4 = findViewById(R.id.floatingActionButton4);

        s1.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String text) {
                if(direction.name().equals("RIGHT")) {
                    s1.setSwitchColor(getResources().getColor(R.color.green));
                    //command("ST:");
                } else {
                    s1.setSwitchColor(getResources().getColor(R.color.red));
                    //command("SF");
                }
            }
        });

        s2.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String text) {
                if(direction.name().equals("RIGHT")) {
                    s2.setSwitchColor(getResources().getColor(R.color.green));
                    //command("PU:");
                } else {
                    s2.setSwitchColor(getResources().getColor(R.color.red));
                    //command("PD:");
                }
            }
        });



        registerReceiver(receiver, new IntentFilter(
                MqttMessageService.NOTIFICATION));

        //MQTT
        pahoMqttClient = new PahoMqttClient();
        client = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);

        Intent intent = new Intent(MainActivity.this, MqttMessageService.class);
        startService(intent);

        acceptTouch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_subscribe) {
            subscribeMQTT();
            return true;
        } else if (id == R.id.action_unsubscribe) {
            unSubscribeMQTT();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void acceptTouch() {
        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command("FT");
            }
        });
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command("LT");
            }
        });
        f3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command("RT");
            }
        });
        f4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command("BT");
            }
        });
    }

    public void command(String msg) {
        if (!msg.isEmpty()) {
            try {
                pahoMqttClient.publishMessage(client, msg, 1, Constants.PUBLISH_TOPIC);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void subscribeMQTT() {
        String topic = "bellax/ack";
        try {
            pahoMqttClient.subscribe(client, topic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unSubscribeMQTT() {
        String topic = "bellax/ack";
        if (!topic.isEmpty()) {
            try {
                pahoMqttClient.unSubscribe(client, topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void StatusCheck(String s)
    {
        //todo
        Log.e("Received: ", s);
    }
}
