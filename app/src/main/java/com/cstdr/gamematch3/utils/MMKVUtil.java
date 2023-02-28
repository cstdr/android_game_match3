package com.cstdr.gamematch3.utils;

import com.tencent.mmkv.MMKV;

public class MMKVUtil {

    // key=====================

    public static final String KEY_GAME_MODE_TIME_HIGHEST_SCORE_NUMBER = "KEY_GAME_MODE_TIME_HIGHEST_SCORE_NUMBER";
    public static final String KEY_GAME_MODE_PROPERTY_HIGHEST_SCORE_NUMBER = "KEY_GAME_MODE_PROPERTY_HIGHEST_SCORE_NUMBER";
    public static final String KEY_MODE = "KEY_MODE";


    // value=====================
    public static int GAME_SCORE_NUMBER = 0;

    public static int GAME_MODE_TIME_HIGHEST_SCORE_NUMBER = 0;
    public static int GAME_MODE_PROPERTY_HIGHEST_SCORE_NUMBER = 0;

    public static final int GAME_SCORE_TIMES = 1;

    public static final int MODE_TIME = 0;
    public static final int MODE_TIME_COUNTDOWN = 120;
    public static final int MODE_INFINITE = 1;

    public static final int MODE_TIME_PROPERTY = 2;

    public static int getCurrentModeHighestScoreNumber() {
        int mode = MMKV.defaultMMKV().getInt(KEY_MODE, 0);
        if (mode == MMKVUtil.MODE_TIME) {
            return MMKV.defaultMMKV().getInt(KEY_GAME_MODE_TIME_HIGHEST_SCORE_NUMBER, 0);
        } else if (mode == MMKVUtil.MODE_TIME_PROPERTY) {
            return MMKV.defaultMMKV().getInt(KEY_GAME_MODE_PROPERTY_HIGHEST_SCORE_NUMBER, 0);
        }

        return 0;
    }

    public static void saveCurrentModeHighestScoreNumber(int score) {
        int mode = MMKV.defaultMMKV().getInt(KEY_MODE, 0);
        if (mode == MMKVUtil.MODE_TIME) {
            MMKV.defaultMMKV().putInt(KEY_GAME_MODE_TIME_HIGHEST_SCORE_NUMBER, score);
        } else if (mode == MMKVUtil.MODE_TIME_PROPERTY) {
            MMKV.defaultMMKV().putInt(KEY_GAME_MODE_PROPERTY_HIGHEST_SCORE_NUMBER, score);
        }

    }

}
