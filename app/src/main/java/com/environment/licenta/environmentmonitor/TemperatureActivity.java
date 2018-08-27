package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;

import com.environment.licenta.environmentmonitor.model.ProgramData;
import com.environment.licenta.environmentmonitor.utils.HourAsXAxisLabelFormatter;
import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class TemperatureActivity extends Activity {

    private String highestTemperature;
    private String lowestTemperature;
    private String averageTemperature;

    public DataPoint[] getDatapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[env_data.size()];
        int index=0;

        for(EnvironmentData data:env_data) {
            Date date = new Date();
            date.setTime(data.getTimestamp());
            datapoints[index++] = new DataPoint(date, Double.parseDouble(data.getTemperature()));
        }

        return datapoints;
    }

    public void computeLowHighAvg(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;

        double totalTemperature=0;
        double lowestTemperature=Double.MAX_VALUE;
        double highestTemperature=0;

        for(EnvironmentData data:env_data) {
            double temperature=Double.parseDouble(data.getTemperature());

            totalTemperature+=temperature;

            if(lowestTemperature>temperature){
                lowestTemperature=temperature;
            }

            if(highestTemperature<temperature){
                highestTemperature=temperature;
            }
        }
        this.averageTemperature= "" + String.format("%.2f", totalTemperature / env_data.size());
        this.lowestTemperature= "" + lowestTemperature;
        this.highestTemperature= "" + highestTemperature;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.temperature);
        ArrayList<EnvironmentData> data=ProgramData.getInstance().environmentDataList;
        computeLowHighAvg();

        GraphView graph = findViewById(R.id.graph);
        TextView currentTemp = findViewById(R.id.currentTemperatureId);
        TextView lowestTemperature = findViewById(R.id.lowestTemperatureId);
        TextView averageTemperature = findViewById(R.id.averageTemperatureId);
        TextView highestTemperature = findViewById(R.id.highestTemperatureId);

        DataPoint datapoints[] = getDatapoints();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(datapoints);

        series.setColor(Color.RED);
        series.setThickness(3);

        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new HourAsXAxisLabelFormatter());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(datapoints[0].getX());
        graph.getViewport().setMaxX(datapoints[datapoints.length-1].getX());
        graph.getGridLabelRenderer().setHumanRounding(false);

        currentTemp.setText(data.get(data.size()-1).getTemperature()+"\u00b0C");
        highestTemperature.setText("Highest Temperature Value: "+this.highestTemperature+"\u00b0C");
        lowestTemperature.setText("Lowest Temperature Value: "+this.lowestTemperature+"\u00b0C");
        averageTemperature.setText("Average Temperature Value: "+this.averageTemperature+"\u00b0C");
    }
}
