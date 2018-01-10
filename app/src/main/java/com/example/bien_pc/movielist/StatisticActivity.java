package com.example.bien_pc.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.controller.JsonParser;
import com.example.bien_pc.movielist.controller.MovieDBController;
import com.example.bien_pc.movielist.controller.MySingleton;
import com.example.bien_pc.movielist.models.Movie;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StatisticActivity extends AppCompatActivity {

    private final String TAG = "StatisticsActivity";
    private ArrayList<Movie> seenMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        Intent intent = getIntent();
        ArrayList<String> idOfMovies = intent.getStringArrayListExtra("ID_OF_MOVIES");
        updateStatistics(idOfMovies);
    }


    /**
     * Gets ID of movies and returns their Movie Objects in a list.
     * @param idOfMovies
     * @return
     */
    private void updateStatistics(final ArrayList<String> idOfMovies){

        // Getting the movie objects by their id.
        for (String id : idOfMovies) {
            MovieDBController movieDBController = new MovieDBController();
            final String movieUrl = movieDBController.getURL() + "/movie/" + id + movieDBController.getAPI_KEY();
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, movieUrl, null, new Response.Listener<JSONObject>() {

                        /**
                         * This is the main part of the method.
                         * Getting the Json String and pass it on to the JsonParser.
                         *
                         * @param response
                         */
                        @Override
                        public void onResponse(JSONObject response) {
                            String result = response.toString();
                            JsonParser jsonParser = new JsonParser(result);
                            Movie movie = jsonParser.getMovie();
                            seenMovies.add(movie);
                            if(seenMovies.size() == idOfMovies.size()){
                                splitMoviesInGenres(seenMovies);
                            }
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            // Access the RequestQueue through your singleton class.
            MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        }
    }

    /**
     * Gets a list of Movies and splits them into different lists of genres.
     * @param movies
     */
    private void splitMoviesInGenres(ArrayList<Movie> movies){
        // Init. Hashmap that contains the genres and their movies
        HashMap<String , ArrayList<Movie>> mapOfGenres = new HashMap<>();
        
        // Seperating the movies into their genres
        for(Movie movie : movies){
            for(String genre : movie.getGenres()){
                // Check if genre already exists
                if(mapOfGenres.containsKey(genre)){
                    mapOfGenres.get(genre).add(movie);
                }else{
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(movie);
                    mapOfGenres.put(genre, list);
                }
            }
        }

        String[] listViewItems = new String[mapOfGenres.size()];
        int counter = 0;
        // Printing the hashmap
        for (String genre: mapOfGenres.keySet()){
            ArrayList<Movie> value = mapOfGenres.get(genre);
            for(Movie movie : value){
                Log.d(TAG, "splitMoviesInGenres: " + genre + " : " + movie.getTitle());
            }

            String listviewItemText = genre + " : " + mapOfGenres.get(genre).size();
            listViewItems[counter] = listviewItemText;
            counter++;
        }

        // Inflating the listview
        ListAdapter listAdapter = new ArrayAdapter<String>(this, R.layout.item_listview_statistics, listViewItems);
        ListView listViewStatistics = (ListView) findViewById(R.id.statistics_listview);
        listViewStatistics.setAdapter(listAdapter);
    }
}
