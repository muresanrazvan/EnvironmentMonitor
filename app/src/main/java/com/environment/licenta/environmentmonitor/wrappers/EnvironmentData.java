package com.environment.licenta.environmentmonitor.wrappers;

import android.support.annotation.NonNull;

import java.security.spec.EncodedKeySpec;

public class EnvironmentData implements Comparable<EnvironmentData> {
    private String humidity;
    private String light;
    private String temperature;
    private long timestamp;

    public EnvironmentData(){}

    public String getHumidity(){
        return humidity;
    }

    public String getLight(){
        return light;
    }

    public String getTemperature(){
        return temperature;
    }

    public long getTimestamp(){
        return timestamp;
    }

    @Override
    public int compareTo(@NonNull EnvironmentData o) {
        if(timestamp<o.timestamp)
            return -1;
        if(timestamp>o.timestamp)
            return 1;
        return 0;
    }
}
