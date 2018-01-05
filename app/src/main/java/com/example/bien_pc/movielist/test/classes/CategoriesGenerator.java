package com.example.bien_pc.movielist.test.classes;

import android.util.Log;

import com.example.bien_pc.movielist.models.Category;
import com.example.bien_pc.movielist.models.Movie;

import java.util.ArrayList;

/**
 * Created by Bien-PC on 04.01.2018.
 */

public class CategoriesGenerator {
    private final String TAG = "CategoriesGenerator";
    private ArrayList<Movie> popularMovies = new ArrayList<>();
    public ArrayList<Category> categories = new ArrayList<>();

    public CategoriesGenerator(ArrayList<Movie> list){
        popularMovies = list;
    }

    public ArrayList generateCategories(){
        MovieGenerator mg = new MovieGenerator();
        ArrayList<Movie> movies = mg.generateMovies();

        Log.d(TAG, "generateCategories: " + popularMovies.size());
        categories.add(new Category("Popular", popularMovies));
        categories.add(new Category("Comedy", movies));
        categories.add(new Category("Drama", movies));
        categories.add(new Category("Horror", movies));
        return categories;
    }
}
