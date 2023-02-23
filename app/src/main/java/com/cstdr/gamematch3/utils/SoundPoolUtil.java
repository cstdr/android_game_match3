package com.cstdr.gamematch3.utils;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import com.cstdr.gamematch3.R;

import java.util.HashMap;

public class SoundPoolUtil {

    private static final String KEY_COVER_BGM = "KEY_COVER_BGM";
    private static final String KEY_EXPLODE_BGM = "KEY_EXPLODE_BGM";
    private static final String KEY_SUCCESS_BGM = "KEY_SUCCESS_BGM";
    private static final String KEY_FAIL_BGM = "KEY_FAIL_BGM";
    private static final String TAG = SoundPoolUtil.class.getSimpleName();


    private static SoundPool sp;

    private static HashMap<String, Integer> soundIds;

    private static int mCoverStreamId;

    public static void initSoundPool(Context context) {
        soundIds = new HashMap<>();
        sp = new SoundPool.Builder().setMaxStreams(10).build();
        soundIds.put(KEY_COVER_BGM, sp.load(context, R.raw.bgm_cover, 1));
        soundIds.put(KEY_EXPLODE_BGM, sp.load(context, R.raw.bgm_explode, 1));
        soundIds.put(KEY_SUCCESS_BGM, sp.load(context, R.raw.bgm_success, 1));
        soundIds.put(KEY_FAIL_BGM, sp.load(context, R.raw.bgm_fail, 1));
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == soundIds.get(KEY_COVER_BGM) && status == 0) {
                    playCoverBgm();
                }
            }
        });
    }

    public static void playCoverBgm() {
        int streamId = playDefault(soundIds.get(KEY_COVER_BGM), true);
        if (streamId != 0) {
            mCoverStreamId = streamId;
        }
    }

    public static void stopCoverBgm() {
        if (mCoverStreamId != 0) {
            sp.stop(mCoverStreamId);
        }
    }

    public static void playExplode() {
        playDefault(soundIds.get(KEY_EXPLODE_BGM), false);
    }

    public static void playSuccess() {
        playDefault(soundIds.get(KEY_SUCCESS_BGM), false);
    }

    public static void playFail() {
        playDefault(soundIds.get(KEY_FAIL_BGM), false);
    }

    public static void release() {
        if (sp != null) {
            sp.release();
        }
    }

    private static int playDefault(int id, boolean isLoop) {
        if (isLoop) {
            return sp.play(id, 1, 1, 1, -1, 1);
        } else {
            return sp.play(id, 1, 1, 1, 0, 1);
        }
    }

    public static void pauseAll() {
        sp.autoPause();
    }

    public static void resumeAll() {
        sp.autoResume();
    }


}