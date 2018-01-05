package com.example.bien_pc.movielist.controller;

/**
 * This class handles the API for the Movie Database.
 *
 * Created by Bien-PC on 04.01.2018.
 */

public class MovieDbUrlGenerator {
    private final String URL = "https://api.themoviedb.org/3";
    private final String QUERY_TITLE = "/search/movie?api_key=c9fa182d1bdc69a05cdaf873e0216d82&query=";
    private final String POSTER_PATH = "http://image.tmdb.org/t/p/w300";
    private final String TAG = "MovieDbUrlGenerator";

    public String generateGetRequestUrl(String title){
        return URL + QUERY_TITLE +title;
    }
}
