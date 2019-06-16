package com.environment.licenta.environmentmonitor.utils;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Arrays;
import java.util.Comparator;

public class Utilities {
    public static void sortDatapointsAscendingByX(DataPoint datapoints[]){
        Arrays.sort(datapoints, new Comparator<DataPoint>(){
            @Override
            public int compare(DataPoint o1, DataPoint o2) {
                return o1.getX()<o2.getX()?-1:(o1.getX()>o2.getX()?1:0);
            }
        });
    }

    public static LineEquation getBestFitLineEquation(DataPoint[] datapoints, int lastPointsAnalyzed){
        double ys[]=new double[lastPointsAnalyzed];
        double xs[]=new double[lastPointsAnalyzed];
        for (int i = 0; i<lastPointsAnalyzed; i++){
            ys[i]=datapoints[i+datapoints.length-lastPointsAnalyzed].getY();
            xs[i]=i;
        }
        return new LineEquation(xs,ys);
    }
}
