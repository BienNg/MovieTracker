package com.example.bien_pc.movielist.recyclerview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.models.Movie;

import java.util.List;

/**
 * Created by Bien-PC on 03.01.2018.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Movie> mDataList;
    private int mRowIndex = -1;

    public HorizontalAdapter() {
    }

    public void setData(List<Movie> data) {
        mDataList = data;
    }

    public void setRowIndex(int index) {
        mRowIndex = index;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView movieImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            movieImage = (TextView) itemView.findViewById(R.id.image_movie);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
        ItemViewHolder holder = (ItemViewHolder) rawHolder;
        holder.movieImage.setText(mDataList.get(position).getTitle());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
