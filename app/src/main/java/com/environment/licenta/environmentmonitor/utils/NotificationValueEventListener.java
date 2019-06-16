package com.environment.licenta.environmentmonitor.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.environment.licenta.environmentmonitor.R;
import com.environment.licenta.environmentmonitor.model.Constants;
import com.environment.licenta.environmentmonitor.model.ProgramData;
import com.environment.licenta.environmentmonitor.model.ServiceData;
import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class NotificationValueEventListener implements ValueEventListener, Constants {
    private Context context;
    private String NOTIFICATION_CHANNEL_ID = "environment_notifications";

    public NotificationValueEventListener(Context context){
        this.context=context;
    }

    private DataPoint[] getTemperatureDatapoints(){
        ArrayList<EnvironmentData> env_data= ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[LAST_POINTS_ANALYZED];
        int startIndex=env_data.size()-LAST_POINTS_ANALYZED;
        startIndex = startIndex>0?startIndex:0;
        for(int i =0;i<LAST_POINTS_ANALYZED;i++) {
            Date date = new Date();
            date.setTime(env_data.get(i+startIndex).getTimestamp());
            datapoints[i] = new DataPoint(date, Double.parseDouble(env_data.get(i+startIndex).getTemperature()));
        }

        return datapoints;
    }

    private DataPoint[] getTVOCDatapoints(){
        ArrayList<EnvironmentData> env_data= ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[LAST_POINTS_ANALYZED];
        int startIndex=env_data.size()-LAST_POINTS_ANALYZED;
        startIndex = startIndex>0?startIndex:0;
        for(int i =0;i<LAST_POINTS_ANALYZED;i++) {
            Date date = new Date();
            date.setTime(env_data.get(i+startIndex).getTimestamp());
            datapoints[i] = new DataPoint(date, Double.parseDouble(env_data.get(i+startIndex).getTVOC()));
        }

        return datapoints;
    }

    public LineEquation getBestFitLineEquation(DataPoint[] datapoints){
        double ys[]=new double[datapoints.length];
        double xs[]=new double[datapoints.length];
        for (int i = 0; i<ys.length; i++){
            ys[i]=datapoints[i].getY();
            xs[i]=datapoints[i].getX();
        }
        return new LineEquation(xs,ys);
    }

    public DataPoint[] getCorrelationDatapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[10];
        int index=0;

        for (int i = 126; i<136;i++) {
            datapoints[index++] = new DataPoint(Double.parseDouble(env_data.get(i).getTemperature()), Double.parseDouble(env_data.get(i).getTVOC()));
        }
        Utilities.sortDatapointsAscendingByX(datapoints);
        return datapoints;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        putFirebaseData(dataSnapshot);
        ServiceData serviceData=ServiceData.getInstance();
        ArrayList<EnvironmentData> environmentDataList = ServiceData.getInstance().environmentDataList;
        EnvironmentData lastDatapoint = environmentDataList.get(environmentDataList.size()-1);

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
        Notification fireAlertNotification=getFireAlertNotification();

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
        if(fireAlertNotification!=null){
            mNotificationManager.notify(6,fireAlertNotification);
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

    private Notification getFireAlertNotification(){
        String title = "Fire Alert";
        String content = "Rapid rise of temperature and TVOC detected";
        DataPoint correlationDatapoints[] = getCorrelationDatapoints();
        DataPoint temperatureDatapoints[] = getTemperatureDatapoints();
        DataPoint TVOCDatapoints[] = getTVOCDatapoints();
        Correlation temperatureTVOCCorrelation = new Correlation(correlationDatapoints);
        LineEquation temperatureLineEquation = Utilities.getBestFitLineEquation(temperatureDatapoints,
                                                                        temperatureDatapoints.length);
        LineEquation TVOCLineEquation = Utilities.getBestFitLineEquation(TVOCDatapoints,TVOCDatapoints.length);

        // if temperature and TVOC are not strongly correlated, there is no fire
        if(temperatureTVOCCorrelation.getCorrelationCoefficient()<0.7){
            return null;
        }
        // if the predicted temperature is safe, no fire
        if(temperatureLineEquation.getY(PREDICTED_POINTS + LAST_POINTS_ANALYZED) <= MAX_SAFE_TEMPERATURE){
            return null;
        }
        // if the total volatile organic compounds are projected at safe levels, no fire
        if(TVOCLineEquation.getY(PREDICTED_POINTS + LAST_POINTS_ANALYZED) <= MAX_SAFE_TVOC){
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
