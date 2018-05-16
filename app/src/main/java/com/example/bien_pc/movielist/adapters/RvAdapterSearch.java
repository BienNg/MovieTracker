package com.example.bien_pc.movielist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.activities.MovieActivity;
import com.example.bien_pc.movielist.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by bien on 24.03.2018.
 */

public class RvAdapterSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Variables
    private Context context;
    private ArrayList<Movie> listSearchResults;
    private final String TAG = "TAG:RvAdapterSearch";

    public RvAdapterSearch(Context context, ArrayList<Movie> listSearches){
        this.context = context;
        this.listSearchResults = listSearches;
    }

    /* Init. Views via this class
    */
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageMoviePoster;
        public ItemViewHolder(View itemView) {
            super(itemView);
            imageMoviePoster = (ImageView) itemView.findViewById(R.id.act_search_rvItem_movie_poster);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_searches, parent, false);
        RvAdapterSearch.ItemViewHolder holder = new RvAdapterSearch.ItemViewHolder(itemView);
        return holder;
    }

    /**
     * Declares the behaviour and appearance of the items.
     * @param rawHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final RvAdapterSearch.ItemViewHolder holder = (RvAdapterSearch.ItemViewHolder) rawHolder;
        //Setting up the ImageView from URL
        if(listSearchResults.get(position).getPosterPath() != null){
            Picasso.with(context).load(listSearchResults.get(position).getPosterPath()).into(holder.imageMoviePoster, new Callback() {
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
            holder.imageMoviePoster.setImageDrawable(null);
        }
        // Setting click listener for the movie item
        holder.imageMoviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieActivity.class);
                intent.putExtra("ID", listSearchResults.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSearchResults.size();
    }
}