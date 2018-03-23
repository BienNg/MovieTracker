package com.example.bien_pc.movielist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.activities.ChatActivity;

import java.util.ArrayList;

/**
 * Created by bien on 20.03.2018.
 */

public class RvAdapterWatchRequests extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Variables
    private Context mContext;
    private ArrayList<String> listWatchRequests;
    private int movieId;

    public RvAdapterWatchRequests(Context context, ArrayList<String> watchRequests, int movieId){
        mContext = context;
        this.listWatchRequests = watchRequests;
        this.movieId = movieId;
    }

    /* Init. Views via this class
    */
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvRequesterName;
        private ImageView imageUserIcon;
        public ItemViewHolder(View itemView) {
            super(itemView);
            txtvRequesterName = (TextView) itemView.findViewById(R.id.rvItem_watchRequest_userName);
            imageUserIcon = (ImageView) itemView.findViewById(R.id.rvItem_watchRequest_image_user);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_watch_request, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    /**
     * Declares the behaviour and appearance of the items.
     * @param rawHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;
        holder.txtvRequesterName.setText(listWatchRequests.get(position));
        holder.imageUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("ChatPartner", holder.txtvRequesterName.getText().toString());
                intent.putExtra("MovieID", movieId);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listWatchRequests.size();
    }
}
