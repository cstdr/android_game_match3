package com.cstdr.gamematch3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cstdr.gamematch3.activity.GameActivity;
import com.cstdr.gamematch3.utils.MMKVUtil;
import com.cstdr.gamematch3.utils.SoundPoolUtil;

public class GameSplashActivity extends AppCompatActivity {

    private Context mContext;
    private Button mBtnTimeMode;
    private Button mBtnInfiniteMode;

    private Button mBtnPropertyMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_splash);
        mContext = this;

        initView();
        initData();
    }


    private void initView() {
        mBtnTimeMode = findViewById(R.id.btn_time_mode);
        mBtnInfiniteMode = findViewById(R.id.btn_infinite_mode);
        mBtnPropertyMode = findViewById(R.id.btn_time_property_mode);
    }

    private void initData() {
        mBtnTimeMode.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, GameActivity.class);
            intent.putExtra(MMKVUtil.KEY_MODE, MMKVUtil.MODE_TIME);
            startActivity(intent);
        });

        mBtnInfiniteMode.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, GameActivity.class);
            intent.putExtra(MMKVUtil.KEY_MODE, MMKVUtil.MODE_INFINITE);
            startActivity(intent);
        });

        mBtnPropertyMode.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, GameActivity.class);
            intent.putExtra(MMKVUtil.KEY_MODE, MMKVUtil.MODE_TIME_PROPERTY);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SoundPoolUtil.resumeAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundPoolUtil.pauseAll();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundPoolUtil.release();
    }
}