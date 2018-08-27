package com.environment.licenta.environmentmonitor.utils;

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

    public NotificationValueEventListener(Context context){
        this.context=context;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        putFirebaseData(dataSnapshot);
        EnvironmentData lastDatapoint = ServiceData.getInstance().environmentDataList.get(ServiceData.getInstance().environmentDataList.size()-1);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);

        notificationManager.notify(2, getTemperatureNotification(Double.parseDouble(lastDatapoint.getTemperature())).build());
    }

    private NotificationCompat.Builder getTemperatureNotification(double currentTemperature){
        ServiceData serviceData=ServiceData.getInstance();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, "channel_id")
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (currentTemperature<serviceData.minTemperature){
            mBuilder.setContentTitle("Temperature too low")
                    .setContentText("Current temperature: "+currentTemperature+"C");
        }
        if (currentTemperature>serviceData.maxTemperature){
            mBuilder.setContentTitle("Temperature too high")
                    .setContentText("Current temperature: "+currentTemperature+"C");
        }

        return mBuilder;
    }

    private NotificationCompat.Builder getHumidityNotification(double currentHumidity){
        ServiceData serviceData=ServiceData.getInstance();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, "channel_id")
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (currentHumidity<serviceData.minHumidity){
            mBuilder.setContentTitle("Humidity Too Low")
                    .setContentText("Current Humidity: "+currentHumidity+"%");
        }
        if (currentHumidity>serviceData.maxHumidity){
            mBuilder.setContentTitle("Humidity Too High")
                    .setContentText("Current Humidity: "+currentHumidity+"%");
        }

        return mBuilder;
    }

    private NotificationCompat.Builder getNoiseNotification(double currentNoise){
        ServiceData serviceData=ServiceData.getInstance();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, "channel_id")
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (currentNoise<serviceData.minNoise){
            mBuilder.setContentTitle("Noise Too Low")
                    .setContentText("Current Noise: "+currentNoise+"dB");
        }
        if (currentNoise>serviceData.maxNoise){
            mBuilder.setContentTitle("Noise Too High")
                    .setContentText("Current Noise: "+currentNoise+"dB");
        }

        return mBuilder;
    }

    private NotificationCompat.Builder getLightNotification(double currentLight){
        ServiceData serviceData=ServiceData.getInstance();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, "channel_id")
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (currentLight<serviceData.minLight){
            mBuilder.setContentTitle("Light Too Low")
                    .setContentText("Current Light: "+currentLight+"Lux");
        }
        if (currentLight>serviceData.maxLight){
            mBuilder.setContentTitle("Light Too High")
                    .setContentText("Current Light: "+currentLight+"Lux");
        }

        return mBuilder;
    }

    private NotificationCompat.Builder getCO2Notification(double currentCO2){
        ServiceData serviceData=ServiceData.getInstance();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, "channel_id")
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (currentCO2<serviceData.minCO2){
            mBuilder.setContentTitle("CO2 Too Low")
                    .setContentText("Current eCO2: "+currentCO2+"ppm");
        }
        if (currentCO2>serviceData.maxCO2){
            mBuilder.setContentTitle("CO2 Too High")
                    .setContentText("Current eCO2: "+currentCO2+"ppm");
        }

        return mBuilder;
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
