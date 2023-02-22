package com.cstdr.gamematch3.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cstdr.gamematch3.R;
import com.cstdr.gamematch3.adapter.GameItemAdapter;
import com.cstdr.gamematch3.model.GameItem;
import com.cstdr.gamematch3.utils.Constant;
import com.cstdr.gamematch3.utils.MatchUtil;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private GridLayout mGlGameBoard;

    private List<GameItem> mListGameItems;
    private List<RelativeLayout> mListItemLayouts;
    private List<Integer> mNeedAnimLayoutIdList;
    private List<Integer> mNewItemLayoutIdList;

    private GameItemAdapter mAdapter;

    private int mFirstClickedItemId = -1;
    private String[] mPersonNameList;
    private TypedArray mPersonImageArray;
    private Animation showAnimation;
    private Animation hideAnimation;

    private Handler resetUIHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            resetUI();
            // 三消后，新添加的图标动画显示入场
            startNewItemAnim(true);

            List<Integer> needMatchedList = MatchUtil.startMatchAll(mListGameItems);
            if (needMatchedList.size() > 0) {
                setNeedAnimIds(needMatchedList);
                startAnim(false);

                resetUIHandler.sendEmptyMessageDelayed(0, 500);
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initView();
        initAnimation();
        initData();

        resetUI();
        startNewItemAnim(true);
    }


    private void initView() {
        mGlGameBoard = findViewById(R.id.gl_game);
        mGlGameBoard.setColumnCount(Constant.GAME_ITEM_COLUMN_COUNT);
        mGlGameBoard.setRowCount(Constant.GAME_ITEM_COLUMN_COUNT);
    }

    private void initAnimation() {
        showAnimation = new AlphaAnimation(0, 1);
        showAnimation.setDuration(500);
        showAnimation.setFillAfter(true);
        showAnimation.setFillBefore(false);

        hideAnimation = new AlphaAnimation(1, 0);
        hideAnimation.setDuration(500);
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
    }

    private void setOnClick(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) v.getTag();
                Log.d(TAG, "onClick: id = " + id);

                // 如果第一次是这个id，或者刚三消后，则把这次设为第一次点击
                if (mFirstClickedItemId == -1) {
                    mFirstClickedItemId = id;
                    // TODO 替换掉这个底色
                    v.setBackgroundResource(com.qmuiteam.qmui.R.color.qmui_config_color_gray_9);
                    return;
                } else {
                    setNeedAnimId(mFirstClickedItemId);
                    setNeedAnimId(id);
                    startAnim(false);

                    Collections.swap(mListGameItems, mFirstClickedItemId, id);
                    resetUI();

//                    setNeedAnimId(mFirstClickedItemId);
//                    setNeedAnimId(id);
//                    startAnim(true);

                    List<Integer> itemIdList = new ArrayList<Integer>();
                    itemIdList.add(mFirstClickedItemId);
                    itemIdList.add(id);
                    List<Integer> needMatchedList = MatchUtil.startMatch(mListGameItems, itemIdList);
                    setNeedAnimIds(needMatchedList);
                    startAnim(false);

                    resetUIHandler.sendEmptyMessageDelayed(0, 500);

                    resetClickedItemId();
                }
            }
        });
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

    private void setNewItemLayoutId(int id) {
        mNewItemLayoutIdList.add(id);
    }

    private void startNewItemAnim(boolean isShow) {
        for (int i = 0; i < mNewItemLayoutIdList.size(); i++) {
            int id = mNewItemLayoutIdList.get(i);
            RelativeLayout layout = mListItemLayouts.get(id);
            layout.clearAnimation();
            if (isShow) {
                layout.startAnimation(showAnimation);
            } else {
                layout.startAnimation(hideAnimation);
            }
        }
        mNewItemLayoutIdList.clear();
    }

    private GameItem newGameItem() {
        SecureRandom sr = new SecureRandom();
        int i = sr.nextInt(Constant.GAME_ITEM_TYPE_COUNT);
        return new GameItem(i, mPersonNameList[i], mPersonImageArray.getResourceId(i, 0));
    }

    /**
     * TODO 简化逻辑，只更新需要更新的图标，尝试使用mGlGameBoard.removeViewAt(index);
     * 重新绘制游戏画板
     */
    private void resetUI() {
        mGlGameBoard.removeAllViews();
        mListItemLayouts.clear();

        int size = mListGameItems.size();
        Log.d(TAG, "resetUI: size = " + size);
        for (int i = 0; i < Constant.GAME_ITEM_COLUMN_COUNT * Constant.GAME_ITEM_COLUMN_COUNT; i++) {
            GameItem gameItem;
            if (i >= size) {
                gameItem = newGameItem();
                mListGameItems.add(gameItem);

                // 添加需要加载显示动画的id
                setNewItemLayoutId(i);
            } else {
                gameItem = mListGameItems.get(i);
            }
//            Log.d(TAG, "resetUI: gameItem = " + gameItem.toString());
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_person, null);
            int px = QMUIDisplayHelper.dp2px(this, Constant.GAME_ITEM_WIDTH);
            layout.setLayoutParams(new RelativeLayout.LayoutParams(px, px));
            ImageView imageView = layout.findViewById(R.id.iv_item);
            imageView.setImageResource(gameItem.getImage());
            imageView.setTag(i);

            setOnClick(imageView);
            mGlGameBoard.addView(layout);
            mListItemLayouts.add(layout);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mGlGameBoard.getLayoutParams());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGlGameBoard.setLayoutParams(layoutParams);

    }

    private void resetClickedItemId() {
        mFirstClickedItemId = -1;
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPersonImageArray.recycle();
    }
}