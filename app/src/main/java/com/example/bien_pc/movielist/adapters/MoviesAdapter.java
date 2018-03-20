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

import com.example.bien_pc.movielist.activities.MovieActivity;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.helper.MyFirebaseUser;
import com.example.bien_pc.movielist.models.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
    private boolean inWatchlist = false;

    public MoviesAdapter(Context context) {
        this.context = context;
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        mDataList = movies;
    }

    public void setInWatchlist(boolean b){
        inWatchlist = b;
    }
    public void setData(List<Movie> data) {
        mDataList = data;
    }

    public void setRowIndex(int index) {
        mRowIndex = index;
    }

    /**
     * (only) INIT. Views of the the items in the RecyclerView
     */
    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView movieImage;
        private TextView txtvBadgeNotification;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // Init. Views
            movieImage = (ImageView) itemView.findViewById(R.id.image_movie);
            txtvBadgeNotification = (TextView) itemView.findViewById(R.id.txtv_movie_badge);
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
     * Setting up the behaviour and appearance of the items.
     * @param rawHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;

        //Setting up the ImageView from URL
        if(mDataList.get(position).getPosterPath() != null){
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

        // Setting up badge notificytion of the movie if movie is in watchlist
        if(inWatchlist){
            MyFirebaseUser myFirebaseUser = new MyFirebaseUser();
            DatabaseReference databaseReference = myFirebaseUser.getWatchlistReference().child(mDataList.get(position).getId()+"");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("watch_requests")){
                        int counterWatchRequests = 0;
                        for(DataSnapshot s : dataSnapshot.child("watch_requests").getChildren()){
                            counterWatchRequests++;
                            Log.d(TAG, "onDataChange: user " + s.getKey() + " requests to watch the movie.");
                            Log.d(TAG, "onDataChange: Watch Counter ::: " + counterWatchRequests);
                        }
                        holder.txtvBadgeNotification.setText(counterWatchRequests+"");
                        holder.txtvBadgeNotification.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        // Setting click listener for the movie item
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
