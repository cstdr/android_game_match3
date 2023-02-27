package com.cstdr.gamematch3.utils;

public class PositionUtil {

    public static int[] getXYFromPosition(int positon) {
        int[] xy = new int[2];
        int x = positon / Constant.GAME_ITEM_COLUMN_COUNT;
        int y = positon % Constant.GAME_ITEM_COLUMN_COUNT;
        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    public static int getPositionFromXY(int x, int y) {
        int position = x * Constant.GAME_ITEM_COLUMN_COUNT + y;
        return position;
    }
}
