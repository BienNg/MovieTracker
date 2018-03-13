package com.example.bien_pc.movielist.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by bien on 13.03.2018.
 */

public class MyFirebaseUser {
    private String username;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser firebaseUser;

    public MyFirebaseUser(){
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null){
            username = mAuth.getCurrentUser().getEmail().replace(".", "(dot)");
            database = FirebaseDatabase.getInstance();
            firebaseUser = mAuth.getCurrentUser();
        }
    }

    // Getter and Setter
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public FirebaseAuth getAuth() {
        return mAuth;
    }
    public void setAuth(FirebaseAuth auth) {
        mAuth = auth;
    }
    public FirebaseDatabase getDatabase() {
        return database;
    }
    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }
    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }
    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    /**
     * Adds the movie with the @param id to the database of the user.
     * @param id
     */
    public void addMovie(int id, String title){

        // Adding the movie and setting the title
        DatabaseReference databaseTitleRef = database
                .getReference("user")
                .child(username)
                .child("movies")
                .child(id+"")
                .child("title");
        databaseTitleRef.setValue(title);

        // Setting favorite attribute to true
        DatabaseReference databaseFavRef = database
                .getReference("user")
                .child(username)
                .child("movies")
                .child(id+"")
                .child("favorite");
        databaseFavRef.setValue("true");
    }
}
