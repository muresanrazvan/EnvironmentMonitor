package com.environment.licenta.environmentmonitor.model;

import android.content.Context;

import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;

import java.util.ArrayList;

public class ServiceData {
    public ArrayList<EnvironmentData> environmentDataList;
    public double maxTemperature;
    public double minTemperature;

    public double maxHumidity;
    public double minHumidity;

    public double maxNoise;
    public double minNoise;

    public double maxLight;
    public double minLight;

    public double maxCO2;
    public double minCO2;

    public boolean temperatureEnabled;
    public boolean humidityEnabled;
    public boolean lightEnabled;
    public boolean noiseEnabled;
    public boolean eCO2Enabled;

    private static ServiceData instance;

    private ServiceData(){
        environmentDataList=new ArrayList<>();
    }

    public static ServiceData getInstance() {
        if (instance == null) {
            instance = new ServiceData();
        }
        return instance;
    }
}
