package com.cstdr.gamematch3.adapter;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cstdr.gamematch3.R;
import com.cstdr.gamematch3.model.GameItem;

import java.util.List;

public class GameItemAdapter extends RecyclerView.Adapter<GameItemAdapter.ViewHolder> {
    private final Context mContext;
    private List<GameItem> mList;

    public GameItemAdapter(Context context, List<GameItem> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_person, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.iv_item);
        }


        public void bindData(int position) {
            GameItem gameItem = mList.get(position);
            int gameItemImage = gameItem.getImage();
            Glide.with(mContext).load(gameItemImage).into(image);
        }
    }
}
