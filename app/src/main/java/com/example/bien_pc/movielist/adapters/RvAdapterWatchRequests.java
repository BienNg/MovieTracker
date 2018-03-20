package com.example.bien_pc.movielist.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bien_pc.movielist.R;

import java.util.ArrayList;

/**
 * Created by bien on 20.03.2018.
 */

public class RvAdapterWatchRequests extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Variables
    private Context mContext;
    private ArrayList<String> listWatchRequests;

    public RvAdapterWatchRequests(Context context, ArrayList<String> watchRequests){
        mContext = context;
        this.listWatchRequests = watchRequests;
    }

    /* Init. Views via this class
    */
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvRequesterName;
        public ItemViewHolder(View itemView) {
            super(itemView);
            txtvRequesterName = (TextView) itemView.findViewById(R.id.txtv_rv_item_watch_request);
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

    }

    @Override
    public int getItemCount() {
        return listWatchRequests.size();
    }
}
