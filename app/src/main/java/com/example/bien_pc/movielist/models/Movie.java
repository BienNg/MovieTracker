package com.example.bien_pc.movielist.models;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Bien-PC on 03.01.2018.
 */

/**
 * This Class represents a Movie.
 */
public class Movie {

    private final String TAG = "Movie.class";
    /**
     * Attributes of a Movie
     */
    private int id;
    private int collectionId;
    private String title, year, posterPath, rating, description;
    private ArrayList<String> genres;

    // List of attributes e.g. Netflix, Amazon Prime
    private ArrayList<String> attributes;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public ArrayList<String> getGenres() {
        return genres;
    }
    public void addGenre(String genre){
        genres.add(genre);
    }
    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }
    public ArrayList<String> getAttributes() {
        return attributes;
    }
    public void setAttributes(ArrayList<String> attributes) {
        this.attributes = attributes;
    }
    public String getPosterPath() {
        return posterPath;
    }
    public void setPosterPath(String posterPath) {
        Log.d(TAG, "setPosterPath: posterPath ::: " + posterPath.length());
        if(!posterPath.equals("null")) {
            this.posterPath = "https://image.tmdb.org/t/p/w500" + posterPath;
        }else{
            this.posterPath = null;
        }
    }
    public int getId(){
        return id;
    }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getCollectionId() {
        return collectionId;
    }
    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    //Constructor
    public Movie(int id, String title){
        this.id = id;
        this.title = title;
    }
}

