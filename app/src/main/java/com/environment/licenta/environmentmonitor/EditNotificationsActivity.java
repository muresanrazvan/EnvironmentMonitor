package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.environment.licenta.environmentmonitor.utils.NotificationService;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class EditNotificationsActivity extends Activity {
    private SharedPreferences prefs;

    private TextView minTemperatureText;
    private TextView minHumidityText;
    private TextView minLightText;
    private TextView minNoiseText;
    private TextView minCO2Text;

    private TextView maxTemperatureText;
    private TextView maxHumidityText;
    private TextView maxLightText;
    private TextView maxNoiseText;
    private TextView maxCO2Text;

    private Switch temperatureSwitch;
    private Switch humiditySwitch;
    private Switch lightSwitch;
    private Switch noiseSwitch;
    private Switch CO2Switch;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.edit_notifications);
        prefs = this.getSharedPreferences(
                "com.environment.licenta.environmentmonitor", Context.MODE_PRIVATE);

        minTemperatureText=findViewById(R.id.minTemperature);
        minHumidityText=findViewById(R.id.minHumidity);
        minLightText=findViewById(R.id.minLight);
        minNoiseText=findViewById(R.id.minNoise);
        minCO2Text=findViewById(R.id.minCO2);

        maxTemperatureText=findViewById(R.id.maxTemperature);
        maxHumidityText=findViewById(R.id.maxHumidity);
        maxLightText=findViewById(R.id.maxLight);
        maxNoiseText=findViewById(R.id.maxNoise);
        maxCO2Text=findViewById(R.id.maxCO2);

        temperatureSwitch=findViewById(R.id.temperatureSwitch);
        humiditySwitch=findViewById(R.id.humiditySwitch);
        lightSwitch=findViewById(R.id.lightSwitch);
        noiseSwitch=findViewById(R.id.noiseSwitch);
        CO2Switch=findViewById(R.id.CO2Switch);

        Button saveSettingsButton=findViewById(R.id.saveSettingsButton);
        applySwitchListeners();

        //fill with preferences
        minTemperatureText.setText(""+prefs.getFloat("environment.minTemperature", Context.MODE_PRIVATE));
        minHumidityText.setText(""+prefs.getFloat("environment.minHumidity", Context.MODE_PRIVATE));
        minLightText.setText(""+prefs.getFloat("environment.minLight", Context.MODE_PRIVATE));
        minNoiseText.setText(""+prefs.getFloat("environment.minNoise", Context.MODE_PRIVATE));
        minCO2Text.setText(""+prefs.getFloat("environment.minCO2", Context.MODE_PRIVATE));

        maxTemperatureText.setText(""+prefs.getFloat("environment.maxTemperature", Context.MODE_PRIVATE));
        maxHumidityText.setText(""+prefs.getFloat("environment.maxHumidity", Context.MODE_PRIVATE));
        maxLightText.setText(""+prefs.getFloat("environment.maxLight", Context.MODE_PRIVATE));
        maxNoiseText.setText(""+prefs.getFloat("environment.maxNoise", Context.MODE_PRIVATE));
        maxCO2Text.setText(""+prefs.getFloat("environment.maxCO2", Context.MODE_PRIVATE));

        temperatureSwitch.setChecked(prefs.getBoolean("environment.temperatureAlert",false));
        humiditySwitch.setChecked(prefs.getBoolean("environment.humidityAlert",false));
        lightSwitch.setChecked(prefs.getBoolean("environment.lightAlert",false));
        noiseSwitch.setChecked(prefs.getBoolean("environment.noiseAlert",false));
        CO2Switch.setChecked(prefs.getBoolean("environment.CO2Alert",false));

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putFloat("environment.minTemperature", Float.parseFloat(minTemperatureText.getText().toString())).apply();
                prefs.edit().putFloat("environment.minHumidity", Float.parseFloat(minHumidityText.getText().toString())).apply();
                prefs.edit().putFloat("environment.minLight", Float.parseFloat(minLightText.getText().toString())).apply();
                prefs.edit().putFloat("environment.minNoise", Float.parseFloat(minNoiseText.getText().toString())).apply();
                prefs.edit().putFloat("environment.minCO2", Float.parseFloat(minCO2Text.getText().toString())).apply();

                prefs.edit().putFloat("environment.maxTemperature", Float.parseFloat(maxTemperatureText.getText().toString())).apply();
                prefs.edit().putFloat("environment.maxHumidity", Float.parseFloat(maxHumidityText.getText().toString())).apply();
                prefs.edit().putFloat("environment.maxLight", Float.parseFloat(maxLightText.getText().toString())).apply();
                prefs.edit().putFloat("environment.maxNoise", Float.parseFloat(maxNoiseText.getText().toString())).apply();
                prefs.edit().putFloat("environment.maxCO2", Float.parseFloat(maxCO2Text.getText().toString())).apply();

                prefs.edit().putBoolean("environment.temperatureAlert",temperatureSwitch.isChecked()).apply();
                prefs.edit().putBoolean("environment.humidityAlert", humiditySwitch.isChecked()).apply();
                prefs.edit().putBoolean("environment.lightAlert", lightSwitch.isChecked()).apply();
                prefs.edit().putBoolean("environment.noiseAlert", noiseSwitch.isChecked()).apply();
                prefs.edit().putBoolean("environment.CO2Alert",CO2Switch.isChecked()).apply();

                startService(new Intent(EditNotificationsActivity.this, NotificationService.class));
            }
        });
    }

    private void setFieldsState(){
        minTemperatureText.setEnabled(temperatureSwitch.isChecked());
        maxTemperatureText.setEnabled(temperatureSwitch.isChecked());
        minHumidityText.setEnabled(humiditySwitch.isChecked());
        maxHumidityText.setEnabled(humiditySwitch.isChecked());
        minLightText.setEnabled(lightSwitch.isChecked());
        maxLightText.setEnabled(lightSwitch.isChecked());
        minNoiseText.setEnabled(noiseSwitch.isChecked());
        maxNoiseText.setEnabled(noiseSwitch.isChecked());
        minCO2Text.setEnabled(CO2Switch.isChecked());
        maxCO2Text.setEnabled(CO2Switch.isChecked());

    }

    private void applySwitchListeners(){
        CompoundButton.OnCheckedChangeListener changeListener= new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setFieldsState();
            }
        };
        temperatureSwitch.setOnCheckedChangeListener(changeListener);
        humiditySwitch.setOnCheckedChangeListener(changeListener);
        lightSwitch.setOnCheckedChangeListener(changeListener);
        noiseSwitch.setOnCheckedChangeListener(changeListener);
        CO2Switch.setOnCheckedChangeListener(changeListener);
    }
}
