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

public class AirQualityActivity extends Activity implements Constants {

    private String lowestTVOC;
    private String highestTVOC;
    private String averageTVOC;

    private String lowestECO2;
    private String highestECO2;
    private String averageECO2;

    public void computeLowHighAvg(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;

        double totalECO2=0;
        double totalTVOC=0;
        double lowestECO2=Double.MAX_VALUE;
        double lowestTVOC=Double.MAX_VALUE;
        double highestECO2=0;
        double highestTVOC=0;

        for(EnvironmentData data:env_data) {
            double ECO2=Double.parseDouble(data.getECO2());
            double TVOC=Double.parseDouble(data.getTVOC());

            totalECO2+=ECO2;
            totalTVOC+=TVOC;

            if(lowestECO2>ECO2){
                lowestECO2=ECO2;
            }
            if(lowestTVOC>TVOC){
                lowestTVOC=TVOC;
            }

            if(highestECO2<ECO2){
                highestECO2=ECO2;
            }
            if(highestTVOC<TVOC){
                highestTVOC=TVOC;
            }
        }
        this.averageECO2= "" + String.format("%.2f", totalECO2 / env_data.size());
        this.averageTVOC= "" + String.format("%.2f", totalTVOC / env_data.size());
        this.lowestECO2= "" + lowestECO2;
        this.lowestTVOC= "" + lowestTVOC;
        this.highestECO2= "" + highestECO2;
        this.highestTVOC= "" + highestTVOC;
    }

    public DataPoint[] getECO2Datapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[LAST_DATA_WINDOW_SIZE];
        int startIndex=env_data.size()-LAST_DATA_WINDOW_SIZE;
        startIndex = startIndex>0?startIndex:0;
        for(int i =0;i<LAST_DATA_WINDOW_SIZE;i++) {
            Date date = new Date();
            date.setTime(env_data.get(i+startIndex).getTimestamp());
            datapoints[i] = new DataPoint(date, Double.parseDouble(env_data.get(i+startIndex).getECO2()));
        }

        return datapoints;
    }

//    public DataPoint[] getTVOCDatapoints(){
//        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
//        DataPoint datapoints[]=new DataPoint[LAST_DATA_WINDOW_SIZE];
//        int startIndex=env_data.size()-LAST_DATA_WINDOW_SIZE;
//        startIndex = startIndex>0?startIndex:0;
//        for(int i =0;i<LAST_DATA_WINDOW_SIZE;i++) {
//            Date date = new Date();
//            date.setTime(env_data.get(i+startIndex).getTimestamp());
//            datapoints[i] = new DataPoint(date, Double.parseDouble(env_data.get(i+startIndex).getTVOC()));
//        }
//
//        return datapoints;
//    }


    public DataPoint[] getTVOCDatapoints(){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[19];
        int startIndex=120;
        startIndex = startIndex>0?startIndex:0;
        for(int i =0;i<19;i++) {
            Date date = new Date();
            date.setTime(env_data.get(i+startIndex).getTimestamp());
            datapoints[i] = new DataPoint(date, Double.parseDouble(env_data.get(i+startIndex).getTVOC()));
        }

        return datapoints;
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
        setContentView(R.layout.air_quality);
        ArrayList<EnvironmentData> data=ProgramData.getInstance().environmentDataList;
        computeLowHighAvg();

        // eco2 info
        GraphView eco2Graph = findViewById(R.id.graph_eco2);
        TextView currentECO2 = findViewById(R.id.currentCO2Id);
        TextView highestECO2 = findViewById(R.id.highestECO2id);
        TextView lowestECO2 = findViewById(R.id.lowestECO2id);
        TextView averageECO2 = findViewById(R.id.averageECO2id);
        TextView eCO2Status = findViewById(R.id.eCO2StatusId);

        DataPoint eCO2Datapoints[] = getECO2Datapoints();
        LineGraphSeries<DataPoint> eco2Series = new LineGraphSeries<>(eCO2Datapoints);

        LineEquation eCO2BestFitLine = Utilities.getBestFitLineEquation(eCO2Datapoints,LAST_POINTS_ANALYZED);
        LineGraphSeries<DataPoint> eCO2BestFitSeries = new LineGraphSeries<>(getPredictedLineDataPoints(eCO2BestFitLine,eCO2Datapoints));
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 6}, 0));
        eCO2BestFitSeries.setColor(Color.BLUE);
        eCO2BestFitSeries.setDrawAsPath(true);
        eCO2BestFitSeries.setCustomPaint(paint);

        eco2Series.setColor(Color.GREEN);
        eco2Series.setThickness(3);

        eco2Graph.addSeries(eco2Series);
        eco2Graph.addSeries(eCO2BestFitSeries);
        eco2Graph.getGridLabelRenderer().setLabelFormatter(new HourAsXAxisLabelFormatter());
        eco2Graph.getViewport().setXAxisBoundsManual(true);
        eco2Graph.getViewport().setMinX(eCO2Datapoints[0].getX());
        eco2Graph.getViewport().setMaxX(eCO2Datapoints[eCO2Datapoints.length-1].getX()+PREDICTED_POINTS*INTERVAL_MILLISECONDS);
        eco2Graph.getGridLabelRenderer().setHumanRounding(false);

        currentECO2.setText(data.get(data.size()-1).getECO2() + "ppm");
        highestECO2.setText("Highest ECO2 Value: "+this.highestECO2+"ppm");
        lowestECO2.setText("Lowest ECO2 Value: "+this.lowestECO2+"ppm");
        averageECO2.setText("Average ECO2 Value: "+this.averageECO2+"ppm");
        eCO2Status.setText("eCO2 is " + (eCO2BestFitLine.getM()>0?"increasing":"decreasing"));

        // tvoc info
        GraphView tvocGraph = findViewById(R.id.graph_tvoc);
        TextView currentTVOC = findViewById(R.id.currentTVOCid);
        TextView highestTVOC = findViewById(R.id.highestTVOCid);
        TextView lowestTVOC = findViewById(R.id.lowestTVOCid);
        TextView averageTVOC = findViewById(R.id.averageTVOCid);
        TextView tvocStatus = findViewById(R.id.tvocStatusId);

        DataPoint tvocDatapoints[] = getTVOCDatapoints();
        LineGraphSeries<DataPoint> tvocSeries = new LineGraphSeries<>(tvocDatapoints);

        LineEquation tvocBestFitLine = Utilities.getBestFitLineEquation(tvocDatapoints,LAST_POINTS_ANALYZED);
        LineGraphSeries<DataPoint> tvocBestFitSeries = new LineGraphSeries<>(getPredictedLineDataPoints(tvocBestFitLine,tvocDatapoints));
        tvocBestFitSeries.setColor(Color.BLUE);
        tvocBestFitSeries.setDrawAsPath(true);
        tvocBestFitSeries.setCustomPaint(paint);

        tvocSeries.setColor(Color.MAGENTA);
        tvocSeries.setThickness(3);

        tvocGraph.addSeries(tvocSeries);
        tvocGraph.addSeries(tvocBestFitSeries);
        tvocGraph.getGridLabelRenderer().setLabelFormatter(new HourAsXAxisLabelFormatter());
        tvocGraph.getViewport().setXAxisBoundsManual(true);
        tvocGraph.getViewport().setMinX(tvocDatapoints[0].getX());
        tvocGraph.getViewport().setMaxX(tvocDatapoints[tvocDatapoints.length-1].getX()+PREDICTED_POINTS*INTERVAL_MILLISECONDS);
        tvocGraph.getGridLabelRenderer().setHumanRounding(false);

        currentTVOC.setText(data.get(data.size()-1).getTVOC() + "ppb");
        highestTVOC.setText("Highest TVOC Value: "+this.highestTVOC+"ppb");
        lowestTVOC.setText("Lowest TVOC Value: "+this.lowestTVOC+"ppb");
        averageTVOC.setText("Average TVOC Value: "+this.averageTVOC+"ppb");
        tvocStatus.setText("TVOC is " + (tvocBestFitLine.getM()>0?"increasing":"decreasing"));
    }
}
