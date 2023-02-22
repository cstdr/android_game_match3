package com.cstdr.gamematch3.utils;

public class TimeUtil {

    public static String getTimeStr(int time) {
        int minus = time / 60;
        String minusStr;
        if (minus < 1) {
            minusStr = "00";
        } else if (minus < 10) {
            minusStr = String.format("0%d", minus);
        } else {
            minusStr = String.format("%d", minus);
        }
        int second = time % 60;
        String secondStr;
        if (second < 10) {
            secondStr = String.format("0%d", second);
        } else {
            secondStr = String.format("%d", second);
        }
        return String.format("%s:%s", minusStr, secondStr);
    }
}
