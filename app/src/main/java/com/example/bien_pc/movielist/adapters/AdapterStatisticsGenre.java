package com.example.bien_pc.movielist.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.models.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This adapter class handles the horizontal recyclerview which contains movies.
 *
 * Created by Bien-PC on 03.01.2018.
 */

public class AdapterStatisticsGenre extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final String TAG = "AdapterStatisticsGenre";
    private Context context;
    private int mRowIndex = -1;
    private ArrayList<String> genres, counter;

    public AdapterStatisticsGenre(Context context) {
        this.context = context;
    }

    public AdapterStatisticsGenre(Context context, HashMap<String , ArrayList<Movie>> map) {
        this.context = context;
        HashMap<String , ArrayList<Movie>> mapOfGenres = map;
        genres = new ArrayList<>();
        counter = new ArrayList<>();
        for(String genre : mapOfGenres.keySet()){
            genres.add(genre);
            counter.add(mapOfGenres.get(genre).size() + "");
        }
    }

    public void setRowIndex(int index) {
        mRowIndex = index;
    }

    /**
     * Declaring the views of each item.
     */
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView txtGenreName;
        private TextView txtGenreCounter;
        private CardView cardviewStatistics;

        public ItemViewHolder(View itemView) {
            super(itemView);
            txtGenreName = (TextView) itemView.findViewById(R.id.statistics_txt_genre);
            txtGenreCounter = (TextView) itemView.findViewById(R.id.statistics_txt_counter);
            cardviewStatistics = (CardView) itemView.findViewById(R.id.statistics_cardview);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_item_statistics_genre, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }


    /**
     * Declares the behaviour and appearance of the items.
     * @param rawHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;

        // Setting genre name of actor
        holder.txtGenreName.setText(genres.get(position));
        holder.txtGenreCounter.setText(counter.get(position));

        // Set random color for the cardview background
        int[] androidColors = context.getResources().getIntArray(R.array.colors_cardview);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        holder.cardviewStatistics.setCardBackgroundColor(randomAndroidColor);

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }
}
