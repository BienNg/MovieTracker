package com.example.bien_pc.movielist.test.classes;

/**
 * Created by Bien-PC on 03.01.2018.
 */

import com.example.bien_pc.movielist.models.Movie;

import java.util.ArrayList;

/**
 * This class generates Movies for testing purposes.
 */
public class MovieGenerator {

    public static ArrayList generateMovies(){
        ArrayList<Movie> movies = new ArrayList<>();

        movies.add(new Movie("The Wolf of Wallstreet"));
        movies.add(new Movie("Suicide Squad"));
        movies.add(new Movie("Whiplash"));
        movies.add(new Movie("La La Land"));
        movies.add(new Movie("IT"));
        movies.add(new Movie("Zombieland"));

        return movies;
    }
}
