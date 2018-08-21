package com.environment.licenta.environmentmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Window;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.splash);

new Handler().postDelayed(new Runnable(){
          @Override
           public void run() {
                                       Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                        SplashActivity.this.startActivity(mainIntent);
                                        SplashActivity.this.finish();
                                    }
       }, 2500);
    }
}
