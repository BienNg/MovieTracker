package com.example.bien_pc.movielist;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by bien on 15.01.2018.
 */

public class WatchNow {

    // Variables
    private FirebaseUser user;
    private final String TAG = "WatchNow.class";
    private int movieId;

    public WatchNow(int movieId){
        this.movieId = movieId;
    }

    public void execute(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail().replace(".", "(dot)");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: user in database ::: " + d.getKey());
                    if(!d.getKey().equals(email)){
                        boolean onUsersWatchlist = d.child("watchlist").hasChild(movieId+"");
                        Log.d(TAG, "onDataChange: user has movie on watchlist ::: " + onUsersWatchlist);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
