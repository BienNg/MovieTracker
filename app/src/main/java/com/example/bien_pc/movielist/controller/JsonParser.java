package com.example.bien_pc.movielist.controller;

import android.util.Log;

import com.example.bien_pc.movielist.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class reads Json Objects and returns the wanted information.
 * <p>
 * Created by Bien-PC on 04.01.2018.
 */

public class JsonParser {

    // Attributes
    private final String TAG = "JsonParser";
    private String jsonString;
    private JSONObject json;
    private ArrayList<Movie> list;

    /**
     * Constructor always needs a json string @param s and returns the wanted infromation.
     * @param s
     */
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
     * This method reads the jsonString and returns the movie titles the
     * wanted list e.g. popular movies, horror movies etc.
     *
     * @return
     */
    public ArrayList<Movie> getList() {
        list = new ArrayList<>();
        try {
            for (int i = 0; i < json.getJSONArray("results").length(); i++) {
                JSONObject movie = json.getJSONArray("results").getJSONObject(i);
                Movie movieObject = new Movie(movie.getInt("id"),movie.getString("title"));
                movieObject.setPosterPath(movie.getString("poster_path"));
                list.add(movieObject);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * This reads the movie http url and returns the equivalent movie object.
     * @return
     */
    public Movie getMovie(){
        Log.d(TAG, "getMovie: started");

        try {
            // Getting id and title and creating the movie object
            int id = json.getInt("id");
            String title = json.getString("title");
            Movie movie = new Movie(id, title);

            //Getting year and genres of the movie
            String year = json.getString("release_date");
            ArrayList<String> genres = new ArrayList<>();
            JSONArray jsonArray = json.getJSONArray("genres");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                genres.add(jo.getString("name"));
            }
            //Setting year and genre
            movie.setGenres(genres);
            movie.setYear(year);
            return movie;
        } catch (JSONException e) {
            Log.e(TAG, "getMovie: some error idk.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns an list of image urls.
     * @return
     */
    public ArrayList<String> getImageUrls(){
        ArrayList<String> imageUrls = new ArrayList<>();
        try {
            for (int i = 0; i < json.getJSONArray("backdrops").length(); i++) {
                JSONObject image = json.getJSONArray("backdrops").getJSONObject(i);
                String imageUrl = "https://image.tmdb.org/t/p/w500" + image.getString("file_path");
                imageUrls.add(imageUrl);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrls;
    }
}
