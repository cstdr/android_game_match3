package com.cstdr.gamematch3;

import android.app.Application;
import android.util.Log;

import com.cstdr.gamematch3.utils.Constant;
import com.cstdr.gamematch3.utils.MMKVUtil;
import com.cstdr.gamematch3.utils.SoundPoolUtil;
import com.tencent.mmkv.MMKV;

public class GameApplication extends Application {

    private static final String TAG = GameApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: ========");
        MMKV.initialize(this);

        MMKVUtil.GAME_MODE_TIME_HIGHEST_SCORE_NUMBER = MMKV.defaultMMKV().getInt(MMKVUtil.KEY_GAME_MODE_TIME_HIGHEST_SCORE_NUMBER, 0);
        MMKVUtil.GAME_MODE_PROPERTY_HIGHEST_SCORE_NUMBER = MMKV.defaultMMKV().getInt(MMKVUtil.KEY_GAME_MODE_PROPERTY_HIGHEST_SCORE_NUMBER, 0);

        SoundPoolUtil.initSoundPool(this);
    }

}
