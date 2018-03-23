package com.example.bien_pc.movielist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.activities.ChatActivity;

import java.util.ArrayList;

/**
 * Created by bien on 23.03.2018.
 */

public class RvAdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Variables
    private Context mContext;
    private ArrayList<String> listMessages;

    public RvAdapterChat(Context context, ArrayList<String> listMessages){
        mContext = context;
        this.listMessages = listMessages;
    }

    /* Init. Views via this class
    */
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvMessage;
        public ItemViewHolder(View itemView) {
            super(itemView);
            txtvMessage = (TextView) itemView.findViewById(R.id.rvItem_chatMessage);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_chat_message, parent, false);
        RvAdapterChat.ItemViewHolder holder = new RvAdapterChat.ItemViewHolder(itemView);
        return holder;
    }

    /**
     * Declares the behaviour and appearance of the items.
     * @param rawHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
        final RvAdapterChat.ItemViewHolder holder = (RvAdapterChat.ItemViewHolder) rawHolder;
        holder.txtvMessage.setText(listMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }
}
