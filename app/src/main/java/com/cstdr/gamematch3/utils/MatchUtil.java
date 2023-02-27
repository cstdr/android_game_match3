package com.cstdr.gamematch3.utils;

import android.util.Log;

import com.cstdr.gamematch3.R;
import com.cstdr.gamematch3.model.GameItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 三消算法工具类
 */
public class MatchUtil {

    private static final String TAG = MatchUtil.class.getSimpleName();

    /**
     * 查询应该消除的图标位置
     *
     * @param list
     * @param itemIdList
     * @return
     */
    public static List<Integer> startMatch(List<GameItem> list, List<Integer> itemIdList) {
        // 最终匹配到需要执行三消到图标列表
        List<Integer> needMatchedList = new ArrayList<>();

        if (itemIdList != null) { // 转换两个位置时，只查询这两个位置转换后是否达到三消条件
            for (int i : itemIdList) {
                doMatch(list, needMatchedList, i);
            }
        } else { // 查询全图位置是否达到三消条件
            for (int i = 0; i < Constant.GAME_ITEM_COLUMN_COUNT * Constant.GAME_ITEM_COLUMN_COUNT; i++) {
                doMatch(list, needMatchedList, i);
            }
        }

        // 查询出的位置列表排下序，目前算法改后不排序也可以
        Collections.sort(needMatchedList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });

        // 检查三消的图标里有没有特殊属性，然后进行处理
        int size = needMatchedList.size();
        if (size >= 3) {
            for (int i = 0; i < size; i++) {
                int position = needMatchedList.get(i);
                GameItem gameItem = list.get(position);
                if (gameItem.getProperty() != Constant.GAME_ITEM_PROPERTY_NORMAL) {
                    startPropertyMatch(list, needMatchedList, position);
                }
            }
        }
        return needMatchedList;
    }

    /**
     * 开始处理特殊属性的图标（横向、纵向、同类）
     *
     * @param list
     * @param needMatchedList
     * @param itemPosition
     */
    private static void startPropertyMatch(List<GameItem> list, List<Integer> needMatchedList, int itemPosition) {
        GameItem gameItem = list.get(itemPosition);
        int property = gameItem.getProperty();
        int position;
        if (property == Constant.GAME_ITEM_PROPERTY_PORT) {
            int[] xy = PositionUtil.getXYFromPosition(itemPosition);
            for (int x = 0; x < Constant.GAME_ITEM_COLUMN_COUNT; x++) {
                position = PositionUtil.getPositionFromXY(x, xy[1]);
                needMatchedList.add(position);
            }
        } else if (property == Constant.GAME_ITEM_PROPERTY_LAND) {
            int[] xy = PositionUtil.getXYFromPosition(itemPosition);
            for (int y = 0; y < Constant.GAME_ITEM_COLUMN_COUNT; y++) {
                position = PositionUtil.getPositionFromXY(xy[0], y);
                needMatchedList.add(position);
            }
        } else if (property == Constant.GAME_ITEM_PROPERTY_SAME) {
            int type = gameItem.getType();
            for (int pos = 0; pos < Constant.GAME_ITEM_COLUMN_COUNT * Constant.GAME_ITEM_COLUMN_COUNT; pos++) {
                GameItem item = list.get(pos);
                if (item.getType() == type) {
                    needMatchedList.add(pos);
                }
            }
        }
    }

    /**
     * 查询出需要消除的位置并存入list
     *
     * @param list
     * @param needMatchedList
     * @param itemId
     */
    private static void doMatch(List<GameItem> list, List<Integer> needMatchedList, int itemId) {
//        Log.d(TAG, "doMatch: ===========");
        GameItem fromItem = list.get(itemId);
        // 已经消除的位置不需要再查
        if (fromItem.getType() == Constant.GAME_ITEM_TYPE_NULL) {
            return;
        }

        // 统计横向和纵向有多少匹配到的图标
        List<Integer> matchedCountList = new ArrayList<>();

        // 横向匹配开始
//        matchedCountList.add(fromItem.getPersonId());
        matchedCountList.add(list.indexOf(fromItem));

        // 向左查询
        checkMatch(list, itemId, -1, matchedCountList);
        // 向右查询
        checkMatch(list, itemId, 1, matchedCountList);
        if (matchedCountList.size() >= 3) { // 横向达到三消条件，提取匹配到的id，清空用于查询的list
//            Log.d(TAG, "doMatch: 横向匹配到!!!!!matchedCountList.size() = " + matchedCountList.size());
            for (int i : matchedCountList) {
                if (!needMatchedList.contains(i)) { // 可能出现位置重复添加
                    needMatchedList.add(i);
                }
            }
        }
        matchedCountList.clear();

        // 纵向匹配开始
        matchedCountList.add(list.indexOf(fromItem));
        // 向上查询
        checkMatch(list, itemId, -Constant.GAME_ITEM_COLUMN_COUNT, matchedCountList);
        // 向下查询
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

    /**
     * @param list
     * @param needMatchedList
     * @deprecated 老算法需要删除图标，已废弃
     */
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

    /**
     * 向某个方向递归查询相邻两个图标是否相同
     *
     * @param list             所有图标列表
     * @param i                查询的起始位置
     * @param direction        查询方向
     * @param matchedCountList 查询出的需要消除的图标位置列表
     * @return
     */
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

        // 废弃逻辑：原因是可能会遗漏需要消除的图标，比如三消后引爆了某个边缘的特殊图标，可能只有单边会纵向或者横向消除
        // 遇到特殊属性图标，处理特殊逻辑，这里为了代码结构清晰，没有把判定写复杂
        // 纵向属性
//        if (item1.getProperty() == Constant.GAME_ITEM_PROPERTY_PORT) {
//            if (direction == -Constant.GAME_ITEM_COLUMN_COUNT || direction == Constant.GAME_ITEM_COLUMN_COUNT) {
//                matchedCountList.add(i + direction);
//                return checkMatch(list, i + direction, direction, matchedCountList);
//            }
//        } else if (item1.getProperty() == Constant.GAME_ITEM_PROPERTY_LAND) { // 横向属性
//            if (direction == -1 || direction == 1) {
//                matchedCountList.add(i + direction);
//                return checkMatch(list, i + direction, direction, matchedCountList);
//            }
//        }

        // 注意检查是否到画布边缘，到了要停止查询
        if (item1.getType() == item2.getType()) {
//            Log.d(TAG, "checkMatch: type相等 " + i + "和" + (i + direction));

            matchedCountList.add(i + direction);

            // 继续递归查询相邻位置
            return checkMatch(list, i + direction, direction, matchedCountList);
        } else {
            return false;
        }
    }

    /**
     * 根据需要消除的位置，进行实际的图标位置调整，默认方向是三消后新补充的图标从上方落下
     *
     * @param mListGameItems  所有图标列表
     * @param needMatchedList 需要消除的图标位置列表
     */
    public static void startChange(List<GameItem> mListGameItems, List<Integer> needMatchedList) {
        startChange(mListGameItems, needMatchedList, Constant.GAME_ITEM_CHANGE_DIRECTION_UP);
    }

    /**
     * 根据需要消除的位置，进行实际的图标位置调整，三消后新补充的图标从传入方向补充
     * TODO bug:如果竖列多个三消，会导致后续补充图标错乱
     *
     * @param mListGameItems  所有图标列表
     * @param needMatchedList 需要消除的图标位置列表
     * @param direction       TODO 目前没有写逻辑，原理是调整x和y的for循环顺序就可以实现
     */
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

        // 这里不同的x和y循环顺序，可以调整补充图标的插入方向
        // 下面逻辑是从上方向下补充新图标
        for (int y = 0; y < Constant.GAME_ITEM_COLUMN_COUNT; y++) {
            for (int x = Constant.GAME_ITEM_COLUMN_COUNT - 1; x >= 0; x--) {
                position = PositionUtil.getPositionFromXY(x, y);
                // 查询是否需要消除
                for (int z = 0; z < needMatchedList.size(); z++) {
                    // 如果查询到
                    if (position == needMatchedList.get(z)) {
                        isMatched = true;
                        // 记录每列第一次查询到的竖列位置
                        if (!isYStart) {
                            yStartId = x;
                            isYStart = true;
                        }
                        break;
                    }
                }
                // 该列有需要消除的图标，并且当前位置不是需要消除的图标，则把该图标位置加入到list
                // 该list相当于把消除后的列里其他剩余图标存放，然后一起移到底部
                if (yStartId > -1 && !isMatched) {
//                    Log.d(TAG, "需要移动的position = " + position);
                    yNeedDownList.add(position);
                }
                isMatched = false;
            }

            if (yStartId > -1) {
                int yNeedDownId = 0;
                for (int x = yStartId; x >= 0; x--) {
//                    Log.d(TAG, "startChange: x =" + x);

                    position = PositionUtil.getPositionFromXY(x, y);
//                    Log.d(TAG, "当前位置position = " + position);
//                    Log.d(TAG, "startChange: yNeedDownList.size() = " + yNeedDownList.size());
//                    Log.d(TAG, "startChange: yNeedDownList = " + yNeedDownList);
                    // 该位置为三消的图标，则后面正常图标向底部补充，直到剩余图标补充完
                    if (yNeedDownId < yNeedDownList.size()) {
                        int needDownPosition = yNeedDownList.get(yNeedDownId);
//                        Log.d(TAG, "startChange: needDownPosition = " + needDownPosition);
                        GameItem needDownItem = mListGameItems.get(needDownPosition);
                        mListGameItems.set(position, needDownItem);
                        yNeedDownId++;
                    } else { // 原来剩余图标补充完，后面空出的位置修改为空类型图标，留给restUI函数进行创建新图标
//                        Log.d(TAG, "!!!!===position = " + position);
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
