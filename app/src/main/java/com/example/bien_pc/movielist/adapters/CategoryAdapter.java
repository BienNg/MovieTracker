package com.example.bien_pc.movielist.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bien-PC on 03.01.2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.SimpleViewHolder> {
    private final Context mContext;
    private static List<Category> mData;
    private static RecyclerView horizontalList;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        private MoviesAdapter moviesAdapter;

        public SimpleViewHolder(View view) {
            super(view);
            Context context = itemView.getContext();
            setIsRecyclable(false);
            title = (TextView) view.findViewById(R.id.category_title);
            horizontalList = (RecyclerView) itemView.findViewById(R.id.rv_horizontal_movies);
            horizontalList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            moviesAdapter = new MoviesAdapter(view.getContext());
            horizontalList.setAdapter(moviesAdapter);
        }
    }

    public CategoryAdapter(Context context, List<Category> data) {
        mContext = context;
        if (data != null)
            mData = new ArrayList<>(data);
        else mData = new ArrayList<>();
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_category, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.title.setText(mData.get(position).getTitle());
        // If category is watchlist, the notification badge should be set
        if(mData.get(position).getTitle().equals("Watchlist")){
            holder.moviesAdapter.setInWatchlist(true);
        }
        holder.moviesAdapter.setData(mData.get(position).getMovies()); // List of Strings
        holder.moviesAdapter.setRowIndex(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
