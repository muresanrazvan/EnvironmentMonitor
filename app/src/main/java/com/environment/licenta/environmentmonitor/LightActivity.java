package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;

import com.environment.licenta.environmentmonitor.model.Constants;
import com.environment.licenta.environmentmonitor.model.ProgramData;
import com.environment.licenta.environmentmonitor.utils.HourAsXAxisLabelFormatter;
import com.environment.licenta.environmentmonitor.utils.LineEquation;
import com.environment.licenta.environmentmonitor.utils.Utilities;
import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Date;

public class LightActivity extends Activity implements Constants {

    private String averageLight;
    private String lowestLight;
    private String highestLight;

    public DataPoint[] getDatapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[LAST_DATA_WINDOW_SIZE];
        int startIndex=env_data.size()-LAST_DATA_WINDOW_SIZE;
        startIndex = startIndex>0?startIndex:0;
        for(int i =0;i<LAST_DATA_WINDOW_SIZE;i++) {
            Date date = new Date();
            date.setTime(env_data.get(i+startIndex).getTimestamp());
            datapoints[i] = new DataPoint(date, Double.parseDouble(env_data.get(i+startIndex).getLight()));
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

    public DataPoint[] getPredictedLineDataPoints(LineEquation le, DataPoint[] dataPoints){
        DataPoint p0=new DataPoint(dataPoints[dataPoints.length-LAST_POINTS_ANALYZED].getX(),le.getY(0));
        DataPoint p1=new DataPoint(dataPoints[dataPoints.length-1].getX()+PREDICTED_POINTS*INTERVAL_MILLISECONDS,le.getY(PREDICTED_POINTS+LAST_POINTS_ANALYZED));
        DataPoint ps[]={p0,p1};
        return ps;
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
        TextView lightStatus = findViewById(R.id.lightStatusId);

        DataPoint datapoints[] = getDatapoints();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(datapoints);

        LineEquation bestFitLine = Utilities.getBestFitLineEquation(datapoints,LAST_POINTS_ANALYZED);
        LineGraphSeries<DataPoint> bestFitSeries = new LineGraphSeries<>(getPredictedLineDataPoints(bestFitLine,datapoints));
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 6}, 0));
        bestFitSeries.setColor(Color.BLUE);
        bestFitSeries.setDrawAsPath(true);
        bestFitSeries.setCustomPaint(paint);

        series.setColor(Color.YELLOW);
        series.setThickness(3);

        graph.addSeries(series);
        graph.addSeries(bestFitSeries);
        graph.getGridLabelRenderer().setLabelFormatter(new HourAsXAxisLabelFormatter());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(datapoints[0].getX());
        graph.getViewport().setMaxX(datapoints[datapoints.length-1].getX()+PREDICTED_POINTS*INTERVAL_MILLISECONDS);
        graph.getGridLabelRenderer().setHumanRounding(false);

        currentLight.setText(data.get(data.size()-1).getLight() + "Lux");
        highestLight.setText("Highest Light Value: " + this.highestLight+"Lux");
        lowestLight.setText("Lowest Light Value: " + this.lowestLight+"Lux");
        averageLight.setText("Average Light Value: " + this.averageLight+"Lux");
        lightStatus.setText("Light intensity is " + (bestFitLine.getM()>0?"increasing":"decreasing"));
    }
}
