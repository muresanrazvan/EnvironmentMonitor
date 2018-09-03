package com.environment.licenta.environmentmonitor.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.environment.licenta.environmentmonitor.model.ProgramData;
import com.environment.licenta.environmentmonitor.model.ServiceData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new NotificationValueEventListener(this));

        ServiceData serviceData=ServiceData.getInstance();
        SharedPreferences prefs = this.getSharedPreferences("com.environment.licenta.environmentmonitor", Context.MODE_PRIVATE);

        serviceData.minTemperature=prefs.getFloat("environment.minTemperature", Context.MODE_PRIVATE);
        serviceData.minHumidity=prefs.getFloat("environment.minHumidity", Context.MODE_PRIVATE);
        serviceData.minLight=prefs.getFloat("environment.minLight", Context.MODE_PRIVATE);
        serviceData.minNoise=prefs.getFloat("environment.minNoise", Context.MODE_PRIVATE);
        serviceData.minCO2=prefs.getFloat("environment.minCO2", Context.MODE_PRIVATE);

        serviceData.maxTemperature=prefs.getFloat("environment.maxTemperature", Context.MODE_PRIVATE);
        serviceData.maxHumidity=prefs.getFloat("environment.maxHumidity", Context.MODE_PRIVATE);
        serviceData.maxLight=prefs.getFloat("environment.maxLight", Context.MODE_PRIVATE);
        serviceData.maxNoise=prefs.getFloat("environment.maxNoise", Context.MODE_PRIVATE);
        serviceData.maxCO2=prefs.getFloat("environment.maxCO2", Context.MODE_PRIVATE);

        serviceData.temperatureEnabled=prefs.getBoolean("environment.temperatureAlert",false);
        serviceData.humidityEnabled=prefs.getBoolean("environment.humidityAlert",false);
        serviceData.lightEnabled=prefs.getBoolean("environment.lightAlert",false);
        serviceData.noiseEnabled=prefs.getBoolean("environment.noiseAlert",false);
        serviceData.eCO2Enabled=prefs.getBoolean("environment.CO2Alert",false);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }
}
