package com.environment.licenta.environmentmonitor.utils;

import com.environment.licenta.environmentmonitor.model.Constants;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Arrays;

public class Correlation implements Constants {
    private double xs[];
    private double ys[];

    public Correlation(DataPoint datapoints[]){
        ys=new double[datapoints.length];
        xs=new double[datapoints.length];
        for (int i = 0; i<datapoints.length; i++){
            ys[i]=datapoints[i].getY();
            xs[i]=datapoints[i].getX();
        }
    }

    public Correlation(double xs[], double ys[]){
        this.xs= Arrays.copyOf(xs,xs.length);
        this.ys=Arrays.copyOf(ys,ys.length);
    }

    public double getCorrelationCoefficient(){
        return getCorrelationCoefficient(xs.length);
    }

    // method that returns correlation coefficient.
    public double getCorrelationCoefficient(int last_n_points)
    {
        int start_index = xs.length - last_n_points;
        double sum_X = 0;
        double sum_Y = 0;
        double sum_XY = 0;
        double squareSum_X = 0;
        double squareSum_Y = 0;

        for (int i = start_index; i < xs.length; i++)
        {
            sum_X = sum_X + xs[i];
            sum_Y = sum_Y + ys[i];
            sum_XY = sum_XY + xs[i] * ys[i];

            // sum of squares
            squareSum_X = squareSum_X + xs[i] * xs[i];
            squareSum_Y = squareSum_Y + ys[i] * ys[i];
        }

        // computing of correlation coefficient
        double corr = (xs.length * sum_XY - sum_X * sum_Y)/
                (float)(Math.sqrt((xs.length * squareSum_X -
                        sum_X * sum_X) * (xs.length * squareSum_Y -
                        sum_Y * sum_Y)));

        return corr;
    }
}