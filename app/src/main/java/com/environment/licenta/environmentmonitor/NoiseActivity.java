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

public class NoiseActivity extends Activity {

    public DataPoint[] getDatapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[env_data.size()];
        int index=0;

        for(EnvironmentData data:env_data) {
            Date date = new Date();
            date.setTime(data.getTimestamp());
            datapoints[index++] = new DataPoint(date, Double.parseDouble(data.getNoise()));
        }

        return datapoints;
    }

    public String lowestNoiseValue() {
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        double lowest=Double.MAX_VALUE;
        for(EnvironmentData data:env_data) {
            double noise=Double.parseDouble(data.getNoise());
            if(lowest>noise){
                lowest=noise;
            }
        }
        return ""+lowest;
    }

    public String highestNoiseValue() {
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        double highest=0;
        for(EnvironmentData data:env_data) {
            double noise=Double.parseDouble(data.getNoise());
            if(highest<noise){
                highest=noise;
            }
        }
        return ""+highest;
    }

    public String averageNoiseValue() {
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        double total=0;
        for(EnvironmentData data:env_data) {
            double noise=Double.parseDouble(data.getNoise());
                total+=noise;
        }
        return "" + String.format("%.2f", total / env_data.size());
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.noise);
        ArrayList<EnvironmentData> data=ProgramData.getInstance().environmentDataList;
        GraphView graph = findViewById(R.id.noiseGraphId);
        TextView currentNoise = findViewById(R.id.currentNoiseId);
        TextView highestNoise = findViewById(R.id.highestNoiseId);
        TextView lowestNoise = findViewById(R.id.lowestNoiseId);
        TextView averageNoise = findViewById(R.id.averageNoiseId);

        DataPoint datapoints[] = getDatapoints();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(datapoints);

        series.setColor(Color.BLACK);
        series.setThickness(3);

        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new HourAsXAxisLabelFormatter());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(datapoints[0].getX());
        graph.getViewport().setMaxX(datapoints[datapoints.length-1].getX());
        graph.getGridLabelRenderer().setHumanRounding(false);

        currentNoise.setText(data.get(data.size()-1).getNoise()+"dB");
        highestNoise.setText("Highest Value Recorded: "+highestNoiseValue()+"dB");
        lowestNoise.setText("Lowest Value Recorded: "+lowestNoiseValue()+"dB");
        averageNoise.setText("Total Average Value: "+averageNoiseValue()+"dB");
    }
}
