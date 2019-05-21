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

public class TemperatureActivity extends Activity implements Constants {

    private String highestTemperature;
    private String lowestTemperature;
    private String averageTemperature;

    public DataPoint[] getDatapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[LAST_DATA_WINDOW_SIZE];
        int startIndex=env_data.size()-LAST_DATA_WINDOW_SIZE;
        startIndex = startIndex>0?startIndex:0;
        for(int i =0;i<LAST_DATA_WINDOW_SIZE;i++) {
            Date date = new Date();
            date.setTime(env_data.get(i+startIndex).getTimestamp());
            datapoints[i] = new DataPoint(date, Double.parseDouble(env_data.get(i+startIndex).getTemperature()));
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

    public LineEquation getBestFitLineEquation(DataPoint[] datapoints){
        double ys[]=new double[LAST_POINTS_ANALYZED];
        double xs[]=new double[LAST_POINTS_ANALYZED];
        for (int i = 0; i<LAST_POINTS_ANALYZED; i++){
            ys[i]=datapoints[i+datapoints.length-LAST_POINTS_ANALYZED].getY();
            xs[i]=i;
        }
        return new LineEquation(xs,ys);
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
        setContentView(R.layout.temperature);
        ArrayList<EnvironmentData> data=ProgramData.getInstance().environmentDataList;
        computeLowHighAvg();

        GraphView graph = findViewById(R.id.graph);
        TextView currentTemp = findViewById(R.id.currentTemperatureId);
        TextView lowestTemperature = findViewById(R.id.lowestTemperatureId);
        TextView averageTemperature = findViewById(R.id.averageTemperatureId);
        TextView highestTemperature = findViewById(R.id.highestTemperatureId);
        TextView temperatureStatus = findViewById(R.id.temperatureStatusId);

        DataPoint datapoints[] = getDatapoints();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(datapoints);

        LineEquation bestFitLine = getBestFitLineEquation(datapoints);
        LineGraphSeries<DataPoint> bestFitSeries = new LineGraphSeries<>(getPredictedLineDataPoints(bestFitLine,datapoints));
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 6}, 0));
        bestFitSeries.setColor(Color.BLUE);
        bestFitSeries.setDrawAsPath(true);
        bestFitSeries.setCustomPaint(paint);

        series.setColor(Color.RED);
        series.setThickness(3);

        graph.addSeries(series);
        graph.addSeries(bestFitSeries);
        graph.getGridLabelRenderer().setLabelFormatter(new HourAsXAxisLabelFormatter());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(datapoints[0].getX());
        graph.getViewport().setMaxX(datapoints[datapoints.length-1].getX()+PREDICTED_POINTS*INTERVAL_MILLISECONDS);
        graph.getGridLabelRenderer().setHumanRounding(false);

        currentTemp.setText(data.get(data.size()-1).getTemperature()+"\u00b0C");
        highestTemperature.setText("Highest Temperature Value: "+this.highestTemperature+"\u00b0C");
        lowestTemperature.setText("Lowest Temperature Value: "+this.lowestTemperature+"\u00b0C");
        averageTemperature.setText("Average Temperature Value: "+this.averageTemperature+"\u00b0C");
        temperatureStatus.setText("Temperature is " + (bestFitLine.getM()>0?"rising":"lowering"));
    }
}
