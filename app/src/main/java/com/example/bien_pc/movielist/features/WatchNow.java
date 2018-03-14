package com.example.bien_pc.movielist.features;

import android.util.Log;

import com.example.bien_pc.movielist.models.MyFirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This class executes the Watch-Now-Feature.
 * By clicking on the watch now button of a movie, every user who has the same movie
 * on his watchlist will be notified and gets a request to start a chat conversation.
 * Created by bien on 15.01.2018.
 */

public class WatchNow {

    // Variables
    private MyFirebaseUser mMyFirebaseUser;
    private final String TAG = "WatchNow.class";
    private int movieId;

    public WatchNow(int movieId){
        this.movieId = movieId;
    }

    public void execute(){
        Log.d(TAG, "execute: watch now executed");
        mMyFirebaseUser = new MyFirebaseUser();
        DatabaseReference databaseWatchRequestRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("watch_requests");

        databaseWatchRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: user in database ::: " + d.getKey());
                    if(d.getKey().equals(movieId+"")){
                        Log.d(TAG, "onDataChange ::: " + movieId + " is aleady in the watch request list.");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
