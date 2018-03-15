package com.example.bien_pc.movielist.helper;

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
        if (mAuth.getCurrentUser() != null){
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
    public void addMovieToSeen(int id, String title){
        DatabaseReference databaseTitleRef = database
                .getReference("user")
                .child(username)
                .child("movies")
                .child(id+"")
                .child("title");
        databaseTitleRef.setValue(title);
    }

    /**
     * Removes movie from seen database with the given id
     * @param id
     */
    public void removeMovieFromSeen(int id){
        DatabaseReference databaseMovieRef = database.
                getReference("user")
                .child(username)
                .child("movies")
                .child(id+"");
        databaseMovieRef.setValue(null);
    }

    /**
     * Sets the favorite attribute to true
     * @param id
     */
    public void favMovie(int id, String title){

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

    /**
     * Unfavs movie from the databse
     * @param id
     */
    public void unfavMovie(int id){
        DatabaseReference databaseFavRef = database
                .getReference("user")
                .child(username)
                .child("movies")
                .child(id+"")
                .child("favorite");
        databaseFavRef.setValue(null);
    }

    /**
     * Returns the databasereference to all movies of the user.
     * @return
     */
    public DatabaseReference getAllMoviesReference(){
        return database
                .getReference("user")
                .child(username)
                .child("movies");
    }

    /**
     * Returns the databasereference to swiped movies.
     * @return
     */
    public DatabaseReference getSwipedMoviesReference(){
        return database
                .getReference("user")
                .child(username)
                .child("swiped");
    }

    public DatabaseReference getWatchedReference(){
        return database
                .getReference("user")
                .child(username)
                .child("watchlist");
    }
}