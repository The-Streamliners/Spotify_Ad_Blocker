package com.example.spotifyadblocker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.spotifyadblocker.databinding.ActivityLeaderboardBinding;

public class LeaderBoardActivity extends AppCompatActivity {

    ActivityLeaderboardBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=ActivityLeaderboardBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
    }
}