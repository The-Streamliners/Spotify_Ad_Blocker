package com.example.spotifyadblocker.services;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.spotifyadblocker.Models.Constant;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("OverrideAbstract")
public class NotificationListenerService extends android.service.notification.NotificationListenerService {

    SharedPreferences sharedPreferences;
    long firstTime = 0;
    long Time_Interval = 10000;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);


        if (!sbn.getPackageName().equals("com.spotify.music")) {
            return;
        }

        Log.d("Abhi", "onNotificationPosted"+System.currentTimeMillis());
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREF, Context.MODE_PRIVATE);
        boolean isMute = sharedPreferences.getBoolean(Constant.BLOCKER_SWITCH, false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            MediaSessionManager manager = getSystemService(MediaSessionManager.class);
            List<android.media.session.MediaController> list = manager.getActiveSessions(new ComponentName(this, NotificationListenerService.class));

            PlaybackState playbackState = list.get(0).getPlaybackState();

            if (playbackState != null) {
                Intent intent = new Intent(Constant.PLAYING_STATE);
                intent.putExtra(Constant.PLAYING_STATE, playbackState.getState());
                sharedPreferences.edit().putInt(Constant.PLAYING_STATE, playbackState.getState()).apply();
                intent.setAction(Constant.PLAYING_STATE);
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(intent);
                Log.d("Abhi", "" + playbackState.getState());

            }
        }
        if (!isMute) {
            return;
        }
        boolean isAdd = (sbn.getNotification().actions.length == 3);
        String previousSong = sharedPreferences.getString("previousSong", ""),
                newSong;

        newSong = sbn.getNotification().extras.getCharSequence("android.title").toString();


//        Bundle bundle=sbn.getNotification().extras;
//        Set<String> strings=bundle.keySet();
//        for(String str:strings){
//            Log.d("Abhi",str);
//        }
//            Log.d("Abhi", sbn.getNotification().extras.getCharSequence("android.title").toString());
////            Log.d("Abhi",sbn.getNotification().extras.get("android.mediaSession").toString());
//            Log.d("Abhi",""+bundle.keySet());
//           Log.d("Abhi",""+bundle.get("android.mediaSession"));


        if ((!newSong.equals(previousSong)) && (!newSong.equals("Advertisement"))) {
            sharedPreferences.edit()
                    .putString("previousSong", newSong).apply();
            sharedPreferences.edit()
                    .putInt(Constant.SONGS_COUNTER, sharedPreferences.getInt(Constant.SONGS_COUNTER, 0) + 1).apply();

            Intent intent = new Intent(Constant.SONGS_COUNTER);
            intent.setAction(Constant.SONGS_COUNTER);
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(intent);
        }

        if (isAdd) {
            if (firstTime + Time_Interval < System.currentTimeMillis()) {

                Muter.mute(getApplicationContext());
                sharedPreferences.edit().putInt(Constant.ADS_COUNTER, sharedPreferences.getInt(Constant.ADS_COUNTER, 0) + 1).apply();
                Intent intent = new Intent(Constant.ADS_COUNTER);

                intent.setAction(Constant.ADS_COUNTER);
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(intent);
                firstTime = System.currentTimeMillis();
            }
        } else {
            Muter.unMute(getApplicationContext());
        }


    }

}
