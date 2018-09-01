package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.environment.licenta.environmentmonitor.model.ProgramData;
import com.environment.licenta.environmentmonitor.utils.HourAsXAxisLabelFormatter;
import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Date;

public class LightActivity extends Activity {

    private String averageLight;
    private String lowestLight;
    private String highestLight;

    public DataPoint[] getDatapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[env_data.size()];
        int index=0;

        for(EnvironmentData data:env_data) {
            Date date = new Date();
            date.setTime(data.getTimestamp());
            datapoints[index++] = new DataPoint(date, Double.parseDouble(data.getLight()));
        }

        return datapoints;
    }

    public void computeLowHighAvg(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;

        double totalLight=0;
        double lowestLight=Double.MAX_VALUE;
        double highestLight=0;

        for(EnvironmentData data:env_data) {
            double light=Double.parseDouble(data.getLight());

            totalLight+=light;

            if(lowestLight>light){
                lowestLight=light;
            }

            if(highestLight<light){
                highestLight=light;
            }
        }
        this.averageLight= "" + String.format("%.2f", totalLight / env_data.size());
        this.lowestLight= "" + lowestLight;
        this.highestLight= "" + highestLight;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.light);
        ArrayList<EnvironmentData> data=ProgramData.getInstance().environmentDataList;
        computeLowHighAvg();

        GraphView graph = findViewById(R.id.lightGraphId);
        TextView currentLight = findViewById(R.id.currentLightId);
        TextView lowestLight = findViewById(R.id.lowestLightId);
        TextView averageLight = findViewById(R.id.averageLightId);
        TextView highestLight = findViewById(R.id.highestLightId);

        DataPoint datapoints[] = getDatapoints();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(datapoints);

        series.setColor(Color.YELLOW);
        series.setThickness(3);

        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new HourAsXAxisLabelFormatter());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(datapoints[0].getX());
        graph.getViewport().setMaxX(datapoints[datapoints.length-1].getX());
        graph.getGridLabelRenderer().setHumanRounding(false);

        currentLight.setText(data.get(data.size()-1).getLight()+"Lux");
        highestLight.setText("Highest Light Value: "+this.highestLight+"Lux");
        lowestLight.setText("Lowest Light Value: "+this.lowestLight+"Lux");
        averageLight.setText("Average Light Value: "+this.averageLight+"Lux");
    }
}
