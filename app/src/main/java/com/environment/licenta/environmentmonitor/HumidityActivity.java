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

public class HumidityActivity extends Activity implements Constants {

    private String highestHumidity;
    private String lowestHumidity;
    private String averageHumidity;

    public DataPoint[] getDatapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[LAST_DATA_WINDOW_SIZE];
        int startIndex=env_data.size()-LAST_DATA_WINDOW_SIZE;
        startIndex = startIndex>0?startIndex:0;
        for(int i =0;i<LAST_DATA_WINDOW_SIZE;i++) {
            Date date = new Date();
            date.setTime(env_data.get(i+startIndex).getTimestamp());
            datapoints[i] = new DataPoint(date, Double.parseDouble(env_data.get(i+startIndex).getHumidity()));
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
        setContentView(R.layout.humidity);
        ArrayList<EnvironmentData> data=ProgramData.getInstance().environmentDataList;
        computeLowHighAvg();

        GraphView graph = findViewById(R.id.humidityGraphId);
        TextView currentHumidity = findViewById(R.id.currentHumidityId);
        TextView lowestHumidity = findViewById(R.id.lowestHumidityId);
        TextView averageHumidity = findViewById(R.id.averageHumidityId);
        TextView highestHumidity = findViewById(R.id.highestHumidityId);
        TextView humidityStatus = findViewById(R.id.humidityStatusId);

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

        series.setColor(Color.BLUE);
        series.setThickness(3);

        graph.addSeries(series);
        graph.addSeries(bestFitSeries);
        graph.getGridLabelRenderer().setLabelFormatter(new HourAsXAxisLabelFormatter());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(datapoints[0].getX());
        graph.getViewport().setMaxX(datapoints[datapoints.length-1].getX()+PREDICTED_POINTS*INTERVAL_MILLISECONDS);
        graph.getGridLabelRenderer().setHumanRounding(false);

        currentHumidity.setText(data.get(data.size()-1).getHumidity()+"%");
        highestHumidity.setText("Highest Humidity Value: "+this.highestHumidity+"%");
        lowestHumidity.setText("Lowest Humidity Value: "+this.lowestHumidity+"%");
        averageHumidity.setText("Average Humidity Value: "+this.averageHumidity+"%");
        humidityStatus.setText("Humidity is " + (bestFitLine.getM()>0?"increasing":"decreasing"));

    }
}
