package com.environment.licenta.environmentmonitor.utils;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.environment.licenta.environmentmonitor.R;
import com.environment.licenta.environmentmonitor.model.ProgramData;
import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class EnvironmentValueEventListener implements ValueEventListener {
    private Activity activity;

    public EnvironmentValueEventListener(Activity activity){
        this.activity=activity;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> childs=dataSnapshot.getChildren();
        ArrayList<EnvironmentData> data=new ArrayList<>();

        for (DataSnapshot snap: childs){
            EnvironmentData env_data=snap.getValue(EnvironmentData.class);
            data.add(env_data);
        }
        Collections.sort(data);
        ArrayList<EnvironmentData> modelInstance=ProgramData.getInstance().environmentDataList;
        modelInstance.removeAll(modelInstance);
        modelInstance.addAll(data);

        TextView mainTemperature=activity.findViewById(R.id.mainTemperatureId);
        mainTemperature.setText(modelInstance.get(modelInstance.size()-1).getTemperature()+"\u00b0C");
        mainTemperature.setVisibility(View.VISIBLE);

        TextView mainHumidity=activity.findViewById(R.id.mainHumidityId);
        mainHumidity.setText("Humidity: "+modelInstance.get(modelInstance.size()-1).getHumidity()+"%");
        mainHumidity.setVisibility(View.VISIBLE);

        Button noiseButton=activity.findViewById(R.id.noiseButtonId);
        noiseButton.setEnabled(true);
        Button airQualityButton=activity.findViewById(R.id.airQualityButtonId);
        airQualityButton.setEnabled(true);
        Button lightButton=activity.findViewById(R.id.lightButtonId);
        lightButton.setEnabled(true);
        Button temperatureButton=activity.findViewById(R.id.temperatureButtonId);
        temperatureButton.setEnabled(true);
        Button humidityButton=activity.findViewById(R.id.humidityButtonId);
        humidityButton.setEnabled(true);
        Button editNotificationButton=activity.findViewById(R.id.settingsId);
        editNotificationButton.setEnabled(true);

        Date d = new Date();
        d.setTime(modelInstance.get(modelInstance.size()-1).getTimestamp());
        System.out.println("date=========="+d);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
