package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.environment.licenta.environmentmonitor.model.ProgramData;
import com.environment.licenta.environmentmonitor.utils.Correlation;
import com.environment.licenta.environmentmonitor.utils.LineEquation;
import com.environment.licenta.environmentmonitor.utils.Utilities;
import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class CorrelationResultsActivity extends Activity {

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

//    public DataPoint[] getDatapoints(String xAxis,String yAxis){
//        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
//        DataPoint datapoints[]=new DataPoint[env_data.size()];
//        int index=0;
//
//        for(EnvironmentData data:env_data) {
//            datapoints[index++] = new DataPoint(getDataByLabel(data,xAxis), getDataByLabel(data,yAxis));
//        }
//        Utilities.sortDatapointsAscendingByX(datapoints);
//        return datapoints;
//    }

    public DataPoint[] getDatapoints(String xAxis,String yAxis){
        ArrayList<EnvironmentData> env_data=ProgramData.getInstance().environmentDataList;
        DataPoint datapoints[]=new DataPoint[10];
        int index=0;

        for (int i = 126; i<136;i++) {
            datapoints[index++] = new DataPoint(getDataByLabel(env_data.get(i), xAxis), getDataByLabel(env_data.get(i), yAxis));
        }
        for (int i = 0; i<10;i++){
            System.out.println("x:"+datapoints[i].getX()+"; y:"+datapoints[i].getY());
        }
        Utilities.sortDatapointsAscendingByX(datapoints);
        return datapoints;
    }

    public LineEquation getBestFitLineEquation(DataPoint[] datapoints){
        double ys[]=new double[datapoints.length];
        double xs[]=new double[datapoints.length];
        for (int i = 0; i<ys.length; i++){
            ys[i]=datapoints[i].getY();
            xs[i]=datapoints[i].getX();
        }
        return new LineEquation(xs,ys);
    }

    public DataPoint[] getBestFitLineDataPoints(LineEquation le, DataPoint[] dataPoints){
        DataPoint p0=new DataPoint(dataPoints[0].getX(),le.getY(dataPoints[0].getX()));
        DataPoint p1=new DataPoint(dataPoints[dataPoints.length-1].getX(),le.getY(dataPoints[dataPoints.length-1].getX()));

        DataPoint ps[]={p0,p1};
        return ps;
    }

    private String interpretCorrelationCoefficient(double r){
        String result = r<0?"Negative":"Positive";
        r = Math.abs(r);

        if (r>=0.9)
            return "Very Strong " + result + " Correlation";
        if (r>=0.7)
            return "Strong " + result + " Correlation";
        if (r>=0.5)
            return "Moderate " + result + " Correlation";
        if (r>=0.3)
            return "Weak " + result + " Correlation";

        return "Not Correlated";
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.correlation_results);
        String xAxis=getIntent().getStringExtra("X_AXIS");
        String yAxis=getIntent().getStringExtra("Y_AXIS");

        DecimalFormat df = new DecimalFormat("#.###");

        GraphView correlationGraph = findViewById(R.id.graph_correlation);
        TextView pearsonCorrelationTextView = findViewById(R.id.pearsonCoefficientId);
        TextView pearsonInterpretationTextView = findViewById(R.id.pearsonInterpretationId);
        TextView bestFitLineEquationTextView = findViewById(R.id.bestFitLineEquationId);

        DataPoint[] datapoints=getDatapoints(xAxis,yAxis);

        LineEquation bestFitLine = getBestFitLineEquation(datapoints);
        LineGraphSeries<DataPoint> bestFitSeries = new LineGraphSeries<>(getBestFitLineDataPoints(bestFitLine,datapoints));
        bestFitSeries.setColor(Color.RED);

        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(datapoints);
        series.setSize(2);
        correlationGraph.addSeries(series);
        correlationGraph.addSeries(bestFitSeries);
        series.setShape(PointsGraphSeries.Shape.POINT);
        correlationGraph.getViewport().setYAxisBoundsManual(true);
        correlationGraph.getViewport().setMaxY(getMaxY(datapoints));
        correlationGraph.getGridLabelRenderer().setHumanRounding(false);

        // Pearson correlation coefficient R
        Correlation corr = new Correlation(datapoints);
        double correlation_coefficient = corr.getCorrelationCoefficient();
        pearsonCorrelationTextView.setText("Pearson Coefficient: " + df.format(correlation_coefficient));
        pearsonInterpretationTextView.setText(interpretCorrelationCoefficient(correlation_coefficient));
        String m=df.format(bestFitLine.getM());
        String b=df.format(bestFitLine.getB());
        bestFitLineEquationTextView.setText("Line Equation: y = " + m + "x " + (bestFitLine.getB()>0 ? "+" : "") + b);

    }
}
