package com.cstdr.gamematch3.utils;

import android.util.Log;

import com.cstdr.gamematch3.R;
import com.cstdr.gamematch3.model.GameItem;
import com.google.android.material.math.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchUtil {

    private static final String TAG = MatchUtil.class.getSimpleName();

    public static List<Integer> startMatch(List<GameItem> list, List<Integer> itemIdList) {
        // 最终匹配到需要执行三消到图标列表
        List<Integer> needMatchedList = new ArrayList<>();

        if (itemIdList != null) {
            for (int i : itemIdList) {
                doMatch(list, needMatchedList, i);
            }
        } else {
            for (int i = 0; i < Constant.GAME_ITEM_COLUMN_COUNT * Constant.GAME_ITEM_COLUMN_COUNT; i++) {
                doMatch(list, needMatchedList, i);
            }
        }
        Collections.sort(needMatchedList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });

        if (needMatchedList.size() >= 3) {
            // 现在不删除图标，只修改
//            removeMatch(list, needMatchedList);
        }
        return needMatchedList;
    }

    private static void doMatch(List<GameItem> list, List<Integer> needMatchedList, int itemId) {
//        Log.d(TAG, "doMatch: ===========");
        GameItem fromItem = list.get(itemId);
        if (fromItem.getType() == Constant.GAME_ITEM_TYPE_NULL) {
            return;
        }

        // 统计横向和纵向有多少匹配到的图标
        List<Integer> matchedCountList = new ArrayList<>();

        // 横向匹配开始
//        matchedCountList.add(fromItem.getPersonId());
        matchedCountList.add(list.indexOf(fromItem));
        checkMatch(list, itemId, -1, matchedCountList);
        checkMatch(list, itemId, 1, matchedCountList);
        if (matchedCountList.size() >= 3) { // 横向达到三消条件，提取匹配到的id，清空用于查询的list
//            Log.d(TAG, "doMatch: 横向匹配到!!!!!matchedCountList.size() = " + matchedCountList.size());
            for (int i : matchedCountList) {
                if (!needMatchedList.contains(i)) {
                    needMatchedList.add(i);
                }
            }
        }
        matchedCountList.clear();

        // 纵向匹配开始
        matchedCountList.add(list.indexOf(fromItem));
        checkMatch(list, itemId, -Constant.GAME_ITEM_COLUMN_COUNT, matchedCountList);
        checkMatch(list, itemId, Constant.GAME_ITEM_COLUMN_COUNT, matchedCountList);
        if (matchedCountList.size() >= 3) { // 纵向达到三消条件，提取匹配到的id，清空用于查询的list
//            Log.d(TAG, "doMatch: 纵向匹配到!!!!!matchedCountList.size() = " + matchedCountList.size());
            for (int i : matchedCountList) {
                if (!needMatchedList.contains(i)) {
                    needMatchedList.add(i);
                }
            }
        }
        matchedCountList.clear();

//        Log.d(TAG, "startMatch: itemId = " + itemId);
//        Log.d(TAG, "startMatch: needMatchedList.size() = " + needMatchedList.size());
    }

    private static void removeMatch(List<GameItem> list, List<Integer> needMatchedList) {
        Log.d(TAG, "removeMatch: ========" + needMatchedList.size());

        // 不排序可能出现先删除低id元素，导致再删除高id的时候出现越界
        Collections.sort(needMatchedList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        for (int i = needMatchedList.size() - 1; i >= 0; i--) {
            int id = needMatchedList.get(i);
            Log.d(TAG, "removeMatch: id = " + id);
            list.remove(id);
        }
    }

    private static boolean checkMatch(List<GameItem> list, int i, int direction, List<Integer> matchedCountList) {
//        Log.d(TAG, "checkMatch: ========");
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


//        Log.d(TAG, "checkMatch: ========i = " + i);
//        Log.d(TAG, "checkMatch: ========direction = " + direction);


        GameItem item1 = list.get(i);
        GameItem item2 = list.get(i + direction);

        // 如果检查到空图标，要停止
        if (item1.getType() == Constant.GAME_ITEM_TYPE_NULL || item2.getType() == Constant.GAME_ITEM_TYPE_NULL) {
            return false;
        }

        // 注意检查是否到画布边缘，到了要停止查询
        if (item1.getType() == item2.getType()) {
            Log.d(TAG, "checkMatch: type相等 " + i + "和" + (i + direction));

            matchedCountList.add(i + direction);
            return checkMatch(list, i + direction, direction, matchedCountList);
        } else {
            return false;
        }
    }

    public static void startChange(List<GameItem> mListGameItems, List<Integer> needMatchedList) {
        startChange(mListGameItems, needMatchedList, Constant.GAME_ITEM_CHANGE_DIRECTION_UP);
    }

    // TODO bug:如果竖列多个三消，会导致后续补充图标错乱
    public static void startChange(List<GameItem> mListGameItems, List<Integer> needMatchedList, int direction) {
        Log.d(TAG, "startChange:  =========");
        Log.d(TAG, "startChange: needMatchedList.size = " + needMatchedList.size());
        Log.d(TAG, "startChange: needMatchedList.size = " + needMatchedList);

        List<Integer> yNeedDownList = new ArrayList<>();
        //每一列匹配到三消图标时
        int yStartId = -1;
        int position;
        boolean isMatched = false;
        boolean isYStart = false;

        for (int y = 0; y < Constant.GAME_ITEM_COLUMN_COUNT; y++) {
            for (int x = Constant.GAME_ITEM_COLUMN_COUNT - 1; x >= 0; x--) {
                position = PositionUtil.getPositionFromXY(x, y);
                for (int z = 0; z < needMatchedList.size(); z++) {
                    if (position == needMatchedList.get(z)) {
                        isMatched = true;
                        if (!isYStart) {
                            yStartId = x;
                            isYStart = true;
                        }
                        break;
                    }
                }
                if (yStartId > -1 && !isMatched) {
                    Log.d(TAG, "需要移动的position = " + position);
                    yNeedDownList.add(position);
                }
                isMatched = false;
            }

            if (yStartId > -1) {
                int yNeedDownId = 0;
                for (int x = yStartId; x >= 0; x--) {
                    Log.d(TAG, "startChange: x =" + x);

                    position = PositionUtil.getPositionFromXY(x, y);
                    Log.d(TAG, "当前位置position = " + position);
                    Log.d(TAG, "startChange: yNeedDownList.size() = " + yNeedDownList.size());
                    Log.d(TAG, "startChange: yNeedDownList = " + yNeedDownList);
                    // 该位置为三消的图标，则后面正常图标向前补
                    if (yNeedDownId < yNeedDownList.size()) {
                        int needDownPosition = yNeedDownList.get(yNeedDownId);
                        Log.d(TAG, "startChange: needDownPosition = " + needDownPosition);
                        GameItem needDownItem = mListGameItems.get(needDownPosition);
                        mListGameItems.set(position, needDownItem);
                        yNeedDownId++;
                    } else {
                        Log.d(TAG, "!!!!===position = " + position);
                        GameItem nullItem = mListGameItems.get(position);
                        nullItem.setShowStatus(Constant.GAME_ITEM_SHOW_TYPE_NULL);
                        nullItem.setImage(R.drawable.btn_bg);
                        nullItem.setType(Constant.GAME_ITEM_TYPE_NULL);
                        mListGameItems.set(position, nullItem);
                    }
                }

                yStartId = -1;
                isYStart = false;
                yNeedDownList.clear();
            }
        }
    }
}
