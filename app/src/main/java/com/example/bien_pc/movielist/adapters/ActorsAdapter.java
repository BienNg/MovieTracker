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

import com.example.bien_pc.movielist.ActorActivity;
import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.models.Actor;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter class handles the horizontal recyclerview which contains movies.
 *
 * Created by Bien-PC on 03.01.2018.
 */

public class ActorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final String TAG = "ActorsAdapter";
    private Context context;
    private List<Actor> mDataList = new ArrayList<>();
    private int mRowIndex = -1;

    public ActorsAdapter(Context context) {
        this.context = context;
    }

    public ActorsAdapter(Context context, ArrayList<Actor> actors) {
        this.context = context;
        mDataList = actors;
    }

    public void setRowIndex(int index) {
        mRowIndex = index;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView actorImage;
        private TextView txtNameActor;

        public ItemViewHolder(View itemView) {
            super(itemView);
            actorImage = (ImageView) itemView.findViewById(R.id.item_actor_image);
            txtNameActor = (TextView) itemView.findViewById(R.id.item_actor_name);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_item_actor, parent, false);
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

        // Setting image of item
        if(mDataList.get(position).getProfilePath() != null){
            //Setting the image from URL
            Picasso.with(context).load(mDataList.get(position).getProfilePath()).into(holder.actorImage, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: ");
                }

                @Override
                public void onError() {
                    Log.d(TAG, "onError: ");

                }
            });
        }

        // Setting name of actor
        holder.txtNameActor.setText(mDataList.get(position).getName());

        //Setting click listener for the movie item
        holder.actorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActorActivity.class);
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
