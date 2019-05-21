package com.environment.licenta.environmentmonitor.utils;

// b = y - mx
public class LineEquation {
    private double m;
    private double b;

    private double mean(double arr[]){
        double sum=0;
        for (double element: arr) {
            sum+=element;
        }
        return sum/arr.length;
    }

    private double[] multiplyArray(double a[], double b[]){
        double result[]=new double[a.length];
        for(int i = 0; i<result.length;i++){
            result[i]=a[i]*b[i];
        }
        return result;
    }

    public LineEquation(double m, double b){
        this.m = m;
        this.b = b;
    }

    // best fit line
    public LineEquation(double x[], double y[]){
        double mean_x=mean(x);
        double mean_y=mean(y);

        m = (mean_x*mean_y-mean(multiplyArray(x,y)))/(mean_x*mean_x-mean(multiplyArray(x,x)));
        b = mean(y) - m*mean(x);
    }

    public double getY(double x){
        return m*x+b;
    }

    public double getM(){
        return m;
    }

    public double getB(){
        return b;
    }
}
