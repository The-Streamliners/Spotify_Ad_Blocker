package com.example.spotifyadblocker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.example.spotifyadblocker.Models.Constant;
import com.example.spotifyadblocker.databinding.ActivityMainBinding;
import com.example.spotifyadblocker.databinding.NotificationAccessDialogBinding;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding b;
    SharedPreferences sharedPreferences;

    BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case Constant.SONGS_COUNTER:
                      b.countSongs.setText(""+sharedPreferences.getInt(Constant.SONGS_COUNTER,0));
                      break;
                case Constant.ADS_COUNTER:
                    b.countAd.setText(""+sharedPreferences.getInt(Constant.ADS_COUNTER,0));
                    break;
                case Constant.BLOCKER_SWITCH:
                    b.switch1.setChecked(false);
                    b.switch1.setText("OFF");
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
//        LoginPageActivity.activity.finish();
        sharedPreferences=getSharedPreferences(Constant.SHARED_PREF,Context.MODE_PRIVATE);

        if(sharedPreferences!=null){
            b.switch1.setChecked(sharedPreferences.getBoolean(Constant.BLOCKER_SWITCH,false));

            b.switch1.setText(sharedPreferences.getString(Constant.BLOCKER_TEXT,"OFF"));
            b.countAd.setText(""+sharedPreferences.getInt(Constant.ADS_COUNTER,0));
            b.countSongs.setText(""+sharedPreferences.getInt(Constant.SONGS_COUNTER,0));
        }


       b.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @RequiresApi(api = Build.VERSION_CODES.M)
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   if(!isNotificationServiceEnabled()){
                       b.switch1.setChecked(false);
                       showNotificationAccessDialog();

                       return;
                   }
                   b.switch1.setText("ON");
                   sharedPreferences.edit()
                           .putBoolean(Constant.BLOCKER_SWITCH,true).apply();
                   sharedPreferences.edit()
                           .putString(Constant.BLOCKER_TEXT,"ON").apply();
                   startService(new Intent(MainActivity.this,NotificationListenerCollectorService.class));
               }
               else{
                   b.switch1.setText("OFF");
                   sharedPreferences.edit()
                           .putBoolean(Constant.BLOCKER_SWITCH,false)
                           .putString(Constant.BLOCKER_TEXT,"OFF").apply();
                   stopService(new Intent(MainActivity.this,NotificationListenerCollectorService.class));
               }
           }
       });

        registerLocalBroadcast();


        }

    private void showNotificationAccessDialog() {
        NotificationAccessDialogBinding notificationBinding=NotificationAccessDialogBinding.inflate(getLayoutInflater());
        AlertDialog alertDialog= new AlertDialog.Builder(MainActivity.this)
                .setView(notificationBinding.getRoot())
                .setCancelable(false)
                .show();


        notificationBinding.btnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });

        notificationBinding.btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    private void registerLocalBroadcast() {
        IntentFilter filter =new IntentFilter();
        filter.addAction(Constant.SONGS_COUNTER);
        filter.addAction(Constant.ADS_COUNTER);
        filter.addAction(Constant.BLOCKER_SWITCH);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mReceiver,filter);
    }

    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mReceiver);
    }
}