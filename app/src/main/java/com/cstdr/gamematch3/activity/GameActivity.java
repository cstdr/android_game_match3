package com.cstdr.gamematch3.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cstdr.gamematch3.R;
import com.cstdr.gamematch3.adapter.GameItemAdapter;
import com.cstdr.gamematch3.model.GameItem;
import com.cstdr.gamematch3.utils.Constant;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private GridLayout mGlGameBoard;

    private List<GameItem> mListGameItems;
    private List<RelativeLayout> mListItemLayouts;
    private GameItemAdapter mAdapter;

    private int mFirstClickedItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initView();
        initData();

    }


    private void initView() {
        mGlGameBoard = findViewById(R.id.gl_game);
        mGlGameBoard.setColumnCount(Constant.GAME_ITEM_COLUMN_COUNT);
        mGlGameBoard.setRowCount(Constant.GAME_ITEM_COLUMN_COUNT);
    }


    private void initData() {
        mListGameItems = new ArrayList<GameItem>();
        String[] personNameList = getResources().getStringArray(R.array.persons_name);
        TypedArray array = getResources().obtainTypedArray(R.array.persons);

        int j = 0;
        for (int i = 0; i < Constant.GAME_ITEM_COLUMN_COUNT * Constant.GAME_ITEM_COLUMN_COUNT; i++) {
            j = i % Constant.GAME_ITEM_TYPE_COUNT;
            GameItem gameItem = new GameItem(i, personNameList[j], array.getResourceId(j, 0));
            mListGameItems.add(gameItem);
            Log.d(TAG, "initData: gameItem = " + gameItem.toString());

            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_person, null);
            int px = QMUIDisplayHelper.dp2px(this, Constant.GAME_ITEM_WIDTH);
            layout.setLayoutParams(new RelativeLayout.LayoutParams(px, px));
            ImageView imageView = layout.findViewById(R.id.iv_item);
            imageView.setImageResource(gameItem.getImage());
            imageView.setTag(i);

            setOnClick(imageView);
            mGlGameBoard.addView(layout);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mGlGameBoard.getLayoutParams());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGlGameBoard.setLayoutParams(layoutParams);
        array.recycle();
    }

    private void setOnClick(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) v.getTag();
                Log.d(TAG, "onClick: id = " + id);

                // 如果第一次是这个id，或者刚三消后，则把这次设为第一次点击
                if (mFirstClickedItemId == id || mFirstClickedItemId == -1) {
                    mFirstClickedItemId = id;
                    // TODO 替换掉这个底色
                    v.setBackgroundResource(com.qmuiteam.qmui.R.color.qmui_config_color_gray_9);
                    return;
                } else {
                    GameItem firstGameItem = mListGameItems.get(mFirstClickedItemId);
                    GameItem secondGameItem = mListGameItems.get(id);
                    Collections.swap(mListGameItems, mFirstClickedItemId, id);

                    resetUI();
                    resetClickedItemId();
                }
            }
        });
    }

    /**
     * 重新绘制游戏画板
     */
    private void resetUI() {
        mGlGameBoard.removeAllViews();

        int j = 0;
        for (int i = 0; i < Constant.GAME_ITEM_COLUMN_COUNT * Constant.GAME_ITEM_COLUMN_COUNT; i++) {
            j = i % Constant.GAME_ITEM_TYPE_COUNT;

            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_person, null);
            int px = QMUIDisplayHelper.dp2px(this, Constant.GAME_ITEM_WIDTH);
            layout.setLayoutParams(new RelativeLayout.LayoutParams(px, px));
            ImageView imageView = layout.findViewById(R.id.iv_item);
            imageView.setImageResource(mListGameItems.get(i).getImage());
            imageView.setTag(i);

            setOnClick(imageView);
            mGlGameBoard.addView(layout);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mGlGameBoard.getLayoutParams());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGlGameBoard.setLayoutParams(layoutParams);

    }

    private void resetClickedItemId() {
        mFirstClickedItemId = -1;
    }

}