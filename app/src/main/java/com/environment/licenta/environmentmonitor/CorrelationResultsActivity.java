package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.os.Bundle;

import com.environment.licenta.environmentmonitor.model.ProgramData;
import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class CorrelationResultsActivity extends Activity {

    private void sortDatapointsAscendingByX(DataPoint datapoints[]){
        Arrays.sort(datapoints, new Comparator<DataPoint>(){
            @Override
            public int compare(DataPoint o1, DataPoint o2) {
                return o1.getX()<o2.getX()?-1:(o1.getX()>o2.getX()?1:0);
            }
        });
    }

    private double getMaxY(DataPoint[] datapoints){
        double largest=-999999;
        for(DataPoint datapoint:datapoints) {
            if (largest<datapoint.getY()){
                largest=datapoint.getY();
            }
        }
        return largest;
    }

    private Double getDataByLabel(EnvironmentData envData,String label){
        switch(label){
            case "Temperature":
                return Double.parseDouble(envData.getTemperature());
            case "Humidity":
                return Double.parseDouble(envData.getHumidity());
            case "eCO2":
                return Double.parseDouble(envData.getECO2());
            case "TVOC":
                return Double.parseDouble(envData.getTVOC());
            case "Light":
                return Double.parseDouble(envData.getLight());
                default:
                    return Double.NaN;
        }
    }

    public DataPoint[] getDatapoints(String xAxis,String yAxis){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[env_data.size()];
        int index=0;

        for(EnvironmentData data:env_data) {
            datapoints[index++] = new DataPoint(getDataByLabel(data,xAxis), getDataByLabel(data,yAxis));
        }
        sortDatapointsAscendingByX(datapoints);
        return datapoints;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.correlation_results);
        String xAxis=getIntent().getStringExtra("X_AXIS");
        String yAxis=getIntent().getStringExtra("Y_AXIS");

        GraphView correlationGraph = findViewById(R.id.graph_correlation);

        DataPoint[] datapoints=getDatapoints(xAxis,yAxis);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(datapoints);
        series.setSize(2);
        correlationGraph.addSeries(series);
        series.setShape(PointsGraphSeries.Shape.POINT);
        correlationGraph.getViewport().setYAxisBoundsManual(true);
        correlationGraph.getViewport().setMaxY(getMaxY(datapoints));
        correlationGraph.getGridLabelRenderer().setHumanRounding(false);
    }
}
