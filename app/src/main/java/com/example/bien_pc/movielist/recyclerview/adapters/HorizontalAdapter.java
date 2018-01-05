package com.example.bien_pc.movielist.recyclerview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Bien-PC on 03.01.2018.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    
    private final String TAG = "HorizontalAdapter";
    private Context context;
    private List<Movie> mDataList;
    private int mRowIndex = -1;

    public HorizontalAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Movie> data) {
        mDataList = data;
    }

    public void setRowIndex(int index) {
        mRowIndex = index;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView movieImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            movieImage = (ImageView) itemView.findViewById(R.id.image_movie);
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
        Log.d(TAG, "onBindViewHolder: Poster URL" + mDataList.get(position).getPosterPath());

        //Setting the image from URL
        Picasso.with(context).load(mDataList.get(position).getPosterPath()).into(holder.movieImage, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: ");

            }
        });

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
