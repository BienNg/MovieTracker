package com.example.bien_pc.movielist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bien_pc.movielist.activities.MovieActivity;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter class handles the horizontal recyclerview which contains movies.
 *
 * Created by Bien-PC on 03.01.2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    
    private final String TAG = "MoviesAdapter";
    private Context context;
    private List<Movie> mDataList = new ArrayList<>();
    private int mRowIndex = -1;

    public MoviesAdapter(Context context) {
        this.context = context;
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        mDataList = movies;
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
            setIsRecyclable(false);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_item_movie, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }


    /**
     * Declares the behaviour appearance of the items.
     * @param rawHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;

        if(mDataList.get(position).getPosterPath() != null){
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
        }else{
            holder.movieImage.setImageDrawable(null);
        }

        //Setting click listener for the movie item
        holder.movieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieActivity.class);
                intent.putExtra("ID", mDataList.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
