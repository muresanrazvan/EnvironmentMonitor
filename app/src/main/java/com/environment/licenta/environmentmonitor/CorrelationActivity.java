package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class CorrelationActivity extends Activity {
    private Spinner x;
    private Spinner y;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.correlation);

        String[] arraySpinner = new String[] {
                "Temperature", "Humidity", "eCO2", "TVOC", "Light"
        };

        x = (Spinner) findViewById(R.id.xAxisSpinnerId);
        ArrayAdapter<String> adapterX = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapterX.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        x.setAdapter(adapterX);

        y = (Spinner) findViewById(R.id.yAxisSpinnerId);
        ArrayAdapter<String> adapterY = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapterY.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        y.setAdapter(adapterY);


        Button correlationViewButton=findViewById(R.id.correlationResultsButtonId);
        correlationViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent correlationResultsIntent = new Intent(CorrelationActivity.this, CorrelationResultsActivity.class);
                String xAxisValue=x.getSelectedItem().toString();
                String yAxisValue=y.getSelectedItem().toString();
                correlationResultsIntent.putExtra("X_AXIS",xAxisValue);
                correlationResultsIntent.putExtra("Y_AXIS",yAxisValue);
                CorrelationActivity.this.startActivity(correlationResultsIntent);
            }
        });
    }
}
