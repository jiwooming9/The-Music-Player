package com.nothing.unnamedplayer.Util;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "mediachannel";
    public static final String CHANNEL_NAME = "Nhạc";
    //NOTIFICATION VÀ LOCKSCREEN
    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel1.enableLights(true); //đèn noti

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

    }
}
