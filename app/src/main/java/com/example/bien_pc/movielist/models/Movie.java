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
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<String> getGenres() {
        return genres;
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

    private int year;
    private ArrayList<String> genres;
    // List of attributes e.g. Netflix, Amazon Prime
    private ArrayList<String> attributes;

    //Constructor
    public Movie(String title){
        this.title = title;
    }
}

