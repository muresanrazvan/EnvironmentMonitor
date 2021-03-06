package com.environment.licenta.environmentmonitor.model;

public interface Constants {
    int PREDICTED_POINTS=20;
    int LAST_POINTS_ANALYZED=7;
    int INTERVAL_MILLISECONDS=30000;
    int LAST_DATA_WINDOW_SIZE=2880; //INTERVAL_MILLISECONDS units
    double MAX_SAFE_TEMPERATURE=35;
    double MAX_SAFE_TVOC=1000;
}
