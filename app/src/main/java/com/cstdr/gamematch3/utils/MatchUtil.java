package com.cstdr.gamematch3.utils;

import android.util.Log;

import com.cstdr.gamematch3.model.GameItem;

import java.util.ArrayList;
import java.util.List;

public class MatchUtil {

    private static final String TAG = MatchUtil.class.getSimpleName();

    /**
     * 计算from和to周边是否达到三消条件
     *
     * @param list 游戏图标list
     * @param from 点击的图标
     * @param to   点击的图标
     */
    public static void startMatch(List<GameItem> list, int from, int to) {
        // 最终匹配到需要执行三消到图标列表
        List<Integer> needMatchedList = new ArrayList<Integer>();

        doMatch(list, needMatchedList, from);
        doMatch(list, needMatchedList, to);

        if (needMatchedList.size() >= 3) {
            removeMatch(list, needMatchedList);
        }
        needMatchedList.clear();
    }

    private static void doMatch(List<GameItem> list, List<Integer> needMatchedList, int itemId) {
        Log.d(TAG, "doMatch: ===========");
        GameItem fromItem = list.get(itemId);

        // 统计横向和纵向有多少匹配到的图标
        List<Integer> matchedCountList = new ArrayList<Integer>();

        // 横向匹配开始
//        matchedCountList.add(fromItem.getPersonId());
        matchedCountList.add(list.indexOf(fromItem));
        checkMatch(list, itemId, -1, matchedCountList);
        checkMatch(list, itemId, 1, matchedCountList);
        if (matchedCountList.size() >= 3) { // 横向达到三消条件，提取匹配到的id，清空用于查询的list
            Log.d(TAG, "doMatch: 横向匹配到!!!!!matchedCountList.size() = " + matchedCountList.size());
            needMatchedList.addAll(matchedCountList);
        }
        matchedCountList.clear();

        // 纵向匹配开始
        matchedCountList.add(list.indexOf(fromItem));
        checkMatch(list, itemId, -Constant.GAME_ITEM_COLUMN_COUNT, matchedCountList);
        checkMatch(list, itemId, Constant.GAME_ITEM_COLUMN_COUNT, matchedCountList);
        if (matchedCountList.size() >= 3) { // 纵向达到三消条件，提取匹配到的id，清空用于查询的list
            Log.d(TAG, "doMatch: 纵向匹配到!!!!!matchedCountList.size() = " + matchedCountList.size());
            needMatchedList.addAll(matchedCountList);
        }
        matchedCountList.clear();

        Log.d(TAG, "startMatch: itemId = " + itemId);
        Log.d(TAG, "startMatch: needMatchedList.size() = " + needMatchedList.size());
    }

    private static void removeMatch(List<GameItem> list, List<Integer> needMatchedList) {
        Log.d(TAG, "removeMatch: ========");
        for (int i = 0; i < needMatchedList.size(); i++) {
            int id = needMatchedList.get(i);
            if (id < list.size()) {
                list.remove(id);
            }
        }
    }

    private static boolean checkMatch(List<GameItem> list, int i, int direction, List<Integer> matchedCountList) {
        Log.d(TAG, "checkMatch: ========");
        // 向左匹配，位置只能从最右一列开始
        if ((i + direction) < 0 || (direction == -1 && ((i + direction) % Constant.GAME_ITEM_COLUMN_COUNT == (Constant.GAME_ITEM_COLUMN_COUNT - 1)))) {
            return false;
        }
        // 向右匹配，位置只能从最左一列开始
        if (direction == 1 && ((i + direction) % Constant.GAME_ITEM_COLUMN_COUNT == 0)) {
            return false;
        }
        // 向上匹配，位置不能低于0
        if (direction == -Constant.GAME_ITEM_COLUMN_COUNT && (i + direction < 0)) {
            return false;
        }
        // 向下匹配，位置不能高于所有图标总和
        if (direction == Constant.GAME_ITEM_COLUMN_COUNT && (i + direction >= Constant.GAME_ITEM_COLUMN_COUNT * Constant.GAME_ITEM_COLUMN_COUNT)) {
            return false;
        }


        Log.d(TAG, "checkMatch: ========i = " + i);
        Log.d(TAG, "checkMatch: ========direction = " + direction);


        GameItem item1 = list.get(i);
        GameItem item2 = list.get(i + direction);

        // 注意检查是否到画布边缘，到了要停止查询

        if (item1.getType() == item2.getType()) {
            Log.d(TAG, "checkMatch: type相等 " + i + "和" + (i + direction));

            matchedCountList.add(i + direction);
            return checkMatch(list, i + direction, direction, matchedCountList);
        } else {
            return false;
        }
    }

}
