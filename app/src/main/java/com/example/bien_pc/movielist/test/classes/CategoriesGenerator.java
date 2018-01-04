package com.example.bien_pc.movielist.test.classes;

import com.example.bien_pc.movielist.models.Category;
import com.example.bien_pc.movielist.models.Movie;

import java.util.ArrayList;

/**
 * Created by Bien-PC on 04.01.2018.
 */

public class CategoriesGenerator {
    public ArrayList<Category> categories = new ArrayList<>();

    public ArrayList generateCategories(){
        MovieGenerator mg = new MovieGenerator();
        ArrayList<Movie> movies = mg.generateMovies();
        categories.add(new Category("Comedy", movies));
        categories.add(new Category("Drama", movies));
        categories.add(new Category("Horror", movies));
        return categories;
    }
}
