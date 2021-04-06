package com.example.spotifyadblocker.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.example.spotifyadblocker.Models.Constant;
import com.example.spotifyadblocker.services.NotificationListenerCollectorService;
import com.example.spotifyadblocker.services.NotificationListenerService;
import com.example.spotifyadblocker.R;
import com.example.spotifyadblocker.databinding.ActivityMainBinding;
import com.example.spotifyadblocker.databinding.NotificationAccessDialogBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;


import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding b;
    SharedPreferences sharedPreferences;

    BroadcastReceiver mReceiver;
    ArrayList<Integer> color;
   

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        sharedPreferences = getSharedPreferences(Constant.SHARED_PREF, Context.MODE_PRIVATE);

        setupSharedPreferences();

        registerLocalBroadcast();

        setupSwitchState();

        setupToolbar();

        setupPieChart();

        setupPieData();

        b.Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://dontkillmyapp.com")));
            }
        });

       b.btnLeaderboard.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this,LeaderBoardActivity.class));
           }
       });
    }

    private void setupSwitchState() {
        b.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ComponentName thisComponent = new ComponentName(MainActivity.this, NotificationListenerService.class);
                    PackageManager pm = getPackageManager();
                    pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                    if (!isNotificationServiceEnabled()) {
                        b.switch1.setChecked(false);
                        showNotificationAccessDialog();

                        return;
                    }
                    b.switch1.setText("ON");
                    b.switch1.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green_400));
                    sharedPreferences.edit()
                            .putBoolean(Constant.BLOCKER_SWITCH, true).apply();
                    sharedPreferences.edit()
                            .putString(Constant.BLOCKER_TEXT, "ON").apply();
                    if (!sharedPreferences.getString("previousSong", "").isEmpty()) {
                        b.songDescription.setVisibility(View.VISIBLE);
                        b.songName.setText(sharedPreferences.getString("previousSong", ""));
                    }
                    startService(new Intent(MainActivity.this, NotificationListenerCollectorService.class));
                } else {
                    b.switch1.setText("OFF");
                    b.switch1.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                    sharedPreferences.edit()
                            .putBoolean(Constant.BLOCKER_SWITCH, false)
                            .putString(Constant.BLOCKER_TEXT, "OFF").apply();
                    b.songDescription.setVisibility(View.INVISIBLE);
                    stopService(new Intent(MainActivity.this, NotificationListenerCollectorService.class));
                }
            }
        });
    }

    private void setupSharedPreferences() {

        if (sharedPreferences != null) {
            b.switch1.setChecked(sharedPreferences.getBoolean(Constant.BLOCKER_SWITCH, false));
            if (sharedPreferences.getBoolean(Constant.BLOCKER_SWITCH, false)) {
                b.switch1.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green_400));
                if (!sharedPreferences.getString("previousSong", "").isEmpty()) {
                    if(sharedPreferences.getInt(Constant.PLAYING_STATE,3)==3){
                        b.PlayingState.setText("Now Playing");
                    }
                    else {
                        b.PlayingState.setText("Paused");
                    }

                    b.songDescription.setVisibility(View.VISIBLE);
                    b.songName.setText(sharedPreferences.getString("previousSong", ""));

                }
            }

            b.switch1.setText(sharedPreferences.getString(Constant.BLOCKER_TEXT, "OFF"));
            b.countAd.setText("" + sharedPreferences.getInt(Constant.ADS_COUNTER, 0));
            b.countSongs.setText("" + sharedPreferences.getInt(Constant.SONGS_COUNTER, 0));


        }
    }

    private void setupToolbar() {
        b.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.browser:
                        Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dontkillmyapp.com"));
                        startActivity(Getintent);
                        break;
                    case R.id.faqs:
                        startActivity(new Intent(MainActivity.this, FAQSActivity.class));
                        break;
                }
                return true;
            }
        });
    }

    private void setupPieChart() {
        b.chart.setDrawHoleEnabled(false);
        b.chart.getDescription().setEnabled(false);
        b.chart.setTouchEnabled(false);
        b.chart.getLegend().setEnabled(false);

        color = new ArrayList<>();
        color.add(ContextCompat.getColor(MainActivity.this, R.color.pieChart2));
        color.add(ContextCompat.getColor(MainActivity.this, R.color.pieChart1));
    }

    private void setupPieData() {

        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        pieEntries.add(new PieEntry(sharedPreferences.getInt(Constant.SONGS_COUNTER, 0)));
        if (sharedPreferences.getInt(Constant.ADS_COUNTER, 0) > 0)
            pieEntries.add(new PieEntry(sharedPreferences.getInt(Constant.ADS_COUNTER, 0)));


        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(color);
        pieDataSet.setSliceSpace(1f);

       PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);
        pieData.setValueTextColor(ContextCompat.getColor(MainActivity.this, R.color.grey_800));
        pieData.setValueTextSize(24);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "" + (int) value;
            }
        });

        b.chart.setData(pieData);
        b.chart.invalidate();
    }


    private void showNotificationAccessDialog() {
        NotificationAccessDialogBinding notificationBinding = NotificationAccessDialogBinding.inflate(getLayoutInflater());
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
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
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Constant.SONGS_COUNTER:
                        setupPieData();
                        b.countSongs.setText("" + sharedPreferences.getInt(Constant.SONGS_COUNTER, 0));
                        b.songDescription.setVisibility(View.VISIBLE);
                        b.songName.setText(sharedPreferences.getString("previousSong", ""));
                        break;
                    case Constant.ADS_COUNTER:
                        setupPieData();
                        b.countAd.setText("" + sharedPreferences.getInt(Constant.ADS_COUNTER, 0));
                        break;
                    case Constant.BLOCKER_SWITCH:
                        b.switch1.setChecked(false);
                        break;
                    case Constant.PLAYING_STATE:
                        if(sharedPreferences.getInt(Constant.PLAYING_STATE,3)==3){
                            b.PlayingState.setText("Now Playing");
                        }
                        else {
                            b.PlayingState.setText("Paused");
                        }

                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.SONGS_COUNTER);
        filter.addAction(Constant.ADS_COUNTER);
        filter.addAction(Constant.BLOCKER_SWITCH);
        filter.addAction(Constant.PLAYING_STATE);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mReceiver, filter);
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