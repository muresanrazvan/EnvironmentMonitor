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

public class HumidityActivity extends Activity {

    private String highestHumidity;
    private String lowestHumidity;
    private String averageHumidity;

    public DataPoint[] getDatapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[env_data.size()];
        int index=0;

        for(EnvironmentData data:env_data) {
            Date date = new Date();
            date.setTime(data.getTimestamp());
            datapoints[index++] = new DataPoint(date, Double.parseDouble(data.getHumidity()));
        }

        return datapoints;
    }

    public void computeLowHighAvg(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;

        double totalHumidity=0;
        double lowestHumidity=Double.MAX_VALUE;
        double highestHumidity=0;

        for(EnvironmentData data:env_data) {
            double light=Double.parseDouble(data.getHumidity());

            totalHumidity+=light;

            if(lowestHumidity>light){
                lowestHumidity=light;
            }

            if(highestHumidity<light){
                highestHumidity=light;
            }
        }
        this.averageHumidity= "" + String.format("%.2f", totalHumidity / env_data.size());
        this.lowestHumidity= "" + lowestHumidity;
        this.highestHumidity= "" + highestHumidity;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.humidity);
        ArrayList<EnvironmentData> data=ProgramData.getInstance().environmentDataList;
        computeLowHighAvg();

        GraphView graph = findViewById(R.id.humidityGraphId);
        TextView currentHumidity = findViewById(R.id.currentHumidityId);
        TextView lowestHumidity = findViewById(R.id.lowestHumidityId);
        TextView averageHumidity = findViewById(R.id.averageHumidityId);
        TextView highestHumidity = findViewById(R.id.highestHumidityId);

        DataPoint datapoints[] = getDatapoints();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(datapoints);

        series.setColor(Color.BLUE);
        series.setThickness(3);

        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new HourAsXAxisLabelFormatter());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(datapoints[0].getX());
        graph.getViewport().setMaxX(datapoints[datapoints.length-1].getX());
        graph.getGridLabelRenderer().setHumanRounding(false);

        currentHumidity.setText(data.get(data.size()-1).getHumidity()+"%");
        highestHumidity.setText("Highest Humidity Value: "+this.highestHumidity+"%");
        lowestHumidity.setText("Lowest Humidity Value: "+this.lowestHumidity+"%");
        averageHumidity.setText("Average Humidity Value: "+this.averageHumidity+"%");

    }
}
