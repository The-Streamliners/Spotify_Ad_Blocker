package com.example.spotifyadblocker;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

public class Muter {

    public static void mute(Context context){

        AudioManager audioManager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        else
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
    }

    public static void unMute(Context context){
        AudioManager audioManager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        else
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }
}
