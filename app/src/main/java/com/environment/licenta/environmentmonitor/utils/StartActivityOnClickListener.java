package com.environment.licenta.environmentmonitor.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class StartActivityOnClickListener implements View.OnClickListener {
    private Activity parentActivity;
    private Class<?> activityClass;
    public StartActivityOnClickListener(Activity parentActivity,Class<?> activityClass)
    {
        this.parentActivity=parentActivity;
        this.activityClass=activityClass;
    }

    @Override
    public void onClick(View v) {
        Intent myIntent = new Intent(parentActivity, activityClass);
        parentActivity.startActivity(myIntent);
    }
}
