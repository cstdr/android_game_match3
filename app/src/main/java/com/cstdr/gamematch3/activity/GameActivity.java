package com.cstdr.gamematch3.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstdr.gamematch3.R;
import com.cstdr.gamematch3.model.GameItem;
import com.cstdr.gamematch3.utils.Constant;
import com.cstdr.gamematch3.utils.DialogUtil;
import com.cstdr.gamematch3.utils.MMKVUtil;
import com.cstdr.gamematch3.utils.MatchUtil;
import com.cstdr.gamematch3.utils.PositionUtil;
import com.cstdr.gamematch3.utils.SoundPoolUtil;
import com.cstdr.gamematch3.utils.TimeUtil;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.tencent.mmkv.MMKV;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 游戏主界面
 * 功能详见 （https://github.com/cstdr/android_game_match3/blob/main/README.md）
 */
public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getSimpleName();

    private Context mContext;
    /**
     * 游戏画布
     */
    private GridLayout mGlGameBoard;

    /**
     * 积分数值
     */
    private TextView mTvScoreNumber;
    /**
     * 计时数值
     */
    private TextView mTvTimeNumber;

    /**
     * 存放图标数据
     */
    private List<GameItem> mListGameItems;
    /**
     * 存放图标的layout，方便调整样式
     */
    private List<RelativeLayout> mListItemLayouts;
    /**
     * 需要执行动画的切换layoutId列表
     */
    private List<Integer> mNeedAnimLayoutIdList;
    /**
     * 需要执行动画的补充图标layoutId列表
     */
    private List<Integer> mNewItemLayoutIdList;

    /**
     * 第一次点击的图标位置
     */
    private int mFirstClickedItemId = -1;

    private String[] mPersonNameList;
    private TypedArray mPersonImageArray;
    private Animation showAnimation;
    private Animation hideAnimation;

    /**
     * 计时int数值
     */
    private int mCountdown;

    /**
     * 刷新画布图标的handler
     */
    private Handler resetUIHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            Log.d(TAG, "handleMessage: resetUIhandler =============");
            resetUI();
//            setNeedAnimIds(needMatchedList);

//            // 三消后，新添加的图标动画显示入场
//            startNewItemAnim(true);

            // 查询哪些图标位置应该消除
            List<Integer> needMatchedList = MatchUtil.startMatch(mListGameItems, null);
            Log.d(TAG, "handleMessage: needMatchedList = " + needMatchedList.size());
            if (needMatchedList.size() >= 3) { // 达到三消条件
                setNeedAnimIds(needMatchedList);
                startAnim(false);

                // 开始图标位置转换
                MatchUtil.startChange(mListGameItems, needMatchedList);

                Message message = new Message();
                message.arg1 = needMatchedList.size();
                scoreHandler.sendMessage(message);

                resetUIHandler.sendEmptyMessageDelayed(0, Constant.GAME_MATCH_SPEED);
            }
            return true;
        }
    });

    /**
     * 计算积分的handler
     */
    private Handler scoreHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int score = msg.arg1;
            if (score > 0) {
                MMKVUtil.GAME_SCORE_NUMBER += score * MMKVUtil.GAME_SCORE_TIMES;
                mTvScoreNumber.setText(String.format("%d", MMKVUtil.GAME_SCORE_NUMBER));
                // TODO 加分时发生音效
                SoundPoolUtil.playExplode();
            }
            return false;
        }
    });

    /**
     * 计算计时数值的handler
     */
    private Handler countdownHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                mCountdown = MMKVUtil.MODE_TIME_COUNTDOWN;
            }
            mCountdown--;
            String timeStr = TimeUtil.getTimeStr(mCountdown);
            mTvTimeNumber.setText(timeStr);
            if (mCountdown <= 0) {
                if (MMKVUtil.GAME_SCORE_NUMBER > MMKVUtil.GAME_HIGHEST_SCORE_NUMBER) {
                    MMKVUtil.GAME_HIGHEST_SCORE_NUMBER = MMKVUtil.GAME_SCORE_NUMBER;

                    // TODO 游戏结束时超过最高分发生音效
                    SoundPoolUtil.playSuccess();
                } else {
                    // TODO 游戏结束时挑战失败发生音效
                    SoundPoolUtil.playFail();
                }
                DialogUtil.showGameoverDialog(mContext, gameoverHandler);

                return true;
            } else {
                countdownHandler.sendEmptyMessageDelayed(0, 1000);
            }
            return true;
        }
    });

    /**
     * 点击游戏结束对话框后的逻辑
     */
    private Handler gameoverHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
//            MMKVUtil.GAME_SCORE_NUMBER = 0;
            clearGameData();

            if (msg.what == 0) { // 重新开始
                resetUIHandler.sendEmptyMessage(0);
                countdownHandler.sendEmptyMessageDelayed(1, 1000);
                mTvScoreNumber.setText(String.format("%d", MMKVUtil.GAME_SCORE_NUMBER));
            } else if (msg.what == 1) { // 下次再玩
                finish();
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mContext = this;

        initView();
        initAnimation();
        initData();

        initMode();

//        resetUI();
//        startNewItemAnim(true);

        resetUIHandler.sendEmptyMessageDelayed(0, Constant.GAME_MATCH_SPEED);
    }

    private void initMode() {
        Intent intent = getIntent();
        if (intent != null) {
            int mode = intent.getIntExtra(MMKVUtil.KEY_MODE, 0);
            if (mode == MMKVUtil.MODE_TIME) {
                mCountdown = MMKVUtil.MODE_TIME_COUNTDOWN;
                countdownHandler.sendEmptyMessage(0);

            } else if (mode == MMKVUtil.MODE_INFINITE) {
                mTvTimeNumber.setText("无限模式");
            }
        }

    }


    private void initView() {
        mGlGameBoard = findViewById(R.id.gl_game);
        mGlGameBoard.setColumnCount(Constant.GAME_ITEM_COLUMN_COUNT);
        mGlGameBoard.setRowCount(Constant.GAME_ITEM_COLUMN_COUNT);

        mTvScoreNumber = findViewById(R.id.tv_score_number);
        mTvTimeNumber = findViewById(R.id.tv_time_number);

    }

    private void initAnimation() {
        showAnimation = new AlphaAnimation(0, 1);
        showAnimation.setDuration(200);
        showAnimation.setFillAfter(true);
        showAnimation.setFillBefore(false);

        hideAnimation = new AlphaAnimation(1, 0);
        hideAnimation.setDuration(200);
        hideAnimation.setFillAfter(false);
        hideAnimation.setFillBefore(true);
    }


    private void initData() {
        mListGameItems = new ArrayList<GameItem>();
        mPersonNameList = getResources().getStringArray(R.array.persons_name);
        mPersonImageArray = getResources().obtainTypedArray(R.array.persons);

        mListItemLayouts = new ArrayList<RelativeLayout>();
        mNeedAnimLayoutIdList = new ArrayList<Integer>();
        mNewItemLayoutIdList = new ArrayList<Integer>();

        initListGameItems(mListGameItems);

//        mTvScoreNumber.setText(String.format("%d", MMKVUtil.GAME_SCORE_NUMBER));
    }

    private void initListGameItems(List<GameItem> mListGameItems) {
        for (int x = 0; x < Constant.GAME_ITEM_COLUMN_COUNT; x++) {
            for (int y = 0; y < Constant.GAME_ITEM_COLUMN_COUNT; y++) {
                GameItem gameItem = newGameItem(x, y);
                mListGameItems.add(gameItem);
            }
        }
    }

    private void setOnClick(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) v.getTag();
                Log.d(TAG, "onClick: id = " + id);



                // 如果第一次是这个id，或者刚三消后，则把这次设为第一次点击
                if (mFirstClickedItemId == -1 || mFirstClickedItemId == id) {
                    mFirstClickedItemId = id;
                    // TODO 点击效果
                    setImageViewReduceRange(v);

                    // 点击音效
                    SoundPoolUtil.playClick();
                    return;
                } else {
                    // 点击音效
                    SoundPoolUtil.playClickSecond();
                    // TODO 转换消失动画去掉后，不容易出现快速点击的误触问题
//                    setNeedAnimId(mFirstClickedItemId);
//                    setNeedAnimId(id);
//                    startAnim(false);

                    // 点击后两个图标id切换
//                    Collections.swap(mListGameItems, mFirstClickedItemId, id);
                    swapItem(mFirstClickedItemId, id);
                    resetUI(); // TODO

//                    setNeedAnimId(mFirstClickedItemId);
//                    setNeedAnimId(id);
//                    startAnim(true);


                    // 传入点击的两个点位置，进行三消判定
                    List<Integer> itemIdList = new ArrayList<Integer>();
                    itemIdList.add(mFirstClickedItemId);
                    itemIdList.add(id);

                    // 获取需要三消的id列表
                    List<Integer> needMatchedList = MatchUtil.startMatch(mListGameItems, itemIdList);

                    if (needMatchedList.size() >= 3) {
                        // 需要三消的图标开始动画
                        setNeedAnimIds(needMatchedList);
                        startAnim(false);

                        // 开始图标位置转换
                        MatchUtil.startChange(mListGameItems, needMatchedList);

                        Message message = new Message();
                        message.arg1 = needMatchedList.size();
                        scoreHandler.sendMessage(message);

                        resetUIHandler.sendEmptyMessageDelayed(0, Constant.GAME_MATCH_SPEED);
                    }
                    resetClickedItemId();
                }
            }
        });
    }

    /**
     * 设置点击后的图标效果
     * @param v
     */
    private void setImageViewReduceRange(View v) {
        int reduceRange = QMUIDisplayHelper.dp2px(mContext, Constant.GAME_ITEM_IMAGEVIEW_WIDTH_REDUCE_RANGE);
        v.setPadding(reduceRange, reduceRange, reduceRange, reduceRange);
        v.setBackgroundResource(R.color.orange);
    }

    /**
     * 点击第二个位置后切换图标
     * @param from
     * @param to
     */
    private void swapItem(int from, int to) {
        GameItem fromItem = mListGameItems.get(from);
        GameItem toItem = mListGameItems.get(to);
        mListGameItems.set(from, toItem);
        mListGameItems.set(to, fromItem);
    }

    private void setNeedAnimId(int id) {
        mNeedAnimLayoutIdList.add(id);
    }

    private void setNeedAnimIds(List<Integer> ids) {
        mNeedAnimLayoutIdList.addAll(ids);
    }

    private void startAnim(boolean isShow) {
        for (int i = 0; i < mNeedAnimLayoutIdList.size(); i++) {
            int id = mNeedAnimLayoutIdList.get(i);
            RelativeLayout layout = mListItemLayouts.get(id);
            layout.clearAnimation();
            if (isShow) {
                layout.startAnimation(showAnimation);
            } else {
                layout.startAnimation(hideAnimation);
            }
        }
        mNeedAnimLayoutIdList.clear();
    }

//    private void setNewItemLayoutId(int id) {
//        mNewItemLayoutIdList.add(id);
//    }

//    private void startNewItemAnim(boolean isShow) {
//        for (int i = 0; i < mNewItemLayoutIdList.size(); i++) {
//            int id = mNewItemLayoutIdList.get(i);
//            RelativeLayout layout = mListItemLayouts.get(id);
//            layout.clearAnimation();
//            if (isShow) {
//                layout.startAnimation(showAnimation);
//            } else {
//                layout.startAnimation(hideAnimation);
//            }
//        }
//        mNewItemLayoutIdList.clear();
//    }

    private GameItem newGameItem() {
        return newGameItem(0, 0);
    }

    /**
     * 随机生成一个图标元素，x和y暂时没有用到
     * @param x
     * @param y
     * @return
     */
    private GameItem newGameItem(int x, int y) {
        SecureRandom sr = new SecureRandom();
        int i = sr.nextInt(Constant.GAME_ITEM_TYPE_COUNT);
        return new GameItem(Constant.GAME_ITEM_SHOW_TYPE_NORMAL, x, y, i, mPersonNameList[i], mPersonImageArray.getResourceId(i, 0));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: ====keycode = " + keyCode);
        Log.d(TAG, "onKeyDown: ====event = " + event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            clearGameData();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 清除游戏数据
     */
    private void clearGameData() {
        if (MMKVUtil.GAME_SCORE_NUMBER > MMKVUtil.GAME_HIGHEST_SCORE_NUMBER) {
            MMKVUtil.GAME_HIGHEST_SCORE_NUMBER = MMKVUtil.GAME_SCORE_NUMBER;
        }
        MMKVUtil.GAME_SCORE_NUMBER = 0;
        MMKV.defaultMMKV().putInt(MMKVUtil.KEY_GAME_HIGHEST_SCORE_NUMBER, MMKVUtil.GAME_HIGHEST_SCORE_NUMBER);

        countdownHandler.removeMessages(0);
        scoreHandler.removeMessages(0);

        mListGameItems.clear();
    }

    /**
     * TODO 重新绘制游戏画板
     * 1、先把画板画好，只调整内部元素位置；
     * 2、三消后元素下移到底部，顶部出现新元素；
     */
    private void resetUI() {
//        mGlGameBoard.removeAllViews();
//        mListItemLayouts.clear();

        int size = mListGameItems.size();
        Log.d(TAG, "resetUI: size = " + size);
        Log.d(TAG, "resetUI: mGlGameBoard.getChildCount() = " + mGlGameBoard.getChildCount());

        for (int x = 0; x < Constant.GAME_ITEM_COLUMN_COUNT; x++) {
            for (int y = 0; y < Constant.GAME_ITEM_COLUMN_COUNT; y++) {
                int position = PositionUtil.getPositionFromXY(x, y);
//                Log.d(TAG, "resetUI: x,y,position = " + x + "," + y + "," + position);
                GameItem gameItem = mListGameItems.get(position);

                // 如果该位置标志为空，获取该位置的GameItem并更新ImageView图标
                if (gameItem.getShowStatus() == Constant.GAME_ITEM_SHOW_TYPE_NULL) {
                    // 标志位恢复正常
                    gameItem = newGameItem(x, y);
                    mListGameItems.set(position, gameItem);
                    // TODO bug:会屏闪，添加需要加载显示动画的id
//                    setNewItemLayoutId(position);
                }
//            Log.d(TAG, "resetUI: gameItem = " + gameItem.toString());

                View child = mGlGameBoard.getChildAt(position);
                RelativeLayout layout;
                if (child == null) {
                    layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_person, null);
                    int px = QMUIDisplayHelper.dp2px(this, Constant.GAME_ITEM_WIDTH);
                    layout.setLayoutParams(new RelativeLayout.LayoutParams(px, px));
                    mGlGameBoard.addView(layout, position);
                    mListItemLayouts.add(layout);
                } else {
                    layout = (RelativeLayout) child;
                }
                ImageView imageView = layout.findViewById(R.id.iv_item);
                imageView.setImageResource(gameItem.getImage());
                imageView.setPadding(0, 0, 0, 0);
                imageView.setTag(position);
                setOnClick(imageView);

//            Log.d(TAG, "resetUI:mGlGameBoard.indexOfChild(layout); = " + mGlGameBoard.indexOfChild(layout));
            }
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mGlGameBoard.getLayoutParams());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGlGameBoard.setLayoutParams(layoutParams);
    }

    private void resetClickedItemId() {
        mFirstClickedItemId = -1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SoundPoolUtil.resumeAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundPoolUtil.pauseAll();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPersonImageArray.recycle();
        clearGameData();
    }


    //=========================


    /**
     * @deprecated 废弃，生成的图标只能在尾部补充，不符合习惯
     * 后期可以简化逻辑，只更新需要更新的图标，尝试使用mGlGameBoard.removeViewAt(index);
     * 重新绘制游戏画板
     */
//    private void resetUI() {
//        mGlGameBoard.removeAllViews();
//        mListItemLayouts.clear();
//
//        int size = mListGameItems.size();
//        Log.d(TAG, "resetUI: size = " + size);
//        for (int i = 0; i < Constant.GAME_ITEM_COLUMN_COUNT * Constant.GAME_ITEM_COLUMN_COUNT; i++) {
//            GameItem gameItem;
//            if (i >= size) {
//                gameItem = newGameItem();
//                mListGameItems.add(gameItem);
//
//                // 添加需要加载显示动画的id
//                setNewItemLayoutId(i);
//            } else {
//                gameItem = mListGameItems.get(i);
//            }
////            Log.d(TAG, "resetUI: gameItem = " + gameItem.toString());
//            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_person, null);
//            int px = QMUIDisplayHelper.dp2px(this, Constant.GAME_ITEM_WIDTH);
//            layout.setLayoutParams(new RelativeLayout.LayoutParams(px, px));
//            ImageView imageView = layout.findViewById(R.id.iv_item);
//            imageView.setImageResource(gameItem.getImage());
//            imageView.setTag(i);
//
//            setOnClick(imageView);
//            mGlGameBoard.addView(layout);
//            mListItemLayouts.add(layout);
//        }
//
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mGlGameBoard.getLayoutParams());
//        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//        mGlGameBoard.setLayoutParams(layoutParams);
//
//    }
}