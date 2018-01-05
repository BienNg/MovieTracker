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
    private ArrayList<Movie> comedyMovies = new ArrayList<>();
    private ArrayList<Movie> dramaMovies = new ArrayList<>();
    private ArrayList<Movie> horrorMovies = new ArrayList<>();
    public ArrayList<Category> categories = new ArrayList<>();

    public CategoriesGenerator(ArrayList<ArrayList<Movie>> list){
        popularMovies = list.get(0);
        comedyMovies = list.get(1);
        dramaMovies = list.get(2);
        horrorMovies = list.get(3);
    }

    public ArrayList generateCategories(){
        Log.d(TAG, "generateCategories: " + popularMovies.size());

        categories.add(new Category("Popular", popularMovies));

        if(comedyMovies != null){
            categories.add(new Category("Comedy", comedyMovies));
        }
        if (dramaMovies != null){
            categories.add(new Category("Drama", dramaMovies));
        }
        if(horrorMovies != null){
            categories.add(new Category("Horror", horrorMovies));
        }
        return categories;
    }
}
