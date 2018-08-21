package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;

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

public class LightActivity extends Activity {

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

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.light);
        GraphView graph = (GraphView) findViewById(R.id.lightGraphId);
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
    }
}