package com.example.bien_pc.movielist.models;

import java.util.ArrayList;

/**
 * Created by Bien-PC on 03.01.2018.
 */

/**
 * This Class represents a Movie.
 */
public class Movie {
    /**
     * Attributes of a Movie
     */
    private int id;
    private String title;
    private String year;
    private ArrayList<String> genres;
    private String posterPath;

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
        this.posterPath = "https://image.tmdb.org/t/p/w500"+posterPath;
    }
    public int getId(){
        return id;
    }

    //Constructor
    public Movie(int id, String title){
        this.id = id;
        this.title = title;
    }
}

