package com.example.bien_pc.movielist.helper;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by bien on 14.03.2018.
 */

public class MyFirebaseWatchRequester {
    //Variables
    private final String TAG = "MyFirebaseWatchRequeste";
    private int movieId;
    private DatabaseReference databaseWatchRequestRef;
    private ArrayList<String> userIdList = new ArrayList<>();

    public MyFirebaseWatchRequester(int id){
        movieId = id;
        databaseWatchRequestRef = FirebaseDatabase
                .getInstance()
                .getReference("watch_requests");
    }

    private void notifiyUsers(){
        Log.d(TAG, "notifyUsers: started.");
        MyFirebaseUser myFirebaseUser = new MyFirebaseUser();
        for(String userId : userIdList){
            DatabaseReference database = FirebaseDatabase
                    .getInstance()
                    .getReference("user")
                    .child(userId)
                    .child("watchlist")
                    .child(movieId+"")
                    .child("watch_requests")
                    .child(myFirebaseUser.getUsername());
            database.setValue("true");
            Log.d(TAG, "execute: request for movie [" + movieId + "] was sent to user " + userId);
        }

    }

    /**
     * Gets a list of all users that want to watch this film.
     * @return
     */
    public void execute(){
        final MyFirebaseUser myFirebaseUser = new MyFirebaseUser();

        getDatabaseWatchRequestRef().child(movieId+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int counter = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    counter++;
                    if(!snapshot.getKey().equals(myFirebaseUser.getUsername())){
                        Log.d(TAG, "onDataChange: user " + snapshot.getKey() + " also wants to watch the movie.");
                        userIdList.add(snapshot.getKey());
                    }
                    if(counter == dataSnapshot.getChildrenCount()){
                        notifiyUsers();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    public DatabaseReference getDatabaseWatchRequestRef() {
        return databaseWatchRequestRef;
    }
    public void setDatabaseWatchRequestRef(DatabaseReference databaseWatchRequestRef) {
        this.databaseWatchRequestRef = databaseWatchRequestRef;
    }
}
