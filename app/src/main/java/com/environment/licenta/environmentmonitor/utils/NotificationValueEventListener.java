package com.environment.licenta.environmentmonitor.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.environment.licenta.environmentmonitor.R;
import com.environment.licenta.environmentmonitor.model.ServiceData;
import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationValueEventListener implements ValueEventListener {
    private Context context;
    private String NOTIFICATION_CHANNEL_ID = "environment_notifications";

    public NotificationValueEventListener(Context context){
        this.context=context;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        putFirebaseData(dataSnapshot);
        ServiceData serviceData=ServiceData.getInstance();
        EnvironmentData lastDatapoint = ServiceData.getInstance().environmentDataList.get(ServiceData.getInstance().environmentDataList.size()-1);

        NotificationManager mNotificationManager =
                (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence name = "environment_notifications";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
        mNotificationManager.createNotificationChannel(mChannel);

        // Pre build the notifications
        Notification temperatureNotification=getTemperatureNotification(Double.parseDouble(lastDatapoint.getTemperature()));
        Notification humidityNotification=getHumidityNotification(Double.parseDouble(lastDatapoint.getHumidity()));
        Notification lightNotification=getLightNotification(Double.parseDouble(lastDatapoint.getLight()));
        Notification noiseNotification=getNoiseNotification(Double.parseDouble(lastDatapoint.getNoise()));
        Notification eCO2Notification=getCO2Notification(Double.parseDouble(lastDatapoint.getECO2()));

        if(serviceData.temperatureEnabled && temperatureNotification!=null){
            mNotificationManager.notify(1, temperatureNotification);
        }
        if(serviceData.humidityEnabled && humidityNotification!=null){
            mNotificationManager.notify(2, humidityNotification);
        }
        if(serviceData.lightEnabled && lightNotification!=null){
            mNotificationManager.notify(3, lightNotification);
        }
        if(serviceData.noiseEnabled && noiseNotification!=null){
            mNotificationManager.notify(4, noiseNotification);
        }
        if(serviceData.eCO2Enabled && eCO2Notification!=null){
            mNotificationManager.notify(5, eCO2Notification);
        }

    }

    private Notification getTemperatureNotification(double currentTemperature){
        ServiceData serviceData=ServiceData.getInstance();
        String title = "";
        String content = "Current temperature: " + currentTemperature + "C";
        if (currentTemperature<serviceData.minTemperature) {
            title = "Temperature too low";
        }
        else if (currentTemperature>serviceData.maxTemperature){
            title = "Temperature too high";
        }
        else{
            return null;
        }

        Notification notification = new Notification.Builder(this.context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.humidity)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build();

        return notification;
    }

    private Notification getHumidityNotification(double currentHumidity){
        ServiceData serviceData=ServiceData.getInstance();
        String title = "";
        String content = "Current humidity: " + currentHumidity + "%";
        if (currentHumidity<serviceData.minHumidity) {
            title = "Humidity too low";
        }
        else if (currentHumidity>serviceData.maxHumidity){
            title = "Humidity too high";
        }
        else{
            return null;
        }

        Notification notification = new Notification.Builder(this.context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.humidity)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build();

        return notification;
    }

    private Notification getNoiseNotification(double currentNoise){
        ServiceData serviceData=ServiceData.getInstance();
        String title = "";
        String content = "Current noise level: " + currentNoise + "dB";
        if (currentNoise<serviceData.minNoise) {
            title = "Noise level too low";
        }
        else if (currentNoise>serviceData.maxNoise){
            title = "Noise level too high";
        }
        else{
            return null;
        }

        Notification notification = new Notification.Builder(this.context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.humidity)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build();

        return notification;
    }

    private Notification getLightNotification(double currentLight){
        ServiceData serviceData=ServiceData.getInstance();
        String title = "";
        String content = "Current light intensity: " + currentLight + "Lux";
        if (currentLight<serviceData.minLight) {
            title = "Light intensity too low";
        }
        else if (currentLight>serviceData.maxLight){
            title = "Light intensity too high";
        }
        else{
            return null;
        }

        Notification notification = new Notification.Builder(this.context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.humidity)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build();

        return notification;
    }

    private Notification getCO2Notification(double currentCO2){
        ServiceData serviceData=ServiceData.getInstance();
        String title = "";
        String content = "Current CO2 concentration: " + currentCO2 + "ppm";
        if (currentCO2<serviceData.minCO2) {
            title = "CO2 concentration too low";
        }
        else if (currentCO2>serviceData.maxCO2){
            title = "CO2 concentration too high";
        }
        else{
            return null;
        }

        Notification notification = new Notification.Builder(this.context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.humidity)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build();

        return notification;
    }

    private void putFirebaseData(DataSnapshot dataSnapshot){
        Iterable<DataSnapshot> childs=dataSnapshot.getChildren();
        ArrayList<EnvironmentData> data=new ArrayList<>();

        for (DataSnapshot snap: childs){
            EnvironmentData env_data=snap.getValue(EnvironmentData.class);
            data.add(env_data);
        }
        Collections.sort(data);
        ArrayList<EnvironmentData> modelInstance= ServiceData.getInstance().environmentDataList;
        modelInstance.removeAll(modelInstance);
        modelInstance.addAll(data);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
