package com.example.spotifyadblocker.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.spotifyadblocker.Models.Constant;
import com.example.spotifyadblocker.R;
import com.example.spotifyadblocker.activity.MainActivity;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class NotificationListenerCollectorService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(intent.getAction()!=null&&intent.getAction().equals("stop")){
          getSharedPreferences(Constant.SHARED_PREF,Context.MODE_PRIVATE)
                    .edit().putBoolean(Constant.BLOCKER_SWITCH,false).apply();
            getSharedPreferences(Constant.SHARED_PREF,Context.MODE_PRIVATE)
                    .edit().putString(Constant.BLOCKER_TEXT,"OFF").apply();

            Intent i=new Intent(Constant.BLOCKER_SWITCH);
            i.setAction(Constant.BLOCKER_SWITCH);
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(i);


            ComponentName thisComponent = new ComponentName(this, /*getClass()*/ NotificationListenerService.class);
            PackageManager pm = getPackageManager();
            pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            stopSelf();
        }

        return START_NOT_STICKY;
    }


    private void startForeground() {
        String channelId="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId= createNotificationChannel("my_service", "My Background Service");
        }

        Intent i=new Intent(this, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),0 , i,0 );

        Intent stIntent = new Intent(this,NotificationListenerCollectorService.class);
        stIntent.setAction("stop");

        PendingIntent stopIntent=PendingIntent.getService(this,12,stIntent,0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId );
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(contentIntent)
                .addAction(new NotificationCompat.Action(R.mipmap.ic_launcher,"stop",stopIntent))
                .setContentTitle("Service is running")
                .build();
        startForeground(101, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel( String channelId,  String channelName){
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(R.color.black);
        chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        @SuppressLint("ServiceCast")
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    @Override
    public void onDestroy() {
        ComponentName thisComponent = new ComponentName(this, NotificationListenerService.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        super.onDestroy();

    }
}
