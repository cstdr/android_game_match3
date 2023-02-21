package com.cstdr.gamematch3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.cstdr.gamematch3.activity.GameActivity;

public class GameSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_splash);

        findViewById(R.id.tv_game_title).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(GameSplashActivity.this, GameActivity.class));
                finish();
            }
        }, 1000);

    }
}