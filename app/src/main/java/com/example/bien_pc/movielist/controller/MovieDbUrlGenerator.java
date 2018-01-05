package com.example.bien_pc.movielist.controller;

import com.example.bien_pc.movielist.models.RequestObject;

/**
 * This class handles the API for the Movie Database.
 *
 * Created by Bien-PC on 04.01.2018.
 */

public class MovieDbUrlGenerator {
    private final String URL = "https://api.themoviedb.org/3";
    private final String QUERY_TITLE = "/search/movie?api_key=c9fa182d1bdc69a05cdaf873e0216d82&query=";
    private final String API_KEY = "?api_key=c9fa182d1bdc69a05cdaf873e0216d82";
    private final String POSTER_PATH = "http://image.tmdb.org/t/p/w300";
    private final String TAG = "MovieDbUrlGenerator";
    private RequestObject requestObject;

    public MovieDbUrlGenerator(RequestObject requestObject) {
        this.requestObject = requestObject;
    }

    /**
     * Returns the correct URL according to the request Object
     * @return
     */
    public String getRequestUrl(){
        String result;

        if(requestObject.getRequest().equals("Popular Movies")){
            result = generatePopularMoviesUrl();
        }else{
            result = "Error: Wrong Request";
        }

        return result;
    }

    private String generateMovieSearchUrl(String title){
        return URL + QUERY_TITLE +title;
    }
    private String generatePopularMoviesUrl(){
        return URL+"/movie/popular"+API_KEY;
    }
}
