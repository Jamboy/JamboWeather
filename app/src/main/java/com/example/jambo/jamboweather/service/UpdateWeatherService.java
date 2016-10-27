package com.example.jambo.jamboweather.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Jambo on 2016/10/27.
 */

public class UpdateWeatherService extends Service{
    private static final String BROADCASTACTION = "android.jambo.jamboweather.broadcast";
    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }


    @Override public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override public void run() {
                Log.d("auto","startService");
                Intent intent1 = new Intent();
                intent1.setAction(BROADCASTACTION);
                sendBroadcast(intent1);
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}
