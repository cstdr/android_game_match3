package com.cstdr.gamematch3.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

import com.cstdr.gamematch3.GameSplashActivity;

public class DialogUtil {

    /**
     * 游戏结束时弹窗
     * @param context
     * @param gameoverHandler
     */
    public static void showGameoverDialog(Context context, Handler gameoverHandler) {
        String msg = String.format("本轮得分：%d,  历史最高分：%d", MMKVUtil.GAME_SCORE_NUMBER, MMKVUtil.getCurrentModeHighestScoreNumber());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("游戏时间到！");
        builder.setMessage(msg);
        builder.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameoverHandler.sendEmptyMessage(0);
            }
        }).setNegativeButton("下次再玩", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameoverHandler.sendEmptyMessage(1);
            }
        });
        builder.show();
    }
}
