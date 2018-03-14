package com.example.bien_pc.movielist.helper;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    public MyFirebaseWatchRequester(int id){
        movieId = id;
        databaseWatchRequestRef = FirebaseDatabase
                .getInstance()
                .getReference("watch_requests");
        getAllUsers();
    }

    /**
     * Gets a list of all users that want to watch this film.
     * @return
     */
    private ArrayList getAllUsers(){
        // List of users that want to watch this film
        ArrayList listOfUsers = new ArrayList();
        final MyFirebaseUser myFirebaseUser = new MyFirebaseUser();

        getDatabaseWatchRequestRef().child(movieId+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(!snapshot.getKey().equals(myFirebaseUser.getUsername())){
                        Log.d(TAG, "onDataChange: users who also want to watch this ::: " + snapshot.getKey() + " : " + snapshot.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return listOfUsers;
    }
    public DatabaseReference getDatabaseWatchRequestRef() {
        return databaseWatchRequestRef;
    }
    public void setDatabaseWatchRequestRef(DatabaseReference databaseWatchRequestRef) {
        this.databaseWatchRequestRef = databaseWatchRequestRef;
    }
}
