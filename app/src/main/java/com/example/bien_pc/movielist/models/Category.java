package com.example.bien_pc.movielist.models;

import java.util.ArrayList;

/**
 * This Class represents a category object
 * It Contains the name of the category and a ArrayList<String> of Movie Titles
 * that are in that category.
 * Created by Bien-PC on 04.01.2018.
 */

public class Category {
    private String title;
    private ArrayList<String> movies = new ArrayList<>();

    public Category(String title, ArrayList movies){
        this.title = title;
        this.movies = movies;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList getMovies(){
        return movies;
    }
}
