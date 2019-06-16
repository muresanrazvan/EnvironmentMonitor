package com.environment.licenta.environmentmonitor.model;

import com.environment.licenta.environmentmonitor.wrappers.EnvironmentData;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ProgramData {
    public ArrayList<EnvironmentData> environmentDataList;
    private static ProgramData instance;
    private ProgramData(){
        environmentDataList=new ArrayList<>();
    }
    public static ProgramData getInstance() {
        if (instance == null) {
            instance = new ProgramData();
        }
        return instance;
    }
}
