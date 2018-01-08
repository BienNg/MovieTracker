package com.example.bien_pc.movielist.controller;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.MovieActivity;
import com.example.bien_pc.movielist.models.Movie;
import com.example.bien_pc.movielist.models.RequestObject;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class handles the API for the Movie Database.
 *
 * Created by Bien-PC on 04.01.2018.
 */

public class MovieDBController {
    private final String URL = "https://api.themoviedb.org/3";
    private final String QUERY_TITLE = "/search/movie?api_key=c9fa182d1bdc69a05cdaf873e0216d82&query=";
    private final String API_KEY = "?api_key=c9fa182d1bdc69a05cdaf873e0216d82";
    private final String TAG = "MovieDBController";
    private RequestObject requestObject;
    private Context mContext;

    /**
     * Constructor for requests.
     * @param requestObject
     */
    public MovieDBController(RequestObject requestObject) {
        this.requestObject = requestObject;
    }


    /**
     * Standard Constructor.
     */
    public MovieDBController(Context context){
        this.mContext = context;
    }

    /**
     * Empty Constructor
     */
    public MovieDBController(){}

    /**
     * Returns the correct URL according to the request Object
     * @return
     */
    public String getUrl(){
        String result;

        if(requestObject.getRequest().equals("Popular Movies")){
            result = generatePopularMoviesUrl();
        }
        else if(requestObject.getRequest().equals("Comedy Movies")){
            result = generateComedyMoviesUrl();
        }
        else if(requestObject.getRequest().equals("Drama Movies")){
            result = generateDramaMoviesUrl();
        }
        else if(requestObject.getRequest().equals("Horror Movies")){
            result = generateHorrorMoviesUrl();
        }
        else{
            result = "Error: Wrong Request";
        }

        return result;
    }

    /**
     * Gets movie by id and updates the MovieActivity UI.
     * @param id
     * @return
     */
    public void getMovieById(int id){

        // Creating the http url for the movie
        final String movieUrl = URL + "/movie/" + id + API_KEY;

        class RequestOperation extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {

                final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, movieUrl, null, new Response.Listener<JSONObject>() {

                            /**
                             * This is the main part of the method.
                             * Getting the Json String and pass it on to the JsonParser.
                             * @param response
                             */
                            @Override
                            public void onResponse(JSONObject response) {
                                String result = response.toString();
                                JsonParser jsonParser = new JsonParser(result);
                                Movie movie = jsonParser.getMovie();
                                MovieActivity.updateUI(movie);
                            }

                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });

                // Access the RequestQueue through your singleton class.
                MySingleton.getInstance(mContext).addToRequestQueue(jsObjRequest);
                return "";
            }
        }
        new RequestOperation().execute();

    }

    public ArrayList<Movie> getCollection(final int movieId, int collectionId){
        final String collectionURL = URL + "/collection/" + collectionId + API_KEY;

        class RequestOperation extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {

                /**
                 * Main Part: Getting the collection
                 */
                final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, collectionURL, null, new Response.Listener<JSONObject>() {

                            /**
                             * This is the main part of the method.
                             * Getting the Json String and pass it on to the JsonParser.
                             * @param response
                             */
                            @Override
                            public void onResponse(JSONObject response) {
                                String result = response.toString();
                                JsonParser jsonParser = new JsonParser(result);
                                ArrayList<Movie> list = jsonParser.getCollection(movieId);
                                if(list != null){
                                    MovieActivity.updateCollectionRV(list);
                                }
                            }

                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });

                // Access the RequestQueue through your singleton class.
                MySingleton.getInstance(mContext).addToRequestQueue(jsObjRequest);
                return "";
            }
        }
        new RequestOperation().execute();

        return null;
    }

    /**
     * Methods that generate get request urls.
     */
    public void generateImageUrl(int movieId){


    }
    private String generateMovieSearchUrl(String title){
        return URL + QUERY_TITLE +title;
    }
    private String generatePopularMoviesUrl(){
        return URL+"/movie/popular"+API_KEY;
    }
    private String generateComedyMoviesUrl(){
        return URL+"/genre/35/movies"+API_KEY;
    }
    private String generateDramaMoviesUrl(){
        return URL+"/genre/18/movies"+API_KEY;
    }
    private String generateHorrorMoviesUrl(){
        return URL+"/genre/27/movies"+API_KEY;
    }
}
