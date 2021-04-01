package com.example.spotifyadblocker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.spotifyadblocker.Models.Constant;

import java.util.Set;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("OverrideAbstract")
public class NotificationListenerService extends android.service.notification.NotificationListenerService {

    SharedPreferences  sharedPreferences;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);


        if(!sbn.getPackageName().equals("com.spotify.music")){
            return;
        }
        Log.d("Abhi","onNotificationPosted");
        sharedPreferences=getSharedPreferences(Constant.SHARED_PREF, Context.MODE_PRIVATE);
        boolean isMute=sharedPreferences.getBoolean(Constant.BLOCKER_SWITCH,false);
        if(!isMute){
            return;
        }
        boolean isAdd=(sbn.getNotification().actions.length==3);
        String previousSong=sharedPreferences.getString("previousSong",""),
                newSong;

        newSong = sbn.getNotification().extras.getCharSequence("android.title").toString();


        Bundle bundle=sbn.getNotification().extras;
        Set<String> strings=bundle.keySet();
        for(String str:strings){
            Log.d("Abhi",str);
        }
            Log.d("Abhi",sbn.getNotification().extras.getCharSequence("android.title").toString());
            Log.d("Abhi",sbn.getNotification().extras.get("android.mediaSession").toString());
            Log.d("Abhi",""+bundle.keySet());

            if((!newSong.equals(previousSong))&&(!newSong.equals("Advertisement"))){
              sharedPreferences.edit()
                      .putString("previousSong",newSong).apply();
              sharedPreferences.edit()
                       .putInt(Constant.SONGS_COUNTER,sharedPreferences.getInt(Constant.SONGS_COUNTER,0)+1).apply();

                Intent intent = new Intent(Constant.SONGS_COUNTER);
                intent.setAction(Constant.SONGS_COUNTER);
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(intent);
            }

            if(isAdd){
                sharedPreferences.edit().putInt(Constant.ADS_COUNTER,sharedPreferences.getInt(Constant.ADS_COUNTER,0)+1).apply();
                Muter.mute(getApplicationContext());
                Intent intent = new Intent(Constant.ADS_COUNTER);

                intent.setAction(Constant.ADS_COUNTER);
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(intent);

            }
            else{
                Muter.unMute(getApplicationContext());
            }

    }



}
