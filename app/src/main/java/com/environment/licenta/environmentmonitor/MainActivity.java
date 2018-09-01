package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.environment.licenta.environmentmonitor.model.ProgramData;
import com.environment.licenta.environmentmonitor.utils.EnvironmentValueEventListener;
import com.environment.licenta.environmentmonitor.utils.NotificationService;
import com.environment.licenta.environmentmonitor.utils.StartActivityOnClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class ExitEventListener implements View.OnClickListener{
    private Activity activity;
    public ExitEventListener(Activity activity){
        this.activity=activity;
    }
    @Override
    public void onClick(View v) {
        activity.finish();
        System.exit(0);
    }
}

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new EnvironmentValueEventListener(this));

        setContentView(R.layout.activity_main);

        startService(new Intent(this, NotificationService.class));

        Button exitButton=findViewById(R.id.exitButtonId);
        exitButton.setOnClickListener(new ExitEventListener(this));

        Button editNotificationsButton=findViewById(R.id.settingsId);
        editNotificationsButton.setOnClickListener(new StartActivityOnClickListener(this,EditNotificationsActivity.class));

        Button temperatureViewButton=findViewById(R.id.temperatureButtonId);
        temperatureViewButton.setOnClickListener(new StartActivityOnClickListener(this, TemperatureActivity.class));

        Button humidityViewButton=findViewById(R.id.humidityButtonId);
        humidityViewButton.setOnClickListener(new StartActivityOnClickListener(this, HumidityActivity.class));

        Button lightViewButton=findViewById(R.id.lightButtonId);
        lightViewButton.setOnClickListener(new StartActivityOnClickListener(this, LightActivity.class));

        Button airQualityViewButton=findViewById(R.id.airQualityButtonId);
        airQualityViewButton.setOnClickListener(new StartActivityOnClickListener(this, AirQualityActivity.class));

        Button noiseViewButton=findViewById(R.id.noiseButtonId);
        noiseViewButton.setOnClickListener(new StartActivityOnClickListener(this, NoiseActivity.class));
    }
}
