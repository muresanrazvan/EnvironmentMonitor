package com.environment.licenta.environmentmonitor.utils;

import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;

import java.text.DecimalFormat;
import java.util.Date;

public class HourAsXAxisLabelFormatter implements LabelFormatter {

    @Override
    public String formatLabel(double value, boolean isValueX) {
        if(isValueX){
            Date d = new Date();
            d.setTime((long)value);
            return (d.getHours()>=10?d.getHours():"0"+d.getHours())+":"+(d.getMinutes()>=10?d.getMinutes():"0"+d.getMinutes());
        }
        return ""+new DecimalFormat("#.00").format(value);
    }

    @Override
    public void setViewport(Viewport viewport) {

    }
}