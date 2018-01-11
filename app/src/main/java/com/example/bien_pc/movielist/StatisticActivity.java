package com.example.bien_pc.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bien_pc.movielist.adapters.AdapterStatisticsGenre;
import com.example.bien_pc.movielist.helper.JsonParser;
import com.example.bien_pc.movielist.helper.MDBUrls;
import com.example.bien_pc.movielist.helper.MySingleton;
import com.example.bien_pc.movielist.models.Movie;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StatisticActivity extends AppCompatActivity {

    private final String TAG = "StatisticsActivity";
    private ArrayList<Movie> seenMovies = new ArrayList<>();

    // Views
    private RecyclerView rvGenres;
    private CardView cardViewTotal;
    TextView txtTotalCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        Intent intent = getIntent();

        // Init. Views
        rvGenres = (RecyclerView) findViewById(R.id.statistics_rv_genres);
        cardViewTotal = (CardView) findViewById(R.id.statistics_cardview_total);
        txtTotalCounter = (TextView) findViewById(R.id.statistics_txt_total_counter);

        ArrayList<String> idOfMovies = intent.getStringArrayListExtra("ID_OF_MOVIES");
        updateStatistics(idOfMovies);
    }


    /** [Genre Statistics]
     * Gets ID of movies and returns their Movie Objects in a list.
     * @param idOfMovies
     * @return
     */
    private void updateStatistics(final ArrayList<String> idOfMovies){

        // Getting the movie objects by their id.
        for (String id : idOfMovies) {
            MDBUrls mdbUrls = new MDBUrls();
            final String movieUrl = mdbUrls.getURL() + "/movie/" + id + mdbUrls.getAPI_KEY();
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

    /** [Genre Statistics]
     * Gets a list of all seen Movies and splits them into different lists of genres.
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

        txtTotalCounter.setText(movies.size()+"");

        // Setting up the RecyclerView
        setRecyclerViewGenres(mapOfGenres);
    }


    /**
     * Sets up the RecyclerView with the parameters:
     * @param mapOfGenres
     */
    private void setRecyclerViewGenres(HashMap<String , ArrayList<Movie>> mapOfGenres){
        int numberOfColumns = 3;
        rvGenres.setHasFixedSize(true);
        rvGenres.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        AdapterStatisticsGenre adapterStatisticsGenre = new AdapterStatisticsGenre(this, mapOfGenres);
        rvGenres.setAdapter(adapterStatisticsGenre);
    }
}
