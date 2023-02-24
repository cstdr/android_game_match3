package com.cstdr.gamematch3.utils;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public interface Constant {

    /**
     * 游戏画布每行每列有几个游戏项
     */
    int GAME_ITEM_COLUMN_COUNT = 8;

    /**
     * 每个游戏项有多宽
     */
    int GAME_ITEM_WIDTH = 50;

    /**
     * 游戏项的种类有多少种
     */
    int GAME_ITEM_TYPE_COUNT = 7;

    /**
     * 空图标的类型
     */
    int GAME_ITEM_TYPE_NULL = -1;

    int GAME_ITEM_TYPE_0 = 0;
    int GAME_ITEM_TYPE_1 = 1;
    int GAME_ITEM_TYPE_2 = 2;
    int GAME_ITEM_TYPE_3 = 3;
    int GAME_ITEM_TYPE_4 = 4;
    int GAME_ITEM_TYPE_5 = 5;
    int GAME_ITEM_TYPE_6 = 6;

    /**
     * 游戏图标在被点击后缩小的宽度
     */
    int GAME_ITEM_IMAGEVIEW_WIDTH_REDUCE_RANGE = 5;

    /**
     * 游戏图标从哪个位置开始填充，一般消消乐都是从顶部
     */
    int GAME_ITEM_CHANGE_DIRECTION_LEFT = 0;
    int GAME_ITEM_CHANGE_DIRECTION_UP = 1;
    int GAME_ITEM_CHANGE_DIRECTION_RIGHT = 2;
    int GAME_ITEM_CHANGE_DIRECTION_DOWN = 3;

    /**
     * 图标未初始化状态||三消后的状态，需要创建图标
     */
    int GAME_ITEM_SHOW_TYPE_NULL = 0;
    /**
     * 图标正常状态
     */
    int GAME_ITEM_SHOW_TYPE_NORMAL = 1;

    int GAME_MATCH_SPEED = 200;


}
