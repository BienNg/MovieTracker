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

    public DatabaseReference getWatchlistReference(){
        return database.
                getReference("user")
                .child(username)
                .child("watchlist");
    }

    public DatabaseReference getWatchRequestReference(int movieId){
        return database
                .getReference("user")
                .child(username)
                .child("watchlist")
                .child(movieId+"")
                .child("watch_requests");
    }

    public DatabaseReference getAllChatsReference(){
        return database
                .getReference("chats");
    }

    /**
     * Creates a new child node in the firebase database that represents the chat of 2 users
     * for a specific movie.
     * @param partner1
     * @param partner2
     * @param movieId
     */
    public void createChat(String partner1, String partner2, int movieId){
        DatabaseReference databasecChatIdRef = database.getReference("chats");
        String[] chatPartners = new String[2];

        // Sort chatpartner IDs alphabetically
        if(partner1.compareTo(partner2) < 0){
            chatPartners[0] = partner1;
            chatPartners[1] = partner2;
        }else{
            chatPartners[0] = partner2;
            chatPartners[1] = partner1;
        }

        databasecChatIdRef
                .child(chatPartners[0] + chatPartners[1] + movieId)
                .child("chat_partners")
                .child(partner1)
                .setValue("male");

        databasecChatIdRef
                .child(chatPartners[0] + chatPartners[1] + movieId)
                .child("chat_partners")
                .child(partner2)
                .setValue("male");


        databasecChatIdRef
                .child(chatPartners[0] + chatPartners[1] + movieId)
                .child("movie")
                .setValue(movieId+"");
    }

    /**
     * Adding a message to the corresponding Chat in the Firebase Database.
     * The Chat ID is generated by the chat partners ids and the movie id.
     * @param partner1
     * @param partner2
     * @param movieId
     */
    public void sendChatMessage(String partner1, String partner2, int movieId, String message){
        DatabaseReference databasecChatIdRef = database.getReference("chats");
        String[] chatPartners = new String[2];

        // Sort chatpartner IDs alphabetically
        if(partner1.compareTo(partner2) < 0){
            chatPartners[0] = partner1;
            chatPartners[1] = partner2;
        }else{
            chatPartners[0] = partner2;
            chatPartners[1] = partner1;
        }

        DatabaseReference messageId =  databasecChatIdRef
                .child(chatPartners[0] + chatPartners[1] + movieId)
                .child("chat")
                .push();

        messageId.child("sender").setValue(username);
        messageId.child("message").setValue(message);
    }

    public DatabaseReference getChatByIdRef(String partner1, String partner2, int movieId){
        String[] chatPartners = new String[2];

        // Sort chatpartner IDs alphabetically
        if(partner1.compareTo(partner2) < 0){
            chatPartners[0] = partner1;
            chatPartners[1] = partner2;
        }else{
            chatPartners[0] = partner2;
            chatPartners[1] = partner1;
        }
        return database.getReference("chats")
                .child(chatPartners[0] + chatPartners[1] + movieId)
                .child("chat");
    }
}
