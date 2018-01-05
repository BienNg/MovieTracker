package com.example.bien_pc.movielist.controller;

import android.util.Log;

import com.example.bien_pc.movielist.models.Movie;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class handles Json Objects.
 * <p>
 * Created by Bien-PC on 04.01.2018.
 */

public class JsonParser {
    private final String TAG = "JsonParser";
    private String jsonString;
    private JSONObject json;

    public JsonParser(String s) {
        jsonString = s;
        Log.d(TAG, "JsonParser jsonString: " + s);
        try {

            json = new JSONObject(jsonString);

        } catch (Throwable t) {
            Log.e(TAG, "Could not parse malformed JSON: \"" + jsonString + "\"");
        }
    }

    /**
     * This method reads the jsonString and returns the movie titles of popular movies.
     *
     * @return
     */
    public ArrayList<Movie> getListOfPopularMovies() {
        ArrayList<Movie> list = new ArrayList<>();
        Log.d(TAG, "getListOfPopularMovies jsonObject: " + json.toString());
        try {
            for (int i = 0; i < json.getJSONArray("results").length(); i++) {
                JSONObject movie = json.getJSONArray("results").getJSONObject(i);
                Movie movieObject = new Movie(movie.getString("title"));
                movieObject.setPosterPath(movie.getString("poster_path"));
                Log.d(TAG, "getListOfPopularMovies: poster_path is set: " + movieObject.getPosterPath());
                list.add(movieObject);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;
    }
}
